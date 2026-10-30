package com.savebite.controller;

import com.savebite.teaching.PatternRegistry;
import com.savebite.teaching.PatternTracer;
import com.savebite.teaching.TraceEvent;
import io.javalin.http.Context;
import io.javalin.http.sse.SseClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeachingController {

    public void streamEvents(SseClient client) {
        PatternTracer.getInstance().addSseClient(client);
    }

    public void getAllPatterns(Context ctx) {
        ctx.json(PatternRegistry.getAllPatterns());
    }

    public void getPattern(Context ctx) {
        String id = ctx.pathParam("id");
        Optional<Map<String, Object>> pattern = PatternRegistry.getPattern(id);
        if (pattern.isPresent()) {
            ctx.json(pattern.get());
        } else {
            ctx.status(404).json(Map.of("error", "Pattern not found: " + id));
        }
    }

    public void getPatternCode(Context ctx) {
        String id = ctx.pathParam("id");
        Optional<Map<String, Object>> pattern = PatternRegistry.getPattern(id);
        if (pattern.isEmpty()) {
            ctx.status(404).json(Map.of("error", "Pattern not found: " + id));
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> filePaths = (List<String>) pattern.get().get("filePaths");
        if (filePaths == null || filePaths.isEmpty()) {
            ctx.status(404).json(Map.of("error", "No code file paths associated with " + id));
            return;
        }

        String primaryPath = filePaths.get(0);
        Path path = Path.of(primaryPath);

        // Fallback search if current working directory is slightly different
        if (!Files.exists(path)) {
            path = Path.of("savebite").resolve(primaryPath);
        }

        try {
            if (Files.exists(path)) {
                String code = Files.readString(path, StandardCharsets.UTF_8);
                ctx.json(Map.of(
                    "pattern", id,
                    "filePath", primaryPath,
                    "fileName", path.getFileName().toString(),
                    "code", code
                ));
            } else {
                ctx.status(404).json(Map.of("error", "Source file not found on disk: " + primaryPath));
            }
        } catch (IOException e) {
            ctx.status(500).json(Map.of("error", "Error reading source file: " + e.getMessage()));
        }
    }

    public void getPatternProof(Context ctx) {
        String id = ctx.pathParam("id");
        Map<String, Object> proof = PatternRegistry.generateLiveProof(id);
        ctx.json(proof);
    }

    public void getTraceHistory(Context ctx) {
        ctx.json(PatternTracer.getInstance().getRecentEvents());
    }

    public void exportTraceText(Context ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("                SAVEBITE GOF PATTERN EXECUTION TRACE             \n");
        sb.append("=================================================================\n\n");

        for (TraceEvent ev : PatternTracer.getInstance().getRecentEvents()) {
            sb.append(ev.toString()).append("\n");
        }

        ctx.contentType("text/plain")
           .header("Content-Disposition", "attachment; filename=\"savebite-pattern-trace.txt\"")
           .result(sb.toString());
    }

    public void clearTrace(Context ctx) {
        PatternTracer.getInstance().clear();
        ctx.json(Map.of("status", "cleared"));
    }
}

