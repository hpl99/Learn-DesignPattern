# SaveBite GoF Design Patterns Catalog

SaveBite implements **all 23 Gang of Four (GoF) Design Patterns** in realistic surplus food marketplace use cases. This document details each pattern, its GoF participants, project class mapping, and engineering trade-offs.

---

## 1. Creational Patterns (5)

### 1.1 Singleton
- **Category:** Creational
- **Intent:** Ensure a class only has one instance, and provide a global point of access to it.
- **Problem in SaveBite:** Global configurations, thread-safe application logging, and the HMAC-SHA256 JWT cryptographic key must exist exactly once across all request threads without duplicate secret generation or conflicting state.
- **Participants & Class Mapping:**
  - `Singleton`: `ConfigManager`, `AppLogger`, `JwtKeyHolder`, `PatternTracer`
  - `Client`: `Main`, `SecurityFilter`, Controllers
- **Implementation Strategy:** Double-checked locking with `volatile` fields or Bill Pugh Initialization-on-Demand Holder idiom for zero-overhead lazy loading.
- **Trade-off:** Global state can hinder parallel testing if state isn't reset. Mitigated in SaveBite via thread-safe collections and explicit test cleanup.

### 1.2 Factory Method
- **Category:** Creational
- **Intent:** Define an interface for creating an object, but let subclasses decide which class to instantiate.
- **Problem in SaveBite:** Users need diverse alert channels (browser in-app toast, email confirmation, SMS pickup code) depending on preference and urgency, without calling code knowing concrete notification classes.
- **Participants & Class Mapping:**
  - `Product`: `Notification`
  - `ConcreteProduct`: `ToastNotification`, `EmailNotification`, `SmsNotification`
  - `Creator`: `NotificationFactory`
  - `ConcreteCreator`: `ToastNotificationFactory`, `EmailNotificationFactory`, `SmsNotificationFactory`
- **Trade-off:** Adding a simple notification channel requires a new factory subclass unless parameterized factory methods are introduced.

### 1.3 Abstract Factory
- **Category:** Creational
- **Intent:** Provide an interface for creating families of related or dependent objects without specifying their concrete classes.
- **Problem in SaveBite:** The UI supports high-contrast Light and Dark accessibility themes. All rendered UI components (buttons, cards, badges, inputs) must match in color scheme, border radius, and contrast styling.
- **Participants & Class Mapping:**
  - `AbstractFactory`: `ThemeFactory`
  - `ConcreteFactory`: `LightThemeFactory`, `DarkThemeFactory`
  - `AbstractProduct`: `Button`, `Card`, `Input`, `Badge`
  - `ConcreteProduct`: `LightButton`, `DarkButton`, `LightCard`, `DarkCard`, `LightInput`, `DarkInput`, `LightBadge`, `DarkBadge`
- **Trade-off:** Adding a new component product (e.g., `Dropdown`) requires modifying `ThemeFactory` and all concrete factory implementations.

### 1.4 Builder
- **Category:** Creational
- **Intent:** Separate the construction of a complex object from its representation so that the same construction process can create different representations.
- **Problem in SaveBite:** Orders and surplus listings have many optional parameters (discounts, pickup addresses, gift packaging, environmental tags, JWT claims) leading to telescoping constructors.
- **Participants & Class Mapping:**
  - `Builder`: `OrderBuilder`, `ListingBuilder`, `JwtClaimsBuilder`
  - `Product`: `Order`, `Listing`, JWT claims payload map
- **Trade-off:** Increases code verbosity with inner builder classes, but eliminates parameter ordering mistakes and enables immutability.

### 1.5 Prototype
- **Category:** Creational
- **Intent:** Specify the kinds of objects to create using a prototypical instance, and create new objects by copying this prototype.
- **Problem in SaveBite:** Frequent customers reorder previous weekly food rescue baskets, and sellers clone repetitive bakery batches (e.g., daily sourdough loaves) without querying and re-entering 15 fields.
- **Participants & Class Mapping:**
  - `Prototype`: `com.savebite.patterns.creational.prototype.Prototype<T>`
  - `ConcretePrototype`: `ListingPrototype`, `OrderPrototype`
  - `Client`: `ReorderService`
- **Trade-off:** Deep copying object graphs with nested line-item references requires careful cloning logic to avoid shared mutable state.

---

## 2. Structural Patterns (7)

### 2.1 Adapter
- **Category:** Structural
- **Intent:** Convert the interface of a class into another interface clients expect.
- **Problem in SaveBite:** Social login providers (Google Identity SDK and GitHub OAuth API) expose incompatible method names, payload shapes, and credential tokens. SaveBite requires a unified `AuthProvider` interface.
- **Participants & Class Mapping:**
  - `Target`: `AuthProvider`
  - `Adaptee`: `GoogleOAuthClient`, `GithubOAuthClient`
  - `Adapter`: `GoogleAuthAdapter`, `GithubAuthAdapter`
- **Trade-off:** Adds an indirection layer, but completely decouples authentication controllers from vendor-specific changes.

### 2.2 Bridge
- **Category:** Structural
- **Intent:** Decouple an abstraction from its implementation so that the two can vary independently.
- **Problem in SaveBite:** Reports (`SalesReport`, `ImpactReport`) and export formats (`CsvExporter`, `HtmlExporter`, `PdfExporter`) vary along two independent dimensions. Without Bridge, an $M \times N$ class explosion would occur (`SalesCsvReport`, `SalesPdfReport`, etc.).
- **Participants & Class Mapping:**
  - `Abstraction`: `Report`
  - `RefinedAbstraction`: `SalesReport`, `ImpactReport`
  - `Implementor`: `Exporter`
  - `ConcreteImplementor`: `CsvExporter`, `HtmlExporter`, `PdfExporter`
- **Trade-off:** Slightly increases conceptual complexity up front, but scales linearly ($M + N$) as new reports or formats are added.

### 2.3 Composite
- **Category:** Structural
- **Intent:** Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.
- **Problem in SaveBite:** Marketplace items can be single food products (`ProductLeaf`) or multi-item "Surplus Rescue Bundles" and nested categories (`Category`, `Bundle`), all needing uniform price and weight calculations.
- **Participants & Class Mapping:**
  - `Component`: `CatalogNode`
  - `Leaf`: `ProductLeaf`
  - `Composite`: `Category`, `Bundle`
- **Trade-off:** Type safety check is relaxed at runtime since both leaf and composite share the same common interface.

### 2.4 Decorator
- **Category:** Structural
- **Intent:** Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.
- **Problem in SaveBite:** Checkout pricing requires dynamic, combinable adjustments (percentage discount coupons, gift wrap charges, express pickup priority fees) without exploding subclass hierarchies.
- **Participants & Class Mapping:**
  - `Component`: `Price`
  - `ConcreteComponent`: `BasePrice`
  - `Decorator`: `PriceDecorator`
  - `ConcreteDecorator`: `CouponDecorator`, `GiftWrapDecorator`, `PickupPriorityDecorator`
- **Trade-off:** Order of decorator wrapping matters (applying a 10% coupon before or after a flat fee alters total).

### 2.5 Facade
- **Category:** Structural
- **Intent:** Provide a unified interface to a set of interfaces in a subsystem.
- **Problem in SaveBite:** Multi-stage checkout orchestrates cart validation, coupon verification, payment execution, stock deduction, order persistence, and notification dispatching across 6 separate services.
- **Participants & Class Mapping:**
  - `Facade`: `CheckoutFacade`, `LoginFacade`
  - `Subsystems`: `ListingService`, `OrderService`, `PaymentProcessor`, `NotificationFactory`, `UserRepository`
- **Trade-off:** Can become a "God Class" if not scoped strictly to a specific client workflow.

### 2.6 Flyweight
- **Category:** Structural
- **Intent:** Use sharing to support large numbers of fine-grained objects efficiently.
- **Problem in SaveBite:** Displaying hundreds of marketplace food cards each bearing dietary and allergen badges (`Vegan`, `Gluten-Free`, `Halal`, `Dairy-Free`) can exhaust memory if each card instantiates new badge objects.
- **Participants & Class Mapping:**
  - `Flyweight`: `BadgeFlyweight`
  - `FlyweightFactory`: `BadgeFactory`
  - `Client`: `ListingController`, Marketplace UI
- **Trade-off:** Extrinsic state (e.g. badge DOM placement and click handlers) must be passed in by the client at render time.

### 2.7 Proxy
- **Category:** Structural
- **Intent:** Provide a surrogate or placeholder for another object to control access to it.
- **Problem in SaveBite:** Database queries for catalog listings are expensive under high traffic, admin service calls must strictly verify role privileges, and high-resolution food images should load lazily.
- **Participants & Class Mapping:**
  - `Subject`: `ListingRepository`, `AdminService`
  - `RealSubject`: `JdbcListingRepository`, `RealAdminService`
  - `Proxy`: `CachingListingRepositoryProxy`, `AdminServiceProxy`, `LazyImageProxy`
- **Trade-off:** Introduces potential cache staleness if cache invalidation on listing updates is not carefully coordinated.

---

## 3. Behavioral Patterns (11)

### 3.1 Chain of Responsibility
- **Category:** Behavioral
- **Intent:** Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request.
- **Problem in SaveBite:** Inbound HTTP requests must pass through a strict security pipeline: Assign Request ID -> Check Rate Limits -> Verify JWT Signature -> Check Revocation Blacklist -> Validate Role Permissions.
- **Participants & Class Mapping:**
  - `Handler`: `SecurityHandler`
  - `ConcreteHandler`: `RequestIdHandler`, `RateLimitHandler`, `JwtAuthenticationHandler`, `BlacklistHandler`, `RoleAuthorizationHandler`
  - `Client`: `SecurityPipeline`, `SecurityFilter`
- **Trade-off:** If a handler fails to call `passToNext()` or improperly terminates, the request silently ends.

### 3.2 Command
- **Category:** Behavioral
- **Intent:** Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.
- **Problem in SaveBite:** Customers managing their shopping cart require instant "Undo" and "Redo" functionality for item additions, removals, and quantity modifications.
- **Participants & Class Mapping:**
  - `Command`: `Command`
  - `ConcreteCommand`: `AddToCartCommand`, `RemoveFromCartCommand`, `ClearCartCommand`
  - `Receiver`: `Cart`
  - `Invoker`: `CommandHistory`
- **Trade-off:** Memory overhead of maintaining command history instances for deep undo stacks.

### 3.3 Interpreter
- **Category:** Behavioral
- **Intent:** Given a language, define a representation for its grammar along with an interpreter that uses the representation to interpret sentences in the language.
- **Problem in SaveBite:** Power users and search filters use queries like `price<100 AND category:bakery OR expiry<=2h`. The app must parse and evaluate this syntax against listings.
- **Participants & Class Mapping:**
  - `AbstractExpression`: `Expression`
  - `TerminalExpression`: `ComparisonExpression`
  - `NonTerminalExpression`: `AndExpression`, `OrExpression`
  - `Context / Parser`: `Tokenizer`, `Parser`, `Listing`
- **Trade-off:** Complex grammars become hard to maintain without compiler generation tools; scoped here to clean boolean filter syntax.

### 3.4 Iterator
- **Category:** Behavioral
- **Intent:** Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.
- **Problem in SaveBite:** The customer browse view requires server-side pagination with offset navigation, while the homepage featured food carousel requires infinite forward/backward circular navigation.
- **Participants & Class Mapping:**
  - `Iterator`: `CustomIterator<T>`
  - `ConcreteIterator`: `PaginatedListingIterator`, `CarouselIterator`
  - `Aggregate`: `ListingAggregate`
  - `ConcreteAggregate`: `ListingCollection`
- **Trade-off:** Creating multiple simultaneous iterators on a mutating list requires thread synchronization.

### 3.5 Mediator
- **Category:** Behavioral
- **Intent:** Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly.
- **Problem in SaveBite:** The checkout form features cascading dependencies: selecting Country restricts States, selecting State updates Cities, selecting City filters Pickup Hubs, selecting Hub reveals available Pickup Slots.
- **Participants & Class Mapping:**
  - `Mediator`: `CheckoutMediator`
  - `Colleague`: `FormField`
  - `ConcreteColleague`: `CountryField`, `StateField`, `CityField`, `PickupPointField`, `SlotField`, `PaymentField`
- **Trade-off:** The Mediator can become complex as more form fields and interdependent business rules are introduced.

### 3.6 Memento
- **Category:** Behavioral
- **Intent:** Without violating encapsulation, capture and externalize an object's internal state so that the object can be restored to this state later.
- **Problem in SaveBite:** Sellers editing surplus food listings and customers modifying profile preferences need automatic draft snapshots and the ability to restore any prior snapshot.
- **Participants & Class Mapping:**
  - `Originator`: `ProfileEditor`
  - `Memento`: `ProfileSnapshot`
  - `Caretaker`: `SnapshotHistory`
- **Trade-off:** Creating full state snapshots consumes memory if snapshots are recorded on every keystroke without debouncing.

### 3.7 Observer
- **Category:** Behavioral
- **Intent:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.
- **Problem in SaveBite:** When seller prices drop or listings near expiry change price, the live customer cart badge, customer price drop alerts, and Server-Sent Events (SSE) stream must update simultaneously.
- **Participants & Class Mapping:**
  - `Subject`: `Subject`, `PriceDropSubject`
  - `Observer`: `Observer`, `TraceListener`
  - `ConcreteObserver`: `CartBadgeObserver`, `PriceDropAlertObserver`, `NotificationCenter`, `PatternTracer`
- **Trade-off:** Observers can cause memory leaks if listeners are not unsubscribed when sessions terminate.

### 3.8 State
- **Category:** Behavioral
- **Intent:** Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.
- **Problem in SaveBite:** A surplus food listing progresses through `FRESH -> DISCOUNTED -> LAST_CHANCE -> SOLD / EXPIRED`. Available operations (reserving, price changes, edits) vary per state.
- **Participants & Class Mapping:**
  - `Context`: `ListingContext`, `Listing`
  - `State`: `ListingState`
  - `ConcreteState`: `FreshState`, `DiscountedState`, `LastChanceState`, `SoldState`, `ExpiredState`
- **Trade-off:** Increases the number of classes for simple finite state machines, but eliminates nested `switch` or `if/else` statements.

### 3.9 Strategy
- **Category:** Behavioral
- **Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.
- **Problem in SaveBite:** Surplus food discounts vary dynamically based on business goals: `ExpiryBasedDiscountStrategy` (drops price exponentially as closing time nears), `FlatDiscountStrategy` (fixed 30%), `BulkBuyDiscountStrategy` (extra 20% off for $\ge 3$ items).
- **Participants & Class Mapping:**
  - `Strategy`: `DiscountStrategy`
  - `ConcreteStrategy`: `ExpiryBasedDiscountStrategy`, `FlatDiscountStrategy`, `BulkBuyDiscountStrategy`
  - `Context`: `PricingContext`
- **Trade-off:** Clients must be aware of the different strategies to select the appropriate pricing mode.

### 3.10 Template Method
- **Category:** Behavioral
- **Intent:** Define the skeleton of an algorithm in an operation, deferring some steps to subclasses.
- **Problem in SaveBite:** Payment processing follows strict invariant steps: (1) Validate credentials, (2) Verify sufficient inventory/funds, (3) Execute transaction with gateway, (4) Record audit log, (5) Send confirmation receipt.
- **Participants & Class Mapping:**
  - `AbstractClass`: `PaymentProcessor`
  - `ConcreteClass`: `CardPayment`, `UpiPayment`, `CodPayment`
- **Trade-off:** Subclasses are bound to the algorithm skeleton defined in the superclass template method.

### 3.11 Visitor
- **Category:** Behavioral
- **Intent:** Represent an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements on which it operates.
- **Problem in SaveBite:** The hierarchical catalog (categories, bundles, products) needs diverse analytical calculations: total CO2 savings, cumulative weight rescued, and applicable GST taxes, without polluting entity models with calculation logic.
- **Participants & Class Mapping:**
  - `Visitor`: `CatalogVisitor`
  - `ConcreteVisitor`: `ImpactVisitor`, `WeightVisitor`, `TaxVisitor`
  - `Element`: `Visitable`, `CatalogNode`
  - `ConcreteElement`: `ProductLeaf`, `Bundle`, `Category`
- **Trade-off:** Adding a new element type requires updating the `CatalogVisitor` interface and all concrete visitors.

