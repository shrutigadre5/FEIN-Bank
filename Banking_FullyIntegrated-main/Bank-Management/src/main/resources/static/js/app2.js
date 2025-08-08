// ====== Config ======
const BASE_URL = "/api/transactions"; // <-- relative path (same origin as Spring Boot)

// ====== State ======
const state = {
  accountNumber: "",
  type: "ALL",
  from: "",
  to: "",
  sortBy: "transactionDate",
  direction: "DESC",
  page: 0,
  size: 10,
  lastResultCount: 0,
  latestN: 5 // for the "Latest N" quick action
};

// ====== Elements ======
const els = {
  form: document.getElementById("filterForm"),
  accountNumber: document.getElementById("accountNumber"),
  type: document.getElementById("type"),
  from: document.getElementById("from"),
  to: document.getElementById("to"),
  sortBy: document.getElementById("sortBy"),
  direction: document.getElementById("direction"),
  size: document.getElementById("size"),
  pageInfo: document.getElementById("pageInfo"),
  prevBtn: document.getElementById("prevBtn"),
  nextBtn: document.getElementById("nextBtn"),
  resultsBody: document.getElementById("resultsBody"),
  latestBtn: document.getElementById("latestBtn"),
  clearBtn: document.getElementById("clearBtn"),
  exportBtn: document.getElementById("exportBtn"),
  resultMeta: document.getElementById("resultMeta"),
  loadingOverlay: document.getElementById("loadingOverlay"),
  toast: document.getElementById("toast"),
  toastMsg: document.getElementById("toastMsg"),

  // For balance display
  balanceArea: document.getElementById("balanceArea"),
  balanceValue: document.getElementById("balanceValue"),
};

// ====== Utils ======
function showLoading(show) {
  els.loadingOverlay.style.display = show ? "flex" : "none";
}

function showError(msg) {
  els.toastMsg.textContent = msg || "Something went wrong";
  const toastObj = new bootstrap.Toast(els.toast, { delay: 4000 });
  toastObj.show();
}

function buildSearchUrl() {
  const params = new URLSearchParams();
  params.set("type", state.type || "ALL");
  if (state.from) params.set("from", state.from);
  if (state.to) params.set("to", state.to);
  params.set("sortBy", state.sortBy || "transactionDate");
  params.set("direction", state.direction || "DESC");
  params.set("page", state.page ?? 0);
  params.set("size", state.size ?? 10);
  return `${BASE_URL}/${encodeURIComponent(state.accountNumber)}/statement/search?${params.toString()}`;
}

function formatAmount(v) {
  if (v == null) return "";
  try {
    return Number(v).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  } catch { return v; }
}

function formatDateTime(s) {
  if (!s) return "";
  try {
    const d = new Date(s);
    if (isNaN(d.getTime())) return s;
    return d.toLocaleString();
  } catch { return s; }
}

function renderRows(items) {
  els.resultsBody.innerHTML = "";
  if (!Array.isArray(items) || items.length === 0) {
    els.resultsBody.innerHTML = `<tr><td colspan="8" class="text-center text-muted py-4">No data</td></tr>`;
    return;
  }
  const rows = items.map(it => {
    const badge = it.txnType === "DEBIT"
      ? `<span class="badge badge-debit">DEBIT</span>`
      : `<span class="badge badge-credit">CREDIT</span>`;
    return `
      <tr>
        <td>${it.transactionId ?? ""}</td>
        <td>${formatDateTime(it.transactionDate)}</td>
        <td class="fw-semibold">${formatAmount(it.amount)}</td>
        <td>${badge}</td>
        <td>${it.counterpartyAccount ?? ""}</td>
        <td>${it.paymentMethod ?? ""}</td>
        <td>${it.status ?? ""}</td>
        <td>${it.remarks ?? ""}</td>
      </tr>
    `;
  }).join("");
  els.resultsBody.innerHTML = rows;
}

function updateMeta(items) {
  state.lastResultCount = Array.isArray(items) ? items.length : 0;
  els.pageInfo.textContent = `Page ${state.page + 1}`;
  els.prevBtn.disabled = state.page <= 0;
  els.nextBtn.disabled = state.lastResultCount < state.size;

  const summary = [];
  summary.push(`Rows: ${state.lastResultCount}`);
  summary.push(`Type: ${state.type}`);
  if (state.from) summary.push(`From: ${state.from}`);
  if (state.to) summary.push(`To: ${state.to}`);
  els.resultMeta.textContent = summary.join(" • ");
}

// Show or hide balance display area with formatted value
function showBalance(value) {
  if (value !== undefined && value !== null) {
    els.balanceValue.textContent = formatAmount(value);
    els.balanceArea.style.display = "inline-block";
  } else {
    els.balanceValue.textContent = "";
    els.balanceArea.style.display = "none";
  }
}

// ====== Events ======

// Normal search (no balance shown)
els.form.addEventListener("submit", (ev) => {
  ev.preventDefault();
  state.accountNumber = els.accountNumber.value.trim();
  state.type = els.type.value;
  state.from = els.from.value || "";
  state.to = els.to.value || "";
  state.sortBy = els.sortBy.value;
  state.direction = els.direction.value;
  state.page = 0;
  state.size = parseInt(els.size.value, 10) || 10;
  fetchAndRender();
});

els.prevBtn.addEventListener("click", () => {
  if (state.page > 0) {
    state.page -= 1;
    fetchAndRender();
  }
});

els.nextBtn.addEventListener("click", () => {
  state.page += 1;
  fetchAndRender();
});

els.clearBtn.addEventListener("click", () => {
  els.type.value = "ALL";
  els.from.value = "";
  els.to.value = "";
  els.sortBy.value = "transactionDate";
  els.direction.value = "DESC";
  els.size.value = "10";
  state.page = 0;
  state.type = "ALL";
  state.from = "";
  state.to = "";
  state.sortBy = "transactionDate";
  state.direction = "DESC";
  state.size = 10;
  fetchAndRender();
});

els.exportBtn.addEventListener("click", () => {
  const rows = [["Txn ID","Date","Amount","Type","Counterparty","Method","Status","Remarks"]];
  document.querySelectorAll("#resultsBody tr").forEach(tr => {
    const cols = Array.from(tr.querySelectorAll("td")).map(td => td.textContent.replace(/\s+/g, " ").trim());
    if (cols.length) rows.push(cols);
  });
  if (rows.length <= 1) {
    showError("Nothing to export.");
    return;
  }
  const csvContent = rows.map(r => r.map(cell => `"${String(cell).replaceAll('"','""')}"`).join(",")).join("\n");
  const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `statement_${state.accountNumber || "account"}_${new Date().toISOString().slice(0,10)}.csv`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
});

// Latest N button fetches statement with balance
els.latestBtn.addEventListener("click", async () => {
  state.type = "ALL";
  state.from = "";
  state.to = "";
  state.page = 0;
  state.size = state.latestN;
  els.type.value = "ALL";
  els.from.value = "";
  els.to.value = "";
  els.size.value = String(state.latestN);

  if (!state.accountNumber) {
    showError("Please enter an account number.");
    return;
  }

  showLoading(true);
  try {
    const url = `${BASE_URL}/${encodeURIComponent(state.accountNumber)}/statementWithBalance?count=${state.latestN}`;
    const res = await fetch(url, { headers: { "Accept": "application/json" } });
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    renderRows(data.transactions || []);
    updateMeta(data.transactions || []);
    showBalance(data.balance);
  } catch (e) {
    showError(e.message);
    showBalance(null);
  } finally {
    showLoading(false);
  }
});

// Header quick-sort clicks
document.querySelectorAll("th[data-sort]").forEach(th => {
  th.addEventListener("click", () => {
    const field = th.getAttribute("data-sort");
    if (state.sortBy === field) {
      state.direction = (state.direction === "ASC" ? "DESC" : "ASC");
    } else {
      state.sortBy = field;
      state.direction = "ASC";
    }
    els.sortBy.value = state.sortBy;
    els.direction.value = state.direction;
    state.page = 0;
    fetchAndRender();
  });
});

// Fetch and render normal paginated search (balance hidden)
async function fetchAndRender() {
  if (!state.accountNumber) {
    showError("Please enter an account number.");
    return;
  }
  showLoading(true);
  try {
    const url = buildSearchUrl();
    const res = await fetch(url, { headers: { "Accept": "application/json" } });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || `HTTP ${res.status}`);
    }
    const data = await res.json();
    renderRows(data);
    updateMeta(data);
    showBalance(null); // Hide balance for normal queries
  } catch (e) {
    console.error(e);
    showError(e.message);
    showBalance(null);
  } finally {
    showLoading(false);
  }
}

// ====== Initial load ======
els.accountNumber.value = "ACC001";
state.accountNumber = "ACC001";
fetchAndRender();
