# SaveBite: Comprehensive Project Manual & Architecture Guide

---

## 1. Executive Summary & Purpose

**SaveBite** is a production-style, hyper-local surplus-food marketplace built with **Java 17**, **Javalin 6.3.0**, **Plain JDBC over embedded H2**, and **Vanilla ES Modules**.

The platform accomplishes two simultaneous missions:
1. **Solves a Critical Real-World Problem:** Rescues fresh, high-quality surplus food from bakeries, restaurants, and grocery shops before closing time, dramatically reducing landfill waste, consumer grocery bills, and greenhouse gas emissions.
2. **Interactive Design Patterns Education:** Seamlessly embeds **all 23 Gang of Four (GoF) design patterns** into real, operational marketplace features. Every button, pricing calculation, form field, and security check serves as a living, demonstrable lesson in software engineering.

---

## 2. The Problems We Are Solving

### 2.1 The Retail Food Waste Dilemma
Every single evening, commercial bakeries, cafes, salad bars, and prepared food counters discard 30% to 50% of their daily fresh output. These items (artisan bread loaves, croissants, salads, parfaits, and dinner boxes) remain 100% wholesome, safe, and delicious, but cannot be sold tomorrow because they must be baked or prepared fresh daily.

### 2.2 Consumer Cost of Living & Inflation
Simultaneously, students, young professionals, and budget-conscious families face high food inflation and grocery costs. They would gladly purchase and consume today’s fresh surplus food if an accessible, trustworthy channel existed to reserve and pick it up at a significant discount (30% to 70% off retail).

### 2.3 Severe Environmental Impact
Food waste rotting in municipal landfills generates massive quantities of methane ($\text{CH}_4$), a greenhouse gas 28 times more potent than carbon dioxide over a 100-year timescale.
$$\text{For every 1 kg of food waste sent to a landfill, approximately } 2.5\text{ kg of }\text{CO}_2\text{ equivalent is released.}$$
By rescuing surplus food before it enters the waste stream, SaveBite directly curbs methane emissions and tracks this ecological benefit in real time.

### 2.4 Lack of Practical Pattern Teaching in Software Education
Traditional computer science education teaches design patterns as abstract, artificial whiteboard exercises (e.g., `Dog extends Animal`, `CarFactory`). Students rarely observe how patterns solve realistic engineering challenges like rate-limiting, cart undo/redo, dynamic markdown pricing, or multi-format reporting. SaveBite bridges this divide by presenting every pattern inside a working commercial marketplace.

---

## 3. How SaveBite Solves These Problems

| Challenge | SaveBite Architectural Solution | GoF Pattern Applied |
|---|---|---|
| **Discounts must rise as closing time nears** | Pricing algorithms dynamically markdown items based on hours until expiry | **Strategy Pattern** |
| **Combating accidental cart removals** | Cart operations are encapsulated into commands with an undo/redo stack | **Command Pattern** |
| **Complex food search filters** | Parses boolean query mini-language (`price<100 AND category:bakery`) | **Interpreter Pattern** |
| **Cascading checkout form fields** | Selecting Country filters States, Cities, Hubs, and Slots centrally | **Mediator Pattern** |
| **Preventing unauthorized admin access** | Intercepts requests and enforces RBAC before delegating to real admin service | **Proxy Pattern** |
| **Multi-channel customer alerts** | Dispatches Toast, SMS, or Email notifications through a unified interface | **Factory Method** |
| **Multi-format report generation** | Separates sales/impact report logic from CSV, HTML, and ASCII PDF exporters | **Bridge Pattern** |
| **Profile & listing draft autosave** | Captures state snapshots allowing users to time-travel and restore drafts | **Memento Pattern** |
| **Real-time price drop notifications** | Event subjects notify cart badges and stream live SSE telemetry | **Observer Pattern** |
| **Calculating environmental CO2 savings** | Traverses categories, bundles, and product leaves to compute total kg and CO2 | **Visitor Pattern** |

---

## 4. Step-by-Step User Guide (How to Use SaveBite)

### 4.1 Launching the Application
Execute one of the following commands in the project root:

- **Windows (Command Prompt):**
  ```cmd
  .\run.bat
  ```
- **Windows (PowerShell):**
  ```powershell
  .\run.ps1
  ```
- **Maven Command Line:**
  ```bash
  mvn clean package && java -jar target/savebite.jar
  ```
- **Linux / macOS / WSL:**
  ```bash
  chmod +x run.sh
  ./run.sh
  ```

Open your web browser and navigate to:
👉 **`http://localhost:7070`**

---

### 4.2 Navigating the System

```
[🥗 SaveBite Navbar]
 ├── Problem & Solution   (Landing view with mission statement & live impact counters)
 ├── Browse Surplus       (Marketplace catalog, search mini-language, category pills, deals)
 ├── My Orders            (Customer order history & 1-click Prototype reordering)
 ├── Seller Hub           (Bakery dashboard, listing creation with Builder, report exports)
 ├── Admin Console        (User management, role assignment, Protection Proxy demo)
 ├── Profile              (Profile editor, autosave checkpoints, Memento restoration)
 ├── Pattern Explorer     (Catalog of all 23 GoF patterns with live code and server proofs)
 └── About                (Technical architecture and framework specifications)
```

---

### 4.3 Key User Workflows

#### A. Logging In with 1-Click Demo Accounts
1. Click the **"Login / Register"** button in the top right corner.
2. In the modal, you will see the **⚡ 1-Click Demo Logins** panel:
   - Click **👤 Customer** (`customer@savebite.org` / `customer123`)
   - Click **🥖 Seller** (`baker@savebite.org` / `baker123`)
   - Click **🛡️ Admin** (`admin@savebite.org` / `admin123`)
3. The system verifies credentials, sets the in-memory JWT access token, sets the `HttpOnly` refresh cookie, and updates the UI instantly.
4. If you wish to register a brand new account, check **"I need to create a new account"**, fill in your details, and click **"Create Account"**.

#### B. Searching & Filtering Surplus Items
1. Click **"Browse Surplus"** in the navigation bar.
2. In the search box, test the **Interpreter pattern** by typing compound queries:
   - `price<100 AND category:bakery`
   - `expiry<=2h`
   - `category:dairy OR category:prepared`
3. Click **Search** or press **Enter**.
4. Click the category pills (**Bakery & Breads**, **Dairy & Parfaits**, **Chef Prepared Meals**, etc.) to filter by department (**Composite pattern**).
5. Change the sort dropdown (**Sort: Expiry Soonest**, **Price Low to High**) to observe the **Strategy pattern** reordering items.

#### C. Shopping Cart with Instant Undo / Redo
1. On any surplus card, click **"Reserve Bite"**.
2. Notice the live notification badge appear over the **🛒 Cart** button.
3. Click **🛒 Cart** to slide open the cart drawer.
4. Adjust quantities using `+` and `−`.
5. Click **"↩ Undo"** (or press `Ctrl+Z` on your keyboard) to undo your last modification.
6. Click **"↪ Redo"** (or press `Ctrl+Y`) to restore the undone action.
7. Click **"Proceed to Checkout"**.

#### D. Cascading Checkout & Dynamic Surcharges
1. In the checkout modal:
   - Select **Country** $\rightarrow$ Notice **State** updates.
   - Select **State** $\rightarrow$ Notice **City** updates.
   - Select **City** $\rightarrow$ Notice available **Pickup Hubs** update (**Mediator pattern**).
2. Toggle **"Thermal Insulated Eco-Packaging"** (+₹25.00) or **"Priority Rush Reservation"** (+₹15.00) to observe runtime price wrapping (**Decorator pattern**).
3. Enter coupon code `SAVE15` for 15% off.
4. Select your preferred payment method (**UPI**, **Card**, or **Cash on Pickup**) and click **"Confirm & Reserve Bite"** (**Template Method pattern** executing 5 invariant steps).

#### E. Seller Operations (Bakery Hub)
1. Log in as **Seller** (`baker@savebite.org`).
2. Click **"Seller Hub"** in the navigation bar.
3. Use the **"List Today's Near-Expiry Surplus Food"** form:
   - Fill in Title, Category, Original Price, Surplus Price, Quantity, and Weight.
   - Click **"Construct Listing with Builder"** (**Builder pattern**).
4. Export sales reports in **HTML**, **CSV**, or **ASCII PDF** formats (**Bridge pattern**).
5. On existing listings, click **"📋 Clone Template"** to instantly replicate a daily bread batch (**Prototype pattern**).

#### F. Profile Autosave & Memento Time Travel
1. Click **"Profile"** in the navigation bar.
2. Edit your name, phone number, or dietary preferences.
3. Click **"Save Profile Changes"** or **"📸 Save Checkpoint"**.
4. In the **Snapshot History (Caretaker)** panel on the right, observe your saved checkpoints.
5. Make changes to the form, then click **"↩ Restore"** on an earlier snapshot to watch the form revert its state without page reload (**Memento pattern**).

#### G. Admin Console & Protection Proxy Demonstration
1. While logged in as **Customer**, click **"Admin"** (or navigate to `/api/admin/users`).
2. Notice the **Protection Proxy** intercepts your request and displays a clear **403 Forbidden** security message.
3. Click **"Switch to Admin Account"** to re-authenticate as `admin@savebite.org`.
4. The admin console loads the full registered user table, allowing you to view audit trails and update user roles.

---

## 5. The Interactive Teaching Experience

### 💡 Contextual Pattern Popups
Every feature area displays a gold pill button: **"💡 How does a design pattern solve this?"**. Clicking it opens a focused modal containing:
1. **The Problem Here:** Plain-English explanation of why this specific screen or feature is architecturally challenging.
2. **Pattern Used:** GoF name, category, and one-line intent.
3. **Without Pattern vs With Pattern:** A code snippet comparison illustrating the naive mistake versus the pattern solution.
4. **Architecture & Participants:** Visual mapping of real SaveBite classes to official GoF roles.
5. **Real Java Source Code:** Reads the actual `.java` file directly from disk and highlights it.
6. **Live Server Proof:** Real-time data fetched from the JVM (e.g. Singleton instance hashcodes, Flyweight memory counters, Chain handler traces).
7. **Viva Voce Question & Model Answer:** Oral exam prep for project presentations.
8. **Engineering Trade-offs:** When not to use the pattern and its associated costs.

### ⚡ Live Pattern Trace Console (SSE)
Click the terminal drawer at the bottom right of the screen:
- Streams every pattern event across the entire backend in real time.
- Displays timestamp, pattern name, Java class, method, and detailed telemetry.
- Includes a **"Clear"** button and an **"Export .txt"** button to save execution logs for project evaluation.

### 🎚️ Global Teaching Mode Switch
Located in the header navigation:
- **ON (Default):** Surfaces all pattern exploration buttons, inline hints, and trace listeners.
- **OFF:** Hides all educational chips, transforming SaveBite into a clean, commercial marketplace interface.

---

## 6. Verification & Automated Test Suite

SaveBite includes **31 automated tests** covering all 23 GoF design patterns, JWT security, and end-to-end integration journeys.

Run all tests with:
```bash
mvn test
```

### Test Coverage Summary:
- **`CreationalPatternsTest` (5/5 Passed):** Singleton, Factory Method, Abstract Factory, Builder, Prototype.
- **`StructuralPatternsTest` (7/7 Passed):** Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy.
- **`BehavioralPatternsTest` (11/11 Passed):** Chain of Responsibility, Command, Interpreter, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor.
- **`JwtSecurityTest` (6/6 Passed):** HMAC-SHA256 signature verification, tampering detection, rate-limiting lockout (5 failures $\rightarrow$ 60s freeze), token revocation blacklist.
- **`PatternConsistencyTest` (1/1 Passed):** Automated reflection test ensuring `@DesignPattern` annotations never drift from `patterns.json`.
- **`IntegrationFlowTest` (1/1 Passed):** Comprehensive HTTP journey: Register $\rightarrow$ Login $\rightarrow$ Search $\rightarrow$ Cart $\rightarrow$ Checkout $\rightarrow$ Logout $\rightarrow$ Token Revocation check.

---

## 7. Default Credentials Quick Reference

| Account Type | Email | Password | Role |
|---|---|---|---|
| **Platform Administrator** | `admin@savebite.org` | `admin123` | `ADMIN` |
| **Bakery / Restaurant Seller** | `baker@savebite.org` | `baker123` | `SELLER` |
| **Food Rescue Shopper** | `customer@savebite.org` | `customer123` | `CUSTOMER` |

---

## 8. Summary of Problems Solved

1. **Economic:** Rescues revenue for culinary vendors and cuts grocery bills for consumers.
2. **Ecological:** Reduces landfill methane by preventing organic waste accumulation ($2.5\text{ kg CO}_2\text{e}$ per kg saved).
3. **Architectural:** Demonstrates clean separation of concerns, loose coupling, open-closed principle, and single responsibility across all 23 GoF patterns.
4. **Educational:** Provides students, teachers, and developers with an interactive, living reference for software design patterns in modern Java.

