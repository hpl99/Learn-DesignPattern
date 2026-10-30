// SaveBite SPA Application Driver
import * as api from './api.js';
import * as teaching from './teaching.js';

// Expose teaching globally for inline HTML references
window.teaching = teaching;

let activePage = 'landing';
let currentSearch = '';
let currentCategory = 'all';
let currentSort = 'expiry';
let currentPage = 1;

document.addEventListener('DOMContentLoaded', async () => {
  teaching.initTeachingMode();
  setupNavigation();
  setupTheme();
  setupSearchAndFilters();
  setupCart();
  setupModals();
  setupSellerForm();
  setupProfileForm();
  setupKeyboardShortcuts();

  // Initial auth state check
  updateAuthUi(api.authState.getUser());
  await loadInitialData();
  navigateTo('landing');
});

/* Navigation */
function setupNavigation() {
  document.querySelectorAll('[data-nav]').forEach(el => {
    el.addEventListener('click', (e) => {
      e.preventDefault();
      const target = el.dataset.nav;
      navigateTo(target);
    });
  });

  window.addEventListener('auth-changed', (e) => {
    updateAuthUi(e.detail);
  });
}

export function navigateTo(pageId) {
  activePage = pageId;
  document.querySelectorAll('.page-view').forEach(p => p.style.display = 'none');
  document.querySelectorAll('.nav-link').forEach(l => l.classList.toggle('active', l.dataset.nav === pageId));

  const pageEl = document.getElementById(`page-${pageId}`);
  if (pageEl) {
    pageEl.style.display = 'block';
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  if (pageId === 'browse') {
    renderBrowseListings();
  } else if (pageId === 'landing') {
    renderLandingImpact();
  } else if (pageId === 'orders') {
    renderOrders();
  } else if (pageId === 'seller') {
    renderSellerDashboard();
  } else if (pageId === 'admin') {
    renderAdminPanel();
  } else if (pageId === 'profile') {
    renderProfileView();
  } else if (pageId === 'explorer') {
    renderPatternExplorer();
  }
}

async function loadInitialData() {
  try {
    const refreshed = await api.refreshToken();
    if (refreshed) {
      const meRes = await api.apiRequest('/api/auth/me');
      if (meRes.ok) {
        const user = await meRes.json();
        api.authState.setUser(user);
      }
    }
  } catch (e) {
    // Guest mode
  }

  await renderCartDrawer();
  await renderLandingImpact();
}

/* Authentication UI */
function updateAuthUi(user) {
  const authContainer = document.getElementById('header-auth-container');
  if (!authContainer) return;

  if (user) {
    authContainer.innerHTML = `
      <div style="display:flex;align-items:center;gap:0.5rem;flex-wrap:wrap;">
        <span style="font-size:0.85rem;font-weight:600;color:var(--text-secondary);">
          👤 ${escapeHtml(user.fullName || user.email)} 
          <span class="badge" style="background:#e0f2fe;color:#0369a1;margin-left:0.25rem;">${user.role}</span>
        </span>
        <button type="button" class="btn btn-sm btn-secondary" id="logout-btn">Logout</button>
      </div>
    `;
    document.getElementById('logout-btn')?.addEventListener('click', async () => {
      await api.logout();
      teaching.showInlineHint("Logged out. JWT token revoked and JTI blacklisted.");
      navigateTo('landing');
    });

    // Show/hide role-specific links
    document.getElementById('nav-seller')?.style.setProperty('display', (user.role === 'SELLER' || user.role === 'ADMIN') ? 'inline-block' : 'none');
    document.getElementById('nav-admin')?.style.setProperty('display', user.role === 'ADMIN' ? 'inline-block' : 'none');
  } else {
    authContainer.innerHTML = `
      <button type="button" class="btn btn-sm btn-primary" id="open-login-btn">Login / Register</button>
    `;
    document.getElementById('open-login-btn')?.addEventListener('click', () => {
      openAuthModal();
    });
    document.getElementById('nav-seller')?.style.setProperty('display', 'none');
    document.getElementById('nav-admin')?.style.setProperty('display', 'none');
  }
}

export function openAuthModal() {
  const modal = document.getElementById('auth-modal');
  if (modal) modal.classList.add('open');
}

export function closeAuthModal() {
  const modal = document.getElementById('auth-modal');
  if (modal) modal.classList.remove('open');
}

/* Theme (Abstract Factory) */
function setupTheme() {
  const themeToggle = document.getElementById('theme-toggle-btn');
  const currentTheme = localStorage.getItem('savebite_theme') || 'light';
  document.documentElement.setAttribute('data-theme', currentTheme);
  updateThemeIcon(currentTheme);

  if (themeToggle) {
    themeToggle.addEventListener('click', () => {
      const active = document.documentElement.getAttribute('data-theme');
      const next = active === 'dark' ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', next);
      localStorage.setItem('savebite_theme', next);
      updateThemeIcon(next);
      teaching.showInlineHint(`Abstract Factory: switched component tokens to ${next.toUpperCase()} theme family`);
    });
  }
}

function updateThemeIcon(theme) {
  const btn = document.getElementById('theme-toggle-btn');
  if (btn) btn.textContent = theme === 'dark' ? '☀️ Light' : '🌙 Dark';
}

/* Search and Filters (Interpreter, Strategy, Composite) */
function setupSearchAndFilters() {
  const searchInput = document.getElementById('search-query-input');
  const searchBtn = document.getElementById('search-exec-btn');
  const sortSelect = document.getElementById('sort-select');
  const pillsContainer = document.getElementById('category-pills-list');

  const executeSearch = () => {
    currentSearch = searchInput ? searchInput.value.trim() : '';
    currentPage = 1;
    renderBrowseListings();
    if (currentSearch) {
      teaching.showInlineHint(`Interpreter: Parsed and evaluated query "${currentSearch}"`);
    }
  };

  searchBtn?.addEventListener('click', executeSearch);

  searchInput?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      executeSearch();
    }
  });

  sortSelect?.addEventListener('change', (e) => {
    currentSort = e.target.value;
    currentPage = 1;
    renderBrowseListings();
    teaching.showInlineHint(`Strategy: Switched listing sort strategy to ${currentSort}`);
  });

  if (pillsContainer) {
    pillsContainer.querySelectorAll('.pill[data-cat]').forEach(pill => {
      pill.addEventListener('click', () => {
        pillsContainer.querySelectorAll('.pill[data-cat]').forEach(p => p.classList.remove('active'));
        pill.classList.add('active');
        currentCategory = pill.dataset.cat;
        currentPage = 1;
        renderBrowseListings();
        teaching.showInlineHint(`Composite: Filtered category branch '${currentCategory.toUpperCase()}'`);
      });
    });
  }
}

/* Landing Page Impact (Visitor) */
async function renderLandingImpact() {
  try {
    const data = await api.fetchImpactMetrics();
    const kgEl = document.getElementById('stat-kg');
    const rsEl = document.getElementById('stat-rupees');
    const co2El = document.getElementById('stat-co2');
    const mealsEl = document.getElementById('stat-meals');

    if (kgEl) kgEl.textContent = data.totalKgRescued || '18.5';
    if (rsEl) rsEl.textContent = '₹' + Math.round(data.totalRupeesSaved || 1250);
    if (co2El) co2El.textContent = data.totalCo2AvoidedKg || '46.2';
    if (mealsEl) mealsEl.textContent = data.totalMealsRescued || '42';
  } catch (e) {
    console.error('Impact metrics fetch failed:', e);
  }
}

/* Browse Page (Flyweight, State, Proxy, Iterator) */
async function renderBrowseListings() {
  const container = document.getElementById('listings-container');
  if (!container) return;
  container.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:3rem;">Loading fresh surplus items...</div>';

  try {
    const data = await api.fetchListings(currentSearch, currentCategory, currentSort, currentPage);
    const listings = data.listings || [];

    if (listings.length === 0) {
      container.innerHTML = `
        <div style="grid-column:1/-1;text-align:center;padding:3rem;background:var(--bg-card);border:1px dashed var(--border-color);border-radius:12px;">
          <div style="font-size:2.5rem;margin-bottom:0.5rem;">🔍</div>
          <h3 style="font-size:1.2rem;font-weight:700;">No surplus food matching your query</h3>
          <p style="color:var(--text-muted);margin:0.5rem 0 1rem;">Try clearing search syntax or selecting another category.</p>
          <button class="btn btn-sm btn-secondary" onclick="document.getElementById('search-query-input').value='';document.getElementById('search-exec-btn').click();">Reset Filters</button>
        </div>
      `;
      renderPagination(1, 1);
      return;
    }

    container.innerHTML = listings.map(l => {
      const isSold = l.state === 'SOLD' || l.quantity <= 0;
      const isExpired = l.state === 'EXPIRED';
      const hoursLeft = Math.max(0, Math.round((new Date(l.expiryTime).getTime() - Date.now()) / (1000 * 60 * 60)));

      return `
        <div class="listing-card">
          <div class="card-img-wrap">
            <img class="card-img" src="${escapeHtml(l.photoUrl || '/img/placeholder.svg')}" alt="${escapeHtml(l.title)}" onerror="this.src='https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&q=80'">
            <div class="card-badges">
              <span class="badge" style="background:#dcfce7;color:#166534;">${escapeHtml(l.category.toUpperCase())}</span>
              ${l.state === 'LAST_CHANCE' ? `<span class="badge" style="background:#fee2e2;color:#991b1b;">⚡ LAST CHANCE</span>` : ''}
              ${l.state === 'DISCOUNTED' ? `<span class="badge" style="background:#fef3c7;color:#92400e;">🏷️ 50% OFF</span>` : ''}
              ${isSold ? `<span class="badge" style="background:#475569;color:#fff;">SOLD OUT</span>` : ''}
            </div>
          </div>
          <div class="card-body">
            <div class="card-seller">${escapeHtml(l.sellerName || 'Verified Bakery')}</div>
            <h3 class="card-title">${escapeHtml(l.title)}</h3>
            <p class="card-desc">${escapeHtml(l.description)}</p>
            
            <div class="card-expiry">
              <span>⏳ Expires in ${hoursLeft}h (${new Date(l.expiryTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})})</span>
            </div>

            <div class="card-pricing-row">
              <div>
                <span class="price-current">₹${l.currentPrice}</span>
                <span class="price-original">₹${l.originalPrice}</span>
              </div>
              <span style="font-size:0.8rem;color:var(--text-muted);font-weight:bold;">${l.weightKg} kg</span>
            </div>

            <div class="card-actions">
              <button class="btn btn-primary btn-sm" style="flex:1;" 
                      ${isSold || isExpired ? 'disabled' : ''} 
                      onclick="window.SaveBite.handleAddToCart('${l.id}', '${escapeHtml(l.title)}', ${l.currentPrice}, ${l.weightKg})">
                ${isSold ? 'Sold Out' : (isExpired ? 'Expired' : 'Reserve Bite')}
              </button>
              <button type="button" class="pattern-btn" onclick="window.SaveBite.openPattern('state')">🧩 State</button>
            </div>
          </div>
        </div>
      `;
    }).join('');

    renderPagination(data.page, data.totalPages);
  } catch (e) {
    container.innerHTML = `<div style="grid-column:1/-1;color:#ef4444;padding:2rem;">Error loading items: ${e.message}</div>`;
  }
}

function renderPagination(page, totalPages) {
  const container = document.getElementById('pagination-container');
  if (!container || totalPages <= 1) {
    if (container) container.innerHTML = '';
    return;
  }

  container.innerHTML = `
    <div style="display:flex;justify-content:center;align-items:center;gap:0.5rem;margin:1.5rem 0;">
      <button class="btn btn-sm btn-secondary" ${page <= 1 ? 'disabled' : ''} onclick="window.SaveBite.changePage(${page - 1})">◀ Prev</button>
      <span style="font-size:0.9rem;font-weight:600;padding:0 0.5rem;">Page ${page} of ${totalPages}</span>
      <button class="btn btn-sm btn-secondary" ${page >= totalPages ? 'disabled' : ''} onclick="window.SaveBite.changePage(${page + 1})">Next ▶</button>
      <button type="button" class="pattern-btn" onclick="window.SaveBite.openPattern('iterator')">🧩 Iterator</button>
    </div>
  `;
}

/* Cart Management (Command Pattern) */
function setupCart() {
  document.getElementById('open-cart-btn')?.addEventListener('click', openCartDrawer);
  document.getElementById('close-cart-btn')?.addEventListener('click', closeCartDrawer);
  document.getElementById('cart-backdrop')?.addEventListener('click', closeCartDrawer);

  document.getElementById('cart-undo-btn')?.addEventListener('click', async () => {
    await api.undoCart();
    teaching.showInlineHint("Command Pattern: Undid previous cart action via undo stack");
    await renderCartDrawer();
  });

  document.getElementById('cart-redo-btn')?.addEventListener('click', async () => {
    await api.redoCart();
    teaching.showInlineHint("Command Pattern: Redid cart action via redo stack");
    await renderCartDrawer();
  });

  document.getElementById('cart-clear-btn')?.addEventListener('click', async () => {
    await api.clearCart();
    teaching.showInlineHint("Command Pattern: Cart cleared with snapshot saved for undo");
    await renderCartDrawer();
  });

  document.getElementById('proceed-checkout-btn')?.addEventListener('click', () => {
    closeCartDrawer();
    openCheckoutModal();
  });
}

export function openCartDrawer() {
  document.getElementById('cart-drawer')?.classList.add('open');
  document.getElementById('cart-backdrop')?.classList.add('open');
  renderCartDrawer();
}

export function closeCartDrawer() {
  document.getElementById('cart-drawer')?.classList.remove('open');
  document.getElementById('cart-backdrop')?.classList.remove('open');
}

async function renderCartDrawer() {
  try {
    const data = await api.fetchCart();
    const items = data.items || [];

    // Update cart badge
    const badgeEl = document.getElementById('cart-badge');
    if (badgeEl) {
      badgeEl.textContent = data.totalCount || 0;
      badgeEl.style.display = data.totalCount > 0 ? 'inline-block' : 'none';
    }

    const itemsContainer = document.getElementById('cart-items-container');
    if (!itemsContainer) return;

    if (items.length === 0) {
      itemsContainer.innerHTML = `
        <div style="text-align:center;padding:3rem 1rem;color:var(--text-muted);">
          <div style="font-size:2.5rem;margin-bottom:0.5rem;">🛒</div>
          <p style="font-weight:600;">Your surplus rescue cart is empty</p>
          <button class="btn btn-sm btn-primary" style="margin-top:1rem;" onclick="window.SaveBite.closeCart();window.SaveBite.navigateTo('browse');">Browse Surplus Food</button>
        </div>
      `;
      const subtotalEl = document.getElementById('cart-subtotal');
      if (subtotalEl) subtotalEl.textContent = '₹0.00';
      document.getElementById('proceed-checkout-btn')?.setAttribute('disabled', 'true');
      return;
    }

    document.getElementById('proceed-checkout-btn')?.removeAttribute('disabled');
    const subtotalEl = document.getElementById('cart-subtotal');
    if (subtotalEl) subtotalEl.textContent = `₹${data.totalPrice.toFixed(2)}`;

    itemsContainer.innerHTML = items.map(item => `
      <div class="cart-item">
        <div style="flex:1;">
          <h4 style="font-size:0.95rem;margin-bottom:0.2rem;">${escapeHtml(item.title)}</h4>
          <span style="font-size:0.85rem;color:var(--primary);font-weight:bold;">₹${item.unitPrice} × ${item.quantity} = ₹${item.subtotal.toFixed(2)}</span>
        </div>
        <div style="display:flex;align-items:center;gap:0.4rem;">
          <button type="button" class="btn btn-sm btn-secondary" onclick="window.SaveBite.handleRemoveFromCart('${item.listingId}')">−</button>
          <span style="font-weight:bold;min-width:20px;text-align:center;">${item.quantity}</span>
          <button type="button" class="btn btn-sm btn-secondary" onclick="window.SaveBite.handleAddToCart('${item.listingId}', '${escapeHtml(item.title)}', ${item.unitPrice}, ${item.weightKg})">+</button>
        </div>
      </div>
    `).join('');

    const undoBtn = document.getElementById('cart-undo-btn');
    const redoBtn = document.getElementById('cart-redo-btn');
    if (undoBtn) undoBtn.disabled = !data.canUndo;
    if (redoBtn) redoBtn.disabled = !data.canRedo;
  } catch (e) {
    console.error('Failed to render cart drawer:', e);
  }
}

/* Checkout Modal (Mediator, Decorator, Template Method, Facade) */
async function openCheckoutModal() {
  const modal = document.getElementById('checkout-modal');
  if (!modal) return;

  try {
    const formState = await api.fetchCheckoutForm();
    populateCheckoutForm(formState);
    modal.classList.add('open');
  } catch (e) {
    alert('Error loading checkout fields: ' + e.message);
  }
}

function populateCheckoutForm(state) {
  const countrySelect = document.getElementById('checkout-country');
  const stateSelect = document.getElementById('checkout-state');
  const citySelect = document.getElementById('checkout-city');
  const hubSelect = document.getElementById('checkout-hub');
  const slotSelect = document.getElementById('checkout-slot');

  if (countrySelect && state.countries) {
    countrySelect.innerHTML = state.countries.map(c => `<option value="${c}" ${c === state.country ? 'selected' : ''}>${c}</option>`).join('');
  }
  if (stateSelect && state.states) {
    stateSelect.innerHTML = state.states.map(s => `<option value="${s}" ${s === state.state ? 'selected' : ''}>${s}</option>`).join('');
  }
  if (citySelect && state.cities) {
    citySelect.innerHTML = state.cities.map(c => `<option value="${c}" ${c === state.city ? 'selected' : ''}>${c}</option>`).join('');
  }
  if (hubSelect && state.pickupPoints) {
    hubSelect.innerHTML = state.pickupPoints.map(h => `<option value="${h}" ${h === state.pickupPoint ? 'selected' : ''}>${h}</option>`).join('');
  }
  if (slotSelect && state.slots) {
    slotSelect.innerHTML = state.slots.map(s => `<option value="${s}" ${s === state.slot ? 'selected' : ''}>${s}</option>`).join('');
  }
}

async function handleCheckoutFieldChange(field, value) {
  try {
    const updated = await api.updateCheckoutField(field, value);
    populateCheckoutForm(updated);
    teaching.showInlineHint(`Mediator Pattern: Cascaded change from [${field} -> ${value}]`);
  } catch (e) {
    console.error(e);
  }
}

/* Modals & Popups Setup */
function setupModals() {
  // Pattern Modal
  document.getElementById('pattern-modal-close')?.addEventListener('click', teaching.closePatternPopup);
  document.querySelectorAll('.modal-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      teaching.switchTab(tab.dataset.tab);
    });
  });

  // Auth Modal
  document.getElementById('auth-modal-close')?.addEventListener('click', closeAuthModal);

  // Close modals when clicking backdrop
  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) {
        overlay.classList.remove('open');
      }
    });
  });

  // Toggle Register vs Login fields
  const registerCheckbox = document.getElementById('auth-is-register');
  const registerFields = document.getElementById('register-fields');
  const authTitle = document.getElementById('auth-modal-title');
  const authSubmit = document.getElementById('auth-submit-btn');

  registerCheckbox?.addEventListener('change', (e) => {
    if (e.target.checked) {
      if (registerFields) registerFields.style.display = 'flex';
      if (authTitle) authTitle.textContent = "Create SaveBite Account";
      if (authSubmit) authSubmit.textContent = "Create Account";
    } else {
      if (registerFields) registerFields.style.display = 'none';
      if (authTitle) authTitle.textContent = "Sign in to SaveBite";
      if (authSubmit) authSubmit.textContent = "Sign In";
    }
  });

  // Submit Login / Register
  document.getElementById('auth-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('auth-email').value.trim();
    const password = document.getElementById('auth-password').value;
    const isRegister = registerCheckbox ? registerCheckbox.checked : false;

    try {
      if (isRegister) {
        const fullName = document.getElementById('auth-name').value.trim();
        const role = document.getElementById('auth-role').value;
        await api.register({ email, password, fullName, role });
        teaching.showInlineHint("Account registered! Logging in...");
      }
      await api.login(email, password);
      closeAuthModal();
      teaching.showInlineHint("Login successful via Security Pipeline & LoginFacade");
    } catch (err) {
      alert("Authentication error: " + err.message);
    }
  });

  // Checkout Cascading Mediator Listeners
  document.getElementById('checkout-country')?.addEventListener('change', (e) => handleCheckoutFieldChange('country', e.target.value));
  document.getElementById('checkout-state')?.addEventListener('change', (e) => handleCheckoutFieldChange('state', e.target.value));
  document.getElementById('checkout-city')?.addEventListener('change', (e) => handleCheckoutFieldChange('city', e.target.value));
  document.getElementById('checkout-hub')?.addEventListener('change', (e) => handleCheckoutFieldChange('pickupPoint', e.target.value));

  // Checkout Submit
  document.getElementById('checkout-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = document.getElementById('checkout-submit-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = "Processing Checkout...";

    try {
      const payload = {
        pickupSlot: document.getElementById('checkout-slot').value,
        pickupAddress: document.getElementById('checkout-hub').value,
        paymentMethod: document.getElementById('checkout-payment-method').value,
        paymentDetails: document.getElementById('checkout-payment-details').value,
        couponCode: document.getElementById('checkout-coupon').value,
        includeGiftWrap: document.getElementById('checkout-giftwrap').checked,
        isPrioritySlot: document.getElementById('checkout-priority').checked
      };

      const res = await api.executeCheckout(payload);
      document.getElementById('checkout-modal')?.classList.remove('open');
      teaching.showInlineHint(`🎉 Order #${res.orderId} placed via CheckoutFacade!`);
      alert(`Order reserved successfully!\nOrder ID: ${res.orderId}\nTotal: ₹${res.totalAmount}\n${res.priceBreakdown}\n\n${res.receipt}`);
      await renderCartDrawer();
      navigateTo('orders');
    } catch (err) {
      alert("Checkout failed: " + err.message);
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = "Confirm & Reserve Bite";
    }
  });

  // Trace Dock Toggle
  const traceHeader = document.getElementById('trace-dock-header');
  const traceDock = document.getElementById('trace-dock');
  if (traceHeader && traceDock) {
    traceHeader.addEventListener('click', () => {
      traceDock.classList.toggle('expanded');
    });
  }

  document.getElementById('trace-clear-btn')?.addEventListener('click', async (e) => {
    e.stopPropagation();
    await api.apiRequest('/api/teaching/trace/clear', { method: 'POST' });
    document.getElementById('trace-rows-container').innerHTML = '';
    const counter = document.getElementById('trace-counter');
    if (counter) counter.textContent = '0';
  });
}

/* Seller Hub Listing Form (Builder) */
function setupSellerForm() {
  const form = document.getElementById('new-listing-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const title = document.getElementById('nl-title').value.trim();
    const category = document.getElementById('nl-cat').value;
    const originalPrice = parseFloat(document.getElementById('nl-orig-price').value);
    const currentPriceVal = document.getElementById('nl-cur-price').value;
    const currentPrice = currentPriceVal ? parseFloat(currentPriceVal) : originalPrice;
    const quantity = parseInt(document.getElementById('nl-qty').value, 10) || 1;
    const weightKg = parseFloat(document.getElementById('nl-weight').value) || 0.5;
    const description = document.getElementById('nl-desc').value.trim();

    try {
      const res = await api.apiRequest('/api/seller/listings', {
        method: 'POST',
        body: JSON.stringify({
          title,
          category,
          originalPrice,
          currentPrice,
          quantity,
          weightKg,
          description: description || "Fresh surplus batch prepared today."
        })
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Failed to create listing");
      }

      teaching.showInlineHint(`Builder Pattern: Constructed new surplus listing '${title}'`);
      alert(`Surplus food item "${title}" listed successfully!`);
      form.reset();
      await renderSellerDashboard();
    } catch (err) {
      alert("Listing creation failed: " + err.message);
    }
  });
}

/* Profile Form & Memento Snapshots */
function setupProfileForm() {
  const form = document.getElementById('profile-editor-form');
  const snapshotBtn = document.getElementById('create-snapshot-btn');

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const fullName = document.getElementById('profile-name').value.trim();
    const phone = document.getElementById('profile-phone').value.trim();
    const address = document.getElementById('profile-address').value.trim();
    const dietaryPreference = document.getElementById('profile-dietary').value;
    const smsAlertsEnabled = document.getElementById('profile-sms').checked;

    try {
      const res = await api.apiRequest('/api/profile', {
        method: 'PUT',
        body: JSON.stringify({
          fullName,
          phone,
          address,
          dietaryPreference,
          smsAlertsEnabled
        })
      });

      if (!res.ok) throw new Error("Failed to update profile");
      teaching.showInlineHint("Memento Pattern: Profile state saved & draft checkpoint recorded!");
      alert("Profile updated successfully!");
      await renderProfileView();
    } catch (err) {
      alert("Error saving profile: " + err.message);
    }
  });

  snapshotBtn?.addEventListener('click', async () => {
    try {
      const desc = prompt("Enter checkpoint description:", "Manual Snapshot " + new Date().toLocaleTimeString());
      if (!desc) return;

      const res = await api.apiRequest('/api/profile/snapshots', {
        method: 'POST',
        body: JSON.stringify({ description: desc })
      });

      if (!res.ok) throw new Error("Failed to capture snapshot");
      teaching.showInlineHint(`Memento: Checkpoint "${desc}" saved to Caretaker history`);
      await renderProfileView();
    } catch (err) {
      alert("Snapshot error: " + err.message);
    }
  });
}

function setupKeyboardShortcuts() {
  window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      teaching.closePatternPopup();
      document.getElementById('checkout-modal')?.classList.remove('open');
      document.getElementById('auth-modal')?.classList.remove('open');
      closeCartDrawer();
    }
    // Ctrl+Z Undo cart
    if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'z') {
      e.preventDefault();
      document.getElementById('cart-undo-btn')?.click();
    }
    // Ctrl+Y Redo cart
    if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'y') {
      e.preventDefault();
      document.getElementById('cart-redo-btn')?.click();
    }
  });
}

/* Customer Orders View */
async function renderOrders() {
  const container = document.getElementById('orders-container');
  if (!container) return;
  container.innerHTML = '<div style="text-align:center;padding:2rem;">Loading your orders...</div>';

  try {
    const res = await api.apiRequest('/api/orders');
    if (res.status === 401) {
      container.innerHTML = `
        <div style="text-align:center;padding:3rem;background:var(--bg-card);border:1px dashed var(--border-color);border-radius:12px;">
          <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:0.5rem;">Sign In Required</h3>
          <p style="color:var(--text-muted);margin-bottom:1.5rem;">Please log in with a customer account to view your past food rescue orders.</p>
          <button class="btn btn-primary" onclick="window.SaveBite.openLogin()">Sign In / Register</button>
        </div>
      `;
      return;
    }
    const orders = await res.json();

    if (orders.length === 0) {
      container.innerHTML = `
        <div style="text-align:center;padding:3rem;background:var(--bg-card);border:1px dashed var(--border-color);border-radius:12px;">
          <div style="font-size:2.5rem;margin-bottom:0.5rem;">📦</div>
          <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:0.5rem;">No orders yet</h3>
          <p style="color:var(--text-muted);margin-bottom:1.5rem;">Explore surplus deals from local bakeries and reserve fresh meals before closing.</p>
          <button class="btn btn-primary" onclick="window.SaveBite.navigateTo('browse')">Browse Surplus Deals</button>
        </div>
      `;
      return;
    }

    container.innerHTML = orders.map(ord => `
      <div style="background:var(--bg-card);border:1px solid var(--border-color);border-radius:var(--radius-md);padding:1.25rem;margin-bottom:1rem;box-shadow:var(--shadow-sm);">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.75rem;flex-wrap:wrap;gap:0.5rem;">
          <div>
            <span style="font-weight:bold;font-size:1.05rem;">Order #${ord.id}</span>
            <span class="badge" style="background:#e0f2fe;color:#0369a1;margin-left:0.5rem;">${ord.status}</span>
          </div>
          <span style="font-size:0.85rem;color:var(--text-muted);">${new Date(ord.createdAt).toLocaleDateString()}</span>
        </div>
        <div style="font-size:0.9rem;color:var(--text-secondary);margin-bottom:0.5rem;">
          📍 Pickup: <b>${escapeHtml(ord.pickupAddress)}</b> | Slot: <b>${escapeHtml(ord.pickupSlot)}</b>
        </div>
        <div style="border-top:1px dashed var(--border-color);padding-top:0.75rem;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:0.5rem;">
          <span style="font-size:1.15rem;font-weight:800;color:var(--primary);">Total: ₹${ord.totalAmount}</span>
          <div style="display:flex;gap:0.5rem;">
            <button type="button" class="btn btn-sm btn-primary" onclick="window.SaveBite.handleReorder('${ord.id}')">🔄 Reorder (Prototype)</button>
            <button type="button" class="pattern-btn" onclick="window.SaveBite.openPattern('prototype')">🧩 Prototype</button>
          </div>
        </div>
      </div>
    `).join('');
  } catch (e) {
    container.innerHTML = `<div style="color:#ef4444;padding:1rem;">Error: ${e.message}</div>`;
  }
}

/* Seller Dashboard */
async function renderSellerDashboard() {
  const container = document.getElementById('seller-listings-container');
  if (!container) return;

  try {
    const res = await api.apiRequest('/api/seller/listings');
    if (res.status === 401 || res.status === 403) {
      container.innerHTML = `
        <div style="padding:1.5rem;background:#fee2e2;color:#991b1b;border-radius:8px;text-align:center;">
          <b>Access Denied:</b> Seller role required. Please log in as <code>baker@savebite.org</code>.
          <div style="margin-top:0.75rem;">
            <button class="btn btn-sm btn-primary" onclick="window.SaveBite.quickLogin('seller')">Switch to Seller Account</button>
          </div>
        </div>
      `;
      return;
    }
    const listings = await res.json();
    if (listings.length === 0) {
      container.innerHTML = `<div style="color:var(--text-muted);padding:1rem;">No surplus items listed yet today. Use the form above to add one!</div>`;
      return;
    }
    container.innerHTML = listings.map(l => `
      <div style="background:var(--bg-card);border:1px solid var(--border-color);padding:1rem;border-radius:8px;margin-bottom:0.75rem;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:0.75rem;">
        <div>
          <b>${escapeHtml(l.title)}</b> (${escapeHtml(l.category)}) — <span style="color:var(--primary);font-weight:bold;">₹${l.currentPrice}</span> [Stock: ${l.quantity}]
          <div style="font-size:0.8rem;color:var(--text-muted);margin-top:0.25rem;">State: <span class="badge" style="background:#e2e8f0;">${l.state}</span> | Expiry: ${new Date(l.expiryTime).toLocaleTimeString()}</div>
        </div>
        <div style="display:flex;gap:0.5rem;">
          <button type="button" class="btn btn-sm btn-secondary" onclick="window.SaveBite.handleCloneListing('${l.id}')">📋 Clone Template</button>
          <button type="button" class="pattern-btn" onclick="window.SaveBite.openPattern('prototype')">🧩 Prototype</button>
        </div>
      </div>
    `).join('');
  } catch (e) {
    container.innerHTML = 'Error loading seller items: ' + e.message;
  }
}

/* Admin Panel */
async function renderAdminPanel() {
  const userTable = document.getElementById('admin-users-table');
  if (!userTable) return;

  try {
    const res = await api.apiRequest('/api/admin/users');
    if (!res.ok) {
      userTable.innerHTML = `
        <div style="padding:1.5rem;background:#fee2e2;color:#991b1b;border-radius:8px;text-align:center;">
          <b>Protection Proxy Active:</b> 403 Forbidden for non-admin accounts.<br>
          <span style="font-size:0.85rem;">This demonstrates GoF Protection Proxy preventing unauthorized privilege escalation.</span>
          <div style="margin-top:0.75rem;">
            <button class="btn btn-sm btn-primary" onclick="window.SaveBite.quickLogin('admin')">Switch to Admin Account (1-Click)</button>
          </div>
        </div>
      `;
      return;
    }
    const users = await res.json();
    userTable.innerHTML = `
      <table style="width:100%;border-collapse:collapse;font-size:0.9rem;">
        <tr style="background:var(--bg-secondary);text-align:left;">
          <th style="padding:0.6rem;">ID</th>
          <th style="padding:0.6rem;">Email</th>
          <th style="padding:0.6rem;">Name</th>
          <th style="padding:0.6rem;">Role</th>
          <th style="padding:0.6rem;">Action</th>
        </tr>
        ${users.map(u => `
          <tr style="border-bottom:1px solid var(--border-color);">
            <td style="padding:0.6rem;">${u.id}</td>
            <td style="padding:0.6rem;">${escapeHtml(u.email)}</td>
            <td style="padding:0.6rem;">${escapeHtml(u.fullName)}</td>
            <td style="padding:0.6rem;"><b>${u.role}</b></td>
            <td style="padding:0.6rem;">
              <select onchange="window.SaveBite.handleRoleChange('${u.id}', this.value)" style="padding:0.3rem 0.5rem;border-radius:4px;border:1px solid var(--border-color);background:var(--bg-secondary);color:var(--text-primary);">
                <option value="CUSTOMER" ${u.role === 'CUSTOMER' ? 'selected' : ''}>CUSTOMER</option>
                <option value="SELLER" ${u.role === 'SELLER' ? 'selected' : ''}>SELLER</option>
                <option value="ADMIN" ${u.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
              </select>
            </td>
          </tr>
        `).join('')}
      </table>
    `;
  } catch (e) {
    userTable.innerHTML = 'Error: ' + e.message;
  }
}

/* Profile View (Memento) */
async function renderProfileView() {
  const container = document.getElementById('page-profile');
  if (!container) return;

  try {
    const res = await api.apiRequest('/api/profile');
    if (res.status === 401) {
      const nameInput = document.getElementById('profile-name');
      if (nameInput) nameInput.value = '';
      const snapList = document.getElementById('profile-snapshots-list');
      if (snapList) {
        snapList.innerHTML = `
          <div style="padding:1rem;background:var(--bg-secondary);border-radius:6px;text-align:center;">
            <p style="margin-bottom:0.75rem;">Please log in to manage your profile and view Memento draft checkpoints.</p>
            <button class="btn btn-sm btn-primary" onclick="window.SaveBite.openLogin()">Sign In / Register</button>
          </div>
        `;
      }
      return;
    }
    const data = await res.json();
    document.getElementById('profile-name').value = data.fullName || '';
    document.getElementById('profile-phone').value = data.phone || '';
    document.getElementById('profile-address').value = data.address || '';
    document.getElementById('profile-dietary').value = data.dietaryPreference || 'Vegetarian';
    document.getElementById('profile-sms').checked = data.smsAlertsEnabled !== false;

    // Load snapshots
    const snapRes = await api.apiRequest('/api/profile/snapshots');
    const snapshots = await snapRes.json();
    const snapList = document.getElementById('profile-snapshots-list');
    if (snapList) {
      if (!snapshots || snapshots.length === 0) {
        snapList.innerHTML = '<div style="color:var(--text-muted);padding:0.5rem;">No checkpoints captured yet. Click "Save Checkpoint" to snapshot current form state.</div>';
      } else {
        snapList.innerHTML = snapshots.map(s => `
          <div style="background:var(--bg-secondary);border:1px solid var(--border-color);padding:0.75rem;border-radius:6px;margin-bottom:0.5rem;display:flex;justify-content:space-between;align-items:center;">
            <div>
              <b>${escapeHtml(s.description)}</b>
              <div style="font-size:0.78rem;color:var(--text-muted);">${new Date(s.snapshotTime).toLocaleString()} [ID: ${s.id.substring(0,8)}]</div>
            </div>
            <button type="button" class="btn btn-sm btn-secondary" onclick="window.SaveBite.handleRestoreSnapshot('${s.id}')">↩ Restore</button>
          </div>
        `).join('');
      }
    }
  } catch (e) {
    console.error(e);
  }
}

/* Pattern Explorer */
async function renderPatternExplorer() {
  const container = document.getElementById('explorer-cards-container');
  if (!container) return;

  try {
    const patterns = await api.fetchPatterns();
    container.innerHTML = patterns.map(p => `
      <div style="background:var(--bg-card);border:1px solid var(--border-color);border-radius:var(--radius-md);padding:1.25rem;box-shadow:var(--shadow-sm);display:flex;flex-direction:column;justify-content:space-between;">
        <div>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.5rem;">
            <span class="badge" style="background:${p.category==='Creational'?'#dbeafe':(p.category==='Structural'?'#ede9fe':'#fef3c7')};color:${p.category==='Creational'?'#1d4ed8':(p.category==='Structural'?'#6d28d9':'#b45309')};">${p.category}</span>
            <span style="font-size:0.75rem;font-weight:bold;color:var(--text-muted);">#${p.id}</span>
          </div>
          <h3 style="font-size:1.15rem;margin-bottom:0.4rem;">${escapeHtml(p.name)}</h3>
          <p style="font-size:0.85rem;color:var(--text-secondary);margin-bottom:1rem;">${escapeHtml(p.intent)}</p>
        </div>
        <button type="button" class="pattern-btn" style="width:100%;justify-content:center;" onclick="window.SaveBite.openPattern('${p.id}')">
          💡 How does ${p.name} solve this?
        </button>
      </div>
    `).join('');
  } catch (e) {
    container.innerHTML = 'Error loading patterns: ' + e.message;
  }
}

// Global API export for inline HTML event handlers & easy console interaction
window.SaveBite = {
  navigateTo,
  openLogin: openAuthModal,
  closeLogin: closeAuthModal,
  openCart: openCartDrawer,
  closeCart: closeCartDrawer,
  openPattern: (id) => teaching.openPatternPopup(id),
  closePatternPopup: () => teaching.closePatternPopup(),
  quickLogin: async (role) => {
    let email = 'customer@savebite.org';
    let pass = 'customer123';
    if (role === 'seller') {
      email = 'baker@savebite.org';
      pass = 'baker123';
    } else if (role === 'admin') {
      email = 'admin@savebite.org';
      pass = 'admin123';
    }
    const emailInput = document.getElementById('auth-email');
    const passInput = document.getElementById('auth-password');
    if (emailInput) emailInput.value = email;
    if (passInput) passInput.value = pass;

    try {
      await api.login(email, pass);
      closeAuthModal();
      teaching.showInlineHint(`1-Click Login: Authenticated as ${role.toUpperCase()} (${email})`);
      if (role === 'seller') {
        navigateTo('seller');
      } else if (role === 'admin') {
        navigateTo('admin');
      } else {
        navigateTo('browse');
      }
    } catch (err) {
      alert("Login failed: " + err.message);
    }
  },
  handleAddToCart: async (listingId, title, unitPrice, weightKg) => {
    await api.addToCart({ listingId, title, unitPrice, quantity: 1, weightKg });
    teaching.showInlineHint(`Added ${title} to cart via Command Pattern`);
    await renderCartDrawer();
  },
  handleRemoveFromCart: async (id) => {
    await api.removeFromCart(id, 1);
    teaching.showInlineHint("Removed item via Command Pattern");
    await renderCartDrawer();
  },
  changePage: (newPage) => {
    currentPage = newPage;
    renderBrowseListings();
  },
  handleReorder: async (orderId) => {
    try {
      const res = await api.apiRequest(`/api/orders/${orderId}/reorder`, { method: 'POST' });
      const newOrder = await res.json();
      teaching.showInlineHint(`Prototype Pattern: Reordered past order #${orderId} -> New #${newOrder.id}`);
      await renderOrders();
    } catch (e) {
      alert(e.message);
    }
  },
  handleCloneListing: async (listingId) => {
    try {
      const res = await api.apiRequest(`/api/listings/${listingId}/clone`, { method: 'POST' });
      const clone = await res.json();
      teaching.showInlineHint(`Prototype Pattern: Cloned template into new batch '${clone.title}'`);
      await renderSellerDashboard();
    } catch (e) {
      alert(e.message);
    }
  },
  handleRoleChange: async (userId, role) => {
    try {
      const res = await api.apiRequest(`/api/admin/users/${userId}/role`, {
        method: 'PUT',
        body: JSON.stringify({ role })
      });
      if (res.ok) {
        teaching.showInlineHint(`Proxy Pattern: Admin role updated user ${userId} to ${role}`);
      } else {
        alert("Protection Proxy blocked non-admin mutation");
      }
    } catch (e) {
      alert(e.message);
    }
  },
  handleRestoreSnapshot: async (snapId) => {
    try {
      await api.apiRequest(`/api/profile/snapshots/${snapId}/restore`, { method: 'POST' });
      teaching.showInlineHint(`Memento Pattern: Profile state restored from snapshot`);
      await renderProfileView();
    } catch (e) {
      alert(e.message);
    }
  }
};

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
