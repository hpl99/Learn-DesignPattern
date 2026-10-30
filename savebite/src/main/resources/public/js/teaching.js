// SaveBite Teaching Mode Engine & Pattern Trace Streamer
import { fetchPatterns, fetchPatternCode, fetchPatternProof } from './api.js';

let patternsCache = [];
let exploredSet = new Set(JSON.parse(localStorage.getItem('savebite_explored_patterns') || '[]'));
let activePattern = null;
let currentTab = 'overview';
let sseSource = null;

export function initTeachingMode() {
  const toggle = document.getElementById('teaching-mode-toggle');
  const savedState = localStorage.getItem('savebite_teaching_mode');
  const isEnabled = savedState !== null ? savedState === 'true' : true;

  if (toggle) {
    toggle.checked = isEnabled;
    toggle.addEventListener('change', (e) => {
      setTeachingMode(e.target.checked);
    });
  }
  setTeachingMode(isEnabled);
  initSseTrace();
  loadAllPatterns();
}

export function setTeachingMode(enabled) {
  localStorage.setItem('savebite_teaching_mode', enabled);
  if (enabled) {
    document.body.classList.remove('body-no-teaching');
  } else {
    document.body.classList.add('body-no-teaching');
  }
}

async function loadAllPatterns() {
  try {
    patternsCache = await fetchPatterns();
    updateExploredTracker();
  } catch (e) {
    console.error('Failed to load patterns metadata:', e);
  }
}

export function markPatternExplored(patternId) {
  if (!patternId) return;
  exploredSet.add(patternId.toLowerCase());
  localStorage.setItem('savebite_explored_patterns', JSON.stringify([...exploredSet]));
  updateExploredTracker();
}

export function updateExploredTracker() {
  const countEl = document.getElementById('explored-count');
  const barEl = document.getElementById('explored-bar');
  const total = patternsCache.length || 23;
  const current = exploredSet.size;

  if (countEl) countEl.textContent = `${current} / ${total} patterns explored`;
  if (barEl) barEl.style.width = `${Math.min(100, Math.round((current / total) * 100))}%`;
}

export async function openPatternPopup(patternId) {
  if (!patternsCache || patternsCache.length === 0) {
    patternsCache = await fetchPatterns();
  }

  activePattern = patternsCache.find(p => p.id.toLowerCase() === patternId.toLowerCase());
  if (!activePattern) {
    console.warn('Pattern not found in registry:', patternId);
    return;
  }

  markPatternExplored(patternId);

  const modal = document.getElementById('pattern-modal');
  if (!modal) return;

  document.getElementById('pattern-modal-title').textContent = `${activePattern.name} Pattern`;
  document.getElementById('pattern-modal-category').textContent = activePattern.category;
  document.getElementById('pattern-modal-intent').textContent = activePattern.intent;

  switchTab('overview');
  modal.classList.add('open');
}

export function closePatternPopup() {
  const modal = document.getElementById('pattern-modal');
  if (modal) modal.classList.remove('open');
}

export async function switchTab(tabName) {
  currentTab = tabName;
  document.querySelectorAll('.modal-tab').forEach(t => {
    t.classList.toggle('active', t.dataset.tab === tabName);
  });

  const bodyEl = document.getElementById('pattern-modal-tab-body');
  if (!bodyEl || !activePattern) return;

  if (tabName === 'overview') {
    bodyEl.innerHTML = `
      <div style="margin-bottom: 1rem;">
        <h4 style="color: var(--primary); margin-bottom: 0.35rem;">🎯 The Problem Here</h4>
        <p style="font-size: 0.95rem; color: var(--text-secondary);">${escapeHtml(activePattern.problemHere)}</p>
      </div>

      <div style="margin-bottom: 1rem;">
        <h4 style="color: #dc2626; margin-bottom: 0.35rem;">❌ Without Pattern (The Naive Trap)</h4>
        <div class="code-container">${escapeHtml(activePattern.withoutPattern)}</div>
      </div>

      <div>
        <h4 style="color: #16a34a; margin-bottom: 0.35rem;">✅ With Pattern (In SaveBite)</h4>
        <div class="code-container">${escapeHtml(activePattern.withPattern)}</div>
      </div>
    `;
  } else if (tabName === 'architecture') {
    bodyEl.innerHTML = `
      <div style="margin-bottom: 1rem;">
        <h4 style="color: var(--primary); margin-bottom: 0.5rem;">🧩 GoF Architecture & Participants</h4>
        <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 0.75rem;">
          Classes participating in this pattern in SaveBite:
        </p>
        <div style="display:flex; gap:0.5rem; flex-wrap:wrap; margin-bottom: 1rem;">
          ${activePattern.classes.map(c => `<span style="background:var(--bg-secondary);padding:0.3rem 0.6rem;border-radius:4px;font-family:monospace;font-weight:bold;">${c}</span>`).join('')}
        </div>
        <div style="background:var(--bg-secondary);border:1px solid var(--border-color);border-radius:8px;padding:1.5rem;text-align:center;">
          <div style="font-size:0.9rem;font-weight:bold;margin-bottom:0.5rem;">UML Participant Mapping</div>
          <div style="display:inline-block;border:2px solid var(--primary);border-radius:6px;padding:0.75rem 1.5rem;background:var(--bg-card);font-weight:bold;">
            ${activePattern.classes[0]} &lt;&lt;${activePattern.name}&gt;&gt;
          </div>
          <div style="margin: 0.5rem 0; font-size: 1.2rem;">⬇ implements / coordinates</div>
          <div style="display:flex;gap:0.75rem;justify-content:center;flex-wrap:wrap;">
            ${activePattern.classes.slice(1).map(c => `<div style="border:1px solid var(--border-color);border-radius:6px;padding:0.5rem 1rem;background:var(--bg-card);">${c}</div>`).join('')}
          </div>
        </div>
      </div>
    `;
  } else if (tabName === 'code') {
    bodyEl.innerHTML = `<div style="text-align:center;padding:2rem;">Fetching real Java source code from disk...</div>`;
    try {
      const codeData = await fetchPatternCode(activePattern.id);
      bodyEl.innerHTML = `
        <div>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.5rem;">
            <span style="font-family:monospace;font-size:0.85rem;color:var(--text-muted);">${escapeHtml(codeData.filePath)}</span>
            <span class="badge" style="background:#e0f2fe;color:#0369a1;">Live File Reader</span>
          </div>
          <div class="code-container" style="max-height:400px;overflow-y:auto;">${escapeHtml(codeData.code)}</div>
        </div>
      `;
    } catch (e) {
      bodyEl.innerHTML = `<div style="color:#ef4444;padding:1rem;">Error reading file: ${e.message}</div>`;
    }
  } else if (tabName === 'proof') {
    bodyEl.innerHTML = `<div style="text-align:center;padding:2rem;">Running backend live verification proof...</div>`;
    try {
      const proofData = await fetchPatternProof(activePattern.id);
      bodyEl.innerHTML = `
        <div>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.75rem;">
            <h4 style="color:var(--primary);">⚡ Live Server Proof Data</h4>
            <span class="badge" style="background:#dcfce7;color:#166534;">Verified from JVM</span>
          </div>
          <div class="code-container" style="background:#0b1120;color:#38bdf8;max-height:360px;">${escapeHtml(JSON.stringify(proofData, null, 2))}</div>
        </div>
      `;
    } catch (e) {
      bodyEl.innerHTML = `<div style="color:#ef4444;padding:1rem;">Proof execution failed: ${e.message}</div>`;
    }
  } else if (tabName === 'viva') {
    bodyEl.innerHTML = `
      <div style="margin-bottom: 1.5rem;">
        <h4 style="color: #b45309; margin-bottom: 0.35rem;">🎓 Examiner / Viva Voce Question</h4>
        <div style="background:#fffbeb;border:1px solid #fef3c7;color:#92400e;padding:1rem;border-radius:8px;font-weight:600;font-size:0.95rem;">
          ${escapeHtml(activePattern.vivaQuestion)}
        </div>
      </div>

      <div style="margin-bottom: 1.5rem;">
        <h4 style="color: var(--primary); margin-bottom: 0.35rem;">💡 Model Answer</h4>
        <div style="background:var(--bg-secondary);border:1px solid var(--border-color);padding:1rem;border-radius:8px;font-size:0.92rem;">
          ${escapeHtml(activePattern.vivaAnswer)}
        </div>
      </div>

      <div>
        <h4 style="color: #dc2626; margin-bottom: 0.35rem;">⚖️ Trade-off & When NOT to Use</h4>
        <div style="background:#fef2f2;border:1px solid #fee2e2;color:#991b1b;padding:1rem;border-radius:8px;font-size:0.92rem;">
          ${escapeHtml(activePattern.tradeOff)}
        </div>
      </div>
    `;
  }
}

function initSseTrace() {
  if (sseSource) sseSource.close();
  try {
    sseSource = new EventSource('/api/teaching/stream');
    sseSource.addEventListener('trace', (e) => {
      try {
        const trace = JSON.parse(e.data);
        appendTraceEvent(trace);
      } catch (err) {
        console.error(err);
      }
    });
  } catch (e) {
    console.warn('SSE stream not available, falling back to polling:', e);
  }
}

let traceCount = 0;
function appendTraceEvent(ev) {
  traceCount++;
  const badge = document.getElementById('trace-counter');
  if (badge) badge.textContent = traceCount;

  const container = document.getElementById('trace-rows-container');
  if (!container) return;

  const row = document.createElement('div');
  row.className = 'trace-row';
  const time = new Date(ev.timestamp).toLocaleTimeString();
  row.innerHTML = `
    <span style="color:#64748b;">${time}</span>
    <span class="trace-tag">[${escapeHtml(ev.pattern)}]</span>
    <span class="trace-class">${escapeHtml(ev.className)}#${escapeHtml(ev.method)}</span>
    <span style="color:#cbd5e1;flex:1;">${escapeHtml(ev.message)}</span>
  `;
  container.prepend(row);

  // Keep max 100 rows in DOM
  if (container.children.length > 100) {
    container.removeChild(container.lastChild);
  }
}

export function showInlineHint(text) {
  const container = document.getElementById('inline-hints-container');
  if (!container) return;

  const hint = document.createElement('div');
  hint.className = 'inline-hint';
  hint.innerHTML = `<span>💡</span><span>${escapeHtml(text)}</span>`;
  container.prepend(hint);

  setTimeout(() => {
    hint.style.opacity = '0';
    setTimeout(() => hint.remove(), 300);
  }, 4000);
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

