const state = {
  token: localStorage.getItem("orderPlatformToken") || "",
  user: null,
  authMode: "login",
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

function toast(message) {
  const node = $("#toast");
  node.textContent = message;
  node.classList.add("show");
  window.clearTimeout(toast.timer);
  toast.timer = window.setTimeout(() => node.classList.remove("show"), 3200);
}

function authHeaders() {
  return state.token ? { Authorization: `Bearer ${state.token}` } : {};
}

async function api(path, options = {}) {
  const headers = {
    ...authHeaders(),
    ...(options.body ? { "Content-Type": "application/json" } : {}),
    ...(options.headers || {}),
  };

  const response = await fetch(path, { ...options, headers });
  const text = await response.text();
  const data = text ? parseJson(text) : null;

  if (!response.ok) {
    const message = data?.message || data?.error || text || `${response.status} ${response.statusText}`;
    throw new Error(message);
  }

  return data;
}

function parseJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function money(value) {
  return Number(value || 0).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

function statusClass(status) {
  if (["COMPLETED", "DELIVERED", "COMPENSATED"].includes(status)) return "good";
  if (["FAILED", "CANCELLED", "NEEDS_MANUAL_REVIEW"].includes(status)) return "bad";
  if (["AWAITING_SHIPMENT", "SHIPMENT_REQUESTED", "SHIPMENT_CREATED", "PAYMENT_COMPLETED"].includes(status)) return "warn";
  return "";
}

function setSessionLabel() {
  $("#session-label").textContent = state.user
    ? `${state.user.email} (${state.user.role})`
    : state.token
      ? "Token saved"
      : "Not signed in";
  $("#logout-button").disabled = !state.token;
}

function setAuthMode(mode) {
  state.authMode = mode;
  $$("[data-auth-mode]").forEach((button) => {
    button.classList.toggle("active", button.dataset.authMode === mode);
  });
  $("#auth-submit").textContent = mode === "login" ? "Login" : "Register";
}

function switchTab(tab) {
  $$(".tabs button").forEach((button) => button.classList.toggle("active", button.dataset.tab === tab));
  $$(".view").forEach((view) => view.classList.toggle("active", view.id === `${tab}-view`));
}

function orderItemRow(item = {}) {
  const row = document.createElement("div");
  row.className = "order-item-row";
  row.innerHTML = `
    <label>SKU<input class="item-sku" value="${item.sku || ""}" required></label>
    <label>Name<input class="item-name" value="${item.productName || ""}" required></label>
    <label>Qty<input class="item-qty" min="1" step="1" type="number" value="${item.quantity || 1}" required></label>
    <label>Price<input class="item-price" min="0.01" step="0.01" type="number" value="${item.price || ""}" required></label>
    <button class="icon-button remove-item" title="Remove item" type="button">-</button>
  `;
  row.querySelector(".remove-item").addEventListener("click", () => {
    if ($$(".order-item-row").length > 1) row.remove();
  });
  return row;
}

function getPageContent(data) {
  return Array.isArray(data) ? data : data?.content || [];
}

async function loadMe() {
  if (!state.token) {
    state.user = null;
    setSessionLabel();
    return;
  }

  try {
    state.user = await api("/api/v1/auth/me");
  } catch {
    state.user = null;
  }
  setSessionLabel();
}

async function loadProducts() {
  const list = $("#products-list");
  list.innerHTML = "";
  try {
    const products = await api("/api/v1/products");
    if (!products.length) {
      list.innerHTML = `<p class="muted">No products</p>`;
      return;
    }
    products.forEach((product) => {
      const card = document.createElement("article");
      card.className = "card";
      card.innerHTML = `
        <div class="card-title">
          <strong>${product.name}</strong>
          <span class="badge">${product.sku}</span>
        </div>
        <div class="kv">
          <span>Price <strong>${money(product.price)}</strong></span>
          <span>Stock <strong>${product.stock}</strong></span>
          <span>Reserved <strong>${product.reservedStock}</strong></span>
        </div>
        <div class="card-actions">
          <input class="stock-input" type="number" min="0" step="1" value="${product.stock}">
          <button class="secondary update-stock" type="button">Update stock</button>
          <button class="secondary use-product" type="button">Use in order</button>
        </div>
      `;
      card.querySelector(".update-stock").addEventListener("click", async () => {
        const stock = Number(card.querySelector(".stock-input").value);
        await api(`/api/v1/products/${encodeURIComponent(product.sku)}/stock`, {
          method: "PATCH",
          body: JSON.stringify({ stock }),
        });
        toast("Stock updated");
        loadProducts();
      });
      card.querySelector(".use-product").addEventListener("click", () => {
        switchTab("orders");
        const row = orderItemRow({
          sku: product.sku,
          productName: product.name,
          quantity: 1,
          price: product.price,
        });
        $("#order-items").appendChild(row);
      });
      list.appendChild(card);
    });
  } catch (error) {
    list.innerHTML = `<p class="muted">${error.message}</p>`;
  }
}

async function loadOrders() {
  const list = $("#orders-list");
  const status = $("#order-status-filter").value;
  const query = status ? `?status=${encodeURIComponent(status)}` : "";
  list.innerHTML = "";
  try {
    const data = await api(`/api/v1/orders${query}`);
    const orders = getPageContent(data);
    if (!orders.length) {
      list.innerHTML = `<p class="muted">No orders</p>`;
      return;
    }
    orders.forEach((order) => {
      const card = document.createElement("article");
      card.className = "card";
      const items = (order.items || [])
        .map((item) => `${item.quantity} x ${item.productName} (${item.sku})`)
        .join(", ");
      card.innerHTML = `
        <div class="card-title">
          <strong>Order #${order.id}</strong>
          <span class="badge ${statusClass(order.status)}">${order.status}</span>
        </div>
        <div class="kv">
          <span>Email <strong>${order.customerEmail}</strong></span>
          <span>Total <strong>${money(order.totalAmount)}</strong></span>
          <span>Created <strong>${order.createdAt || "-"}</strong></span>
          <span>Updated <strong>${order.updatedAt || "-"}</strong></span>
        </div>
        <p class="meta">${items || "No items"}</p>
        <div class="card-actions">
          <select class="status-select">
            ${["CREATED", "PROCESSING", "AWAITING_SHIPMENT", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED", "FAILED"]
              .map((value) => `<option ${value === order.status ? "selected" : ""}>${value}</option>`)
              .join("")}
          </select>
          <button class="secondary update-status" type="button">Set status</button>
          <button class="secondary inspect-order" type="button">Inspect</button>
        </div>
      `;
      card.querySelector(".update-status").addEventListener("click", async () => {
        const nextStatus = card.querySelector(".status-select").value;
        await api(`/api/v1/orders/${order.id}/status`, {
          method: "PATCH",
          body: JSON.stringify({ status: nextStatus, updatedAt: new Date().toISOString() }),
        });
        toast("Order status updated");
        loadOrders();
      });
      card.querySelector(".inspect-order").addEventListener("click", () => {
        switchTab("request");
        $("#request-method").value = "GET";
        $("#request-path").value = `/api/v1/orders/${order.id}`;
        $("#request-body").value = "{}";
      });
      list.appendChild(card);
    });
  } catch (error) {
    list.innerHTML = `<p class="muted">${error.message}</p>`;
  }
}

async function loadSagas() {
  const list = $("#sagas-list");
  const status = $("#saga-status-filter").value;
  const query = status ? `?status=${encodeURIComponent(status)}` : "";
  list.innerHTML = "";
  try {
    const data = await api(`/api/v1/admin/sagas${query}`);
    const sagas = getPageContent(data);
    if (!sagas.length) {
      list.innerHTML = `<p class="muted">No sagas</p>`;
      return;
    }
    sagas.forEach((saga) => {
      const card = document.createElement("article");
      card.className = "card";
      card.innerHTML = `
        <div class="card-title">
          <strong>Saga #${saga.id} / Order #${saga.orderId}</strong>
          <span class="badge ${statusClass(saga.status)}">${saga.status}</span>
        </div>
        <div class="kv">
          <span>Step <strong>${saga.currentStep}</strong></span>
          <span>Event <strong>${saga.lastEventType || "-"}</strong></span>
          <span>Retries <strong>${saga.retryCount}/${saga.maxRetries}</strong></span>
          <span>Failure <strong>${saga.failureReason || "-"}</strong></span>
        </div>
        <div class="card-actions">
          <button class="secondary retry-saga" type="button">Retry</button>
          <button class="secondary review-saga" type="button">Manual review</button>
          <button class="secondary resolve-saga" type="button">Resolve completed</button>
        </div>
      `;
      card.querySelector(".retry-saga").addEventListener("click", async () => {
        await api(`/api/v1/admin/sagas/${saga.id}/retry`, { method: "POST", body: "{}" });
        toast("Saga retry requested");
        loadSagas();
      });
      card.querySelector(".review-saga").addEventListener("click", async () => {
        await api(`/api/v1/admin/sagas/${saga.id}/manual-review`, {
          method: "POST",
          body: JSON.stringify({ reason: "Marked from UI" }),
        });
        toast("Saga moved to manual review");
        loadSagas();
      });
      card.querySelector(".resolve-saga").addEventListener("click", async () => {
        await api(`/api/v1/admin/sagas/${saga.id}/manual-resolve`, {
          method: "POST",
          body: JSON.stringify({ finalStatus: "COMPLETED", reason: "Resolved from UI" }),
        });
        toast("Saga resolved");
        loadSagas();
      });
      list.appendChild(card);
    });
  } catch (error) {
    list.innerHTML = `<p class="muted">${error.message}</p>`;
  }
}

async function refreshAll() {
  await Promise.allSettled([loadMe(), loadProducts(), loadOrders(), loadSagas()]);
}

function bindEvents() {
  $$("[data-auth-mode]").forEach((button) => {
    button.addEventListener("click", () => setAuthMode(button.dataset.authMode));
  });

  $$(".tabs button").forEach((button) => {
    button.addEventListener("click", () => switchTab(button.dataset.tab));
  });

  $("#auth-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const email = $("#auth-email").value.trim();
    const password = $("#auth-password").value;
    try {
      if (state.authMode === "register") {
        await api("/api/v1/auth/register", {
          method: "POST",
          body: JSON.stringify({ email, password }),
        });
        toast("User registered");
        setAuthMode("login");
        return;
      }
      const result = await api("/api/v1/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      state.token = result.token;
      localStorage.setItem("orderPlatformToken", state.token);
      await refreshAll();
      toast("Signed in");
    } catch (error) {
      toast(error.message);
    }
  });

  $("#logout-button").addEventListener("click", () => {
    state.token = "";
    state.user = null;
    localStorage.removeItem("orderPlatformToken");
    setSessionLabel();
    toast("Signed out");
  });

  $("#add-order-item").addEventListener("click", () => $("#order-items").appendChild(orderItemRow()));

  $("#order-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const items = $$(".order-item-row").map((row) => ({
      sku: row.querySelector(".item-sku").value.trim(),
      productName: row.querySelector(".item-name").value.trim(),
      quantity: Number(row.querySelector(".item-qty").value),
      price: Number(row.querySelector(".item-price").value),
    }));
    try {
      await api("/api/v1/orders", {
        method: "POST",
        body: JSON.stringify({ customerEmail: $("#order-email").value.trim(), items }),
      });
      toast("Order created");
      loadOrders();
      loadSagas();
    } catch (error) {
      toast(error.message);
    }
  });

  $("#product-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      await api("/api/v1/products", {
        method: "POST",
        body: JSON.stringify({
          name: $("#product-name").value.trim(),
          sku: $("#product-sku").value.trim(),
          price: Number($("#product-price").value),
          stock: Number($("#product-stock").value),
        }),
      });
      event.target.reset();
      toast("Product created");
      loadProducts();
    } catch (error) {
      toast(error.message);
    }
  });

  $("#refresh-all").addEventListener("click", refreshAll);
  $("#refresh-orders").addEventListener("click", loadOrders);
  $("#refresh-products").addEventListener("click", loadProducts);
  $("#refresh-sagas").addEventListener("click", loadSagas);
  $("#order-status-filter").addEventListener("change", loadOrders);
  $("#saga-status-filter").addEventListener("change", loadSagas);

  $$("[data-health]").forEach((button) => {
    button.addEventListener("click", async () => {
      try {
        const data = await api(button.dataset.health);
        toast(typeof data === "string" ? data : JSON.stringify(data));
      } catch (error) {
        toast(error.message);
      }
    });
  });

  $("#send-request").addEventListener("click", async () => {
    const method = $("#request-method").value;
    const path = $("#request-path").value.trim();
    const bodyText = $("#request-body").value.trim();
    const output = $("#request-output");
    try {
      const options = { method };
      if (!["GET", "DELETE"].includes(method) && bodyText) {
        options.body = JSON.stringify(parseJson(bodyText));
      }
      const data = await api(path, options);
      output.textContent = JSON.stringify(data, null, 2);
    } catch (error) {
      output.textContent = error.message;
    }
  });
}

function init() {
  bindEvents();
  setAuthMode("login");
  $("#order-items").appendChild(orderItemRow());
  setSessionLabel();
  refreshAll();
}

init();
