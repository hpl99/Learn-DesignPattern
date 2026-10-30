# Architecture Decision Records (ADR) Index

This directory documents key architectural decisions and all 23 GoF Design Pattern applications within SaveBite using the MADR (Markdown Architecture Decision Records) template.

## Architectural Foundation Decisions
- [ADR 0001: Technology Stack Selection - Javalin 6 & Java 17](0001-tech-stack-javalin6.md)
- [ADR 0002: Storage Layer - Embedded H2 over Plain JDBC with Repository Pattern](0002-h2-jdbc-repository.md)
- [ADR 0003: Stateless Authentication - In-Memory Access JWT & HttpOnly Refresh Cookie](0003-jwt-in-memory-cookie.md)
- [ADR 0004: Frontend Architecture - Vanilla ES Modules & Modern CSS3 without Build Bundlers](0004-vanilla-es-modules.md)
- [ADR 0005: Single Source of Truth for Teaching Engine (patterns.json & @DesignPattern)](0005-single-source-of-truth-patterns.md)
- [ADR 0006: State Restoration & Rollback - Memento vs. Command Pattern](0006-memento-vs-command.md)
- [ADR 0007: Thread-Safe Singleton Idiom - Bill Pugh vs. Double-Checked Locking](0007-singleton-double-checked-locking.md)

## GoF Design Pattern Decisions (All 23 Patterns)

### Creational Patterns
- [ADR 0010: Creational - Singleton for Shared Configurations, Logging & Key Storage](0010-singleton.md)
- [ADR 0011: Creational - Factory Method for Multi-Channel User Notifications](0011-factory-method.md)
- [ADR 0012: Creational - Abstract Factory for Cohesive Light/Dark Theme Component Families](0012-abstract-factory.md)
- [ADR 0013: Creational - Builder for Multi-Step Listing, Order & Claims Construction](0013-builder.md)
- [ADR 0014: Creational - Prototype for Instant 1-Click Order Reordering](0014-prototype.md)

### Structural Patterns
- [ADR 0015: Structural - Adapter for Third-Party Social Identity Providers (Google & GitHub)](0015-adapter.md)
- [ADR 0016: Structural - Bridge for Decoupling Report Domain from Export Formats](0016-bridge.md)
- [ADR 0017: Structural - Composite for Hierarchical Food Categories & Rescue Bundles](0017-composite.md)
- [ADR 0018: Structural - Decorator for Transparent Order Surcharges, Gift Wraps & Discounts](0018-decorator.md)
- [ADR 0019: Structural - Facade for Simplifying Multi-Subsystem Login & Multi-Stage Checkout](0019-facade.md)
- [ADR 0020: Structural - Flyweight for High-Performance Sharing of Allergen & Dietary Badges](0020-flyweight.md)
- [ADR 0021: Structural - Proxy for Listing Caching, Lazy Image Loading & Admin RBAC Enforcement](0021-proxy.md)

### Behavioral Patterns
- [ADR 0022: Behavioral - Chain of Responsibility for Pre-Request Security & Validation Pipeline](0022-chain-of-responsibility.md)
- [ADR 0023: Behavioral - Command for Undoable Cart Operations & Replay Audit History](0023-command.md)
- [ADR 0024: Behavioral - Interpreter for Dynamic Food Search Grammar & Compound Filters](0024-interpreter.md)
- [ADR 0025: Behavioral - Iterator for Paginated Marketplace Listings & Carousel Traversal](0025-iterator.md)
- [ADR 0026: Behavioral - Mediator for Loose Coupling in Cascading Geographic Checkout Form](0026-mediator.md)
- [ADR 0027: Behavioral - Memento for Seller Listing Drafts & Profile Autosave Time Travel](0027-memento.md)
- [ADR 0028: Behavioral - Observer for Real-Time Price Drops, Cart Badges & SSE Push](0028-observer.md)
- [ADR 0029: Behavioral - State for Encapsulated Surplus Food Listing Lifecycle Transitions](0029-state.md)
- [ADR 0030: Behavioral - Strategy for Dynamic Near-Expiry Discount Calculations](0030-strategy.md)
- [ADR 0031: Behavioral - Template Method for Standardized Multi-Gateway Payment Execution](0031-template-method.md)
- [ADR 0032: Behavioral - Visitor for Environmental CO2, Weight & Tax Calculations on Food Hierarchies](0032-visitor.md)

