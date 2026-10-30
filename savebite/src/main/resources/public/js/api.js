// SaveBite API Client with In-Memory JWT Access Token and Cookie-based Refresh
let inMemoryAccessToken = null;
let currentUser = null;

export const authState = {
  getToken: () => inMemoryAccessToken,
  setToken: (token) => { inMemoryAccessToken = token; },
  getUser: () => currentUser,
  setUser: (user) => {
    currentUser = user;
    window.dispatchEvent(new CustomEvent('auth-changed', { detail: user }));
  },
  clear: () => {
    inMemoryAccessToken = null;
    currentUser = null;
    window.dispatchEvent(new CustomEvent('auth-changed', { detail: null }));
  }
};

export async function apiRequest(endpoint, options = {}) {
  const headers = options.headers || {};
  if (inMemoryAccessToken) {
    headers['Authorization'] = `Bearer ${inMemoryAccessToken}`;
  }
  if (!headers['Content-Type'] && !(options.body instanceof FormData) && options.method && options.method !== 'GET') {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(endpoint, {
    ...options,
    headers,
    credentials: 'include' // Send HttpOnly refresh token cookie
  });

  // Token expired check
  if (response.status === 401 && !endpoint.includes('/api/auth/')) {
    try {
      const refreshed = await refreshToken();
      if (refreshed) {
        headers['Authorization'] = `Bearer ${inMemoryAccessToken}`;
        return fetch(endpoint, { ...options, headers, credentials: 'include' });
      }
    } catch (e) {
      authState.clear();
    }
  }

  return response;
}

export async function login(email, password) {
  const res = await apiRequest('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password })
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || err.error || 'Login failed');
  }
  const data = await res.json();
  authState.setToken(data.accessToken);
  authState.setUser(data.user);
  return data;
}

export async function register(user) {
  const res = await apiRequest('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(user)
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || err.error || 'Registration failed');
  }
  return res.json();
}

export async function refreshToken() {
  const res = await fetch('/api/auth/refresh', {
    method: 'POST',
    credentials: 'include'
  });
  if (res.ok) {
    const data = await res.json();
    authState.setToken(data.accessToken);
    return true;
  }
  return false;
}

export async function logout() {
  await apiRequest('/api/auth/logout', { method: 'POST' });
  authState.clear();
}

export async function fetchListings(query = '', category = '', sort = '', page = 1) {
  const params = new URLSearchParams();
  if (query) params.append('q', query);
  if (category && category !== 'all') params.append('category', category);
  if (sort) params.append('sort', sort);
  params.append('page', page);
  params.append('pageSize', 9);

  const res = await apiRequest(`/api/listings?${params.toString()}`);
  return res.json();
}

export async function fetchCart() {
  const res = await apiRequest('/api/cart');
  return res.json();
}

export async function addToCart(item) {
  const res = await apiRequest('/api/cart/items', {
    method: 'POST',
    body: JSON.stringify(item)
  });
  return res.json();
}

export async function removeFromCart(id, qty = 1) {
  const res = await apiRequest(`/api/cart/items/${id}?quantity=${qty}`, { method: 'DELETE' });
  return res.json();
}

export async function undoCart() {
  const res = await apiRequest('/api/cart/undo', { method: 'POST' });
  return res.json();
}

export async function redoCart() {
  const res = await apiRequest('/api/cart/redo', { method: 'POST' });
  return res.json();
}

export async function clearCart() {
  const res = await apiRequest('/api/cart', { method: 'DELETE' });
  return res.json();
}

export async function fetchCheckoutForm() {
  const res = await apiRequest('/api/checkout/form');
  return res.json();
}

export async function updateCheckoutField(field, value) {
  const res = await apiRequest('/api/checkout/field', {
    method: 'POST',
    body: JSON.stringify({ field, value })
  });
  return res.json();
}

export async function executeCheckout(checkoutData) {
  const res = await apiRequest('/api/checkout', {
    method: 'POST',
    body: JSON.stringify(checkoutData)
  });
  const data = await res.json();
  if (!res.ok) {
    throw new Error(data.error || 'Checkout transaction failed');
  }
  return data;
}

export async function fetchImpactMetrics() {
  const res = await apiRequest('/api/impact');
  return res.json();
}

export async function fetchPatterns() {
  const res = await apiRequest('/api/patterns');
  return res.json();
}

export async function fetchPatternCode(id) {
  const res = await apiRequest(`/api/patterns/${id}/code`);
  return res.json();
}

export async function fetchPatternProof(id) {
  const res = await apiRequest(`/api/patterns/${id}/proof`);
  return res.json();
}

