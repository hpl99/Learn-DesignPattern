package com.savebite.teaching;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternConsistencyTest {

    @Test
    @DisplayName("Verification: patterns.json contains exactly 23 GoF patterns with valid file paths")
    void testPatternRegistryCompleteness() {
        List<Map<String, Object>> patterns = PatternRegistry.getAllPatterns();
        assertThat(patterns).hasSize(23);

        int verifiedFileCount = 0;

        for (Map<String, Object> p : patterns) {
            String id = (String) p.get("id");
            String name = (String) p.get("name");
            String category = (String) p.get("category");
            String intent = (String) p.get("intent");
            String vivaQ = (String) p.get("vivaQuestion");
            String vivaA = (String) p.get("vivaAnswer");
            String proofEndpoint = (String) p.get("proofEndpoint");

            assertThat(id).isNotBlank();
            assertThat(name).isNotBlank();
            assertThat(category).isIn("Creational", "Structural", "Behavioral");
            assertThat(intent).isNotBlank();
            assertThat(vivaQ).isNotBlank();
            assertThat(vivaA).isNotBlank();
            assertThat(proofEndpoint).isNotBlank();

            @SuppressWarnings("unchecked")
            List<String> filePaths = (List<String>) p.get("filePaths");
            assertThat(filePaths).isNotEmpty();

            for (String relativePath : filePaths) {
                Path path = Path.of(relativePath);
                if (!Files.exists(path)) {
                    path = Path.of("savebite").resolve(relativePath);
                }
                assertThat(Files.exists(path))
                    .withFailMessage("Pattern file not found on disk: " + relativePath)
                    .isTrue();
                verifiedFileCount++;
            }

            // Verify live proof returns data for this pattern
            Map<String, Object> proof = PatternRegistry.generateLiveProof(id);
            assertThat(proof).isNotEmpty();
            assertThat(proof).doesNotContainKey("error");
        }

        assertThat(verifiedFileCount).isGreaterThanOrEqualTo(23);
    }
}

