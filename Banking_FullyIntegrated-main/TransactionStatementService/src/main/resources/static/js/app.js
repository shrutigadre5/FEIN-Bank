// ====== Configuration ======
const BASE_URL = "/api/transactions";

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
};

// ====== DOM Elements ======
const els = {
  form: document.getElementById("filterForm"),
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
  accountLabel: document.getElementById("accountLabel"),
  balanceArea: document.getElementById("balanceArea"),
  balanceValue: document.getElementById("balanceValue")
};

// ====== Utility Functions ======
function showLoading(show) {
  els.loadingOverlay.style.display = show ? "flex" : "none";
}

function showError(msg) {
  els.toastMsg.textContent = msg || "Something went wrong";
  const toastObj = new bootstrap.Toast(els.toast, { delay: 4000 });
  toastObj.show();
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
    return new Date(s).toLocaleString("en-US", {
      year: "numeric", month: "short", day: "2-digit",
      hour: "2-digit", minute: "2-digit"
    });
  } catch { return s; }
}

function getStatusClass(status) {
  switch (status) {
    case 'COMPLETED': return 'bg-success';
    case 'PENDING': return 'bg-warning';
    case 'FAILED': return 'bg-danger';
    default: return 'bg-secondary';
  }
}

// ====== URL Parameter Parsing ======
function parseUrlParams() {
  const urlParams = new URLSearchParams(window.location.search);
  
  const accountNo = urlParams.get('accountNo');
  const type = urlParams.get('type');
  const from = urlParams.get('from');
  const to = urlParams.get('to');
  const sortBy = urlParams.get('sortBy');
  const direction = urlParams.get('direction');
  const size = urlParams.get('size');
  
  if (accountNo) {
    state.accountNumber = accountNo;
    els.accountLabel.textContent = `Account: ${accountNo}`;
  }
  
  if (type) {
    els.type.value = type;
    state.type = type;
  }
  
  if (from) {
    els.from.value = from;
    state.from = from;
  }
  
  if (to) {
    els.to.value = to;
    state.to = to;
  }
  
  if (sortBy) {
    els.sortBy.value = sortBy;
    state.sortBy = sortBy;
  }
  
  if (direction) {
    els.direction.value = direction;
    state.direction = direction;
  }
  
  if (size) {
    els.size.value = size;
    state.size = parseInt(size, 10) || 10;
  }
  
  updatePageInfo();
  
  return !!accountNo;
}

// ====== API Functions ======
function buildSearchUrl() {
  if (!state.accountNumber) {
    throw new Error("No account number provided");
  }
  
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

async function fetchAndRender() {
  if (!state.accountNumber) {
    showError("No account number provided in URL parameters");
    return;
  }
  
  showLoading(true);
  
  try {
    const url = buildSearchUrl();
    const res = await fetch(url, { headers: { "Accept": "application/json" } });
    
    if (!res.ok) {
      const text = await res.text();
      if (res.status === 404) {
        throw new Error("No transactions found for the provided account.");
      } else if (res.status === 400) {
        throw new Error("Invalid request parameters. Please check your input.");
      } else {
        throw new Error(text || `HTTP ${res.status}`);
      }
    }
    
    const data = await res.json();
    renderRows(data);
    updateMeta(data);
    await fetchAccountBalance();
    
  } catch (e) {
    console.error(e);
    showError(e.message);
  } finally {
    showLoading(false);
  }
}

async function fetchLatestTransactions(limit = 10) {
  if (!state.accountNumber) {
    showError("No account number provided");
    return;
  }

  showLoading(true);
  
  try {
    const url = `${BASE_URL}/${encodeURIComponent(state.accountNumber)}/statement/latest?limit=${limit}`;
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    const data = await response.json();
    renderRows(data);
    els.resultMeta.textContent = `Latest ${data.length} transactions`;
    await fetchAccountBalance();
    
  } catch (error) {
    console.error('Error fetching latest transactions:', error);
    showError(`Failed to fetch latest transactions: ${error.message}`);
  } finally {
    showLoading(false);
  }
}

async function fetchAccountBalance() {
  if (!state.accountNumber) return;
  
  try {
    const url = `${BASE_URL}/${encodeURIComponent(state.accountNumber)}/statementWithBalance?count=1`;
    const response = await fetch(url);
    
    if (!response.ok) return;
    
    const data = await response.json();
    
    if (data && data.balance !== undefined) {
      els.balanceValue.textContent = Number(data.balance).toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
      els.balanceArea.style.display = 'inline-block';
    }
  } catch (err) {
    console.log('Could not fetch balance:', err.message);
  }
}

function renderRows(items) {
  els.resultsBody.innerHTML = "";
  if (!Array.isArray(items) || items.length === 0) {
    els.resultsBody.innerHTML = `<tr><td colspan="9" class="text-center text-muted py-4">No transactions found</td></tr>`;
    return;
  }
  
  const rows = items.map(it => {
    const amount = parseFloat(it.amount || 0);
    const isCredit = amount > 0;
    const amountClass = isCredit ? 'text-success' : 'text-danger';
    const amountPrefix = isCredit ? '+' : '-';
    
    const typeClass = it.txnType === "CREDIT" ? 'bg-success' : 'bg-danger';
    const statusClass = getStatusClass(it.status);
    
    const balanceAfter = it.balanceAfterTxn ? formatAmount(it.balanceAfterTxn) : '-';
    
    return `
      <tr>
        <td><strong>${it.transactionId ?? ""}</strong></td>
        <td>${formatDateTime(it.transactionDate)}</td>
        <td class="${amountClass}">
          ${amountPrefix}₹${Math.abs(amount).toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}
        </td>
        <td><span class="badge ${typeClass}">${it.txnType ?? ""}</span></td>
        <td>${it.counterpartyAccount ?? ""}</td>
        <td>${it.paymentMethod ?? ""}</td>
        <td><span class="badge ${statusClass}">${it.status ?? ""}</span></td>
        <td>${it.remarks ?? ""}</td>
        <td class="${amountClass}">₹${balanceAfter}</td>
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

// ====== State Management ======
function updateState() {
  state.type = els.type.value;
  state.from = els.from.value || "";
  state.to = els.to.value || "";
  state.sortBy = els.sortBy.value;
  state.direction = els.direction.value;
  state.size = parseInt(els.size.value, 10) || 10;
}

function updateStateFromForm() {
  updateState();
  state.page = 0;
  updatePageInfo();
}

function updatePageInfo() {
  if (els.pageInfo) {
    els.pageInfo.textContent = `Page ${state.page + 1}`;
  }
}

// ====== Event Listeners ======
els.form.addEventListener("submit", (ev) => {
  ev.preventDefault();
  updateStateFromForm();
  fetchAndRender();
});

els.prevBtn.addEventListener("click", () => {
  if (state.page > 0) {
    state.page -= 1;
    updatePageInfo();
    fetchAndRender();
  }
});

els.nextBtn.addEventListener("click", () => {
  state.page += 1;
  updatePageInfo();
  fetchAndRender();
});

els.clearBtn.addEventListener("click", () => {
  els.type.value = "ALL";
  els.from.value = "";
  els.to.value = "";
  els.sortBy.value = "transactionDate";
  els.direction.value = "DESC";
  els.size.value = "10";
  
  state.type = "ALL";
  state.from = "";
  state.to = "";
  state.sortBy = "transactionDate";
  state.direction = "DESC";
  state.page = 0;
  state.size = 10;
  
  updatePageInfo();
  fetchAndRender();
});

els.latestBtn.addEventListener("click", () => {
  fetchLatestTransactions(10);
});

els.exportBtn.addEventListener("click", () => {
  const rows = [["Txn ID","Date","Amount","Type","Counterparty","Method","Status","Remarks","Balance After"]];
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
  a.download = `statement_${state.accountNumber || "unknown"}_${new Date().toISOString().slice(0,10)}.csv`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
});

// Header click sorting
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

// ====== Initialization ======
document.addEventListener('DOMContentLoaded', () => {
  if (parseUrlParams()) {
    if (state.accountNumber) {
      fetchAndRender();
    }
  } else {
    els.resultsBody.innerHTML = `
      <tr>
        <td colspan="9" class="text-center py-4">
          <div class="text-muted">
            <i class="bi bi-info-circle fs-1"></i>
            <p class="mt-2">Please provide an account number in the URL</p>
            <small>Example: ?accountNo=ACC001</small>
          </div>
        </td>
      </tr>
    `;
  }
});
