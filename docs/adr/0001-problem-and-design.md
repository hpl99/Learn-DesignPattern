# ADR 0001: Solve food waste with a pattern-driven marketplace
Status: Accepted | Date: 2026-10-30

## Context
The course needs a project that solves a real problem and shows all 23 GoF patterns.

## Options
1. Generic store: no real problem. 2. Local-shop marketplace: common idea. 3. **Surplus-food marketplace**: measurable impact; patterns fit naturally (Strategy for expiry discounts, State for listing lifecycle, Observer for alerts, Visitor for impact totals).

## Decision
Option 3, on the JDK's built-in HTTP server and plain HTML/JS, so no framework hides the patterns.

## Consequences
+ Easy to demo and explain, zero dependencies. − No database or auth UI yet.
Next: pattern tests, per-pattern ADRs, cart/checkout pages, trace console, guided tour.
