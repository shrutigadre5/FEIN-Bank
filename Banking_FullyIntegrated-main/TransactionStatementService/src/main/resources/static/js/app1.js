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

function parseUrlParams() {
  const urlParams = new URLSearchParams(window.location.search);
  const accountNo = urlParams.get('accountNo');
  
  if (accountNo) {
    state.accountNumber = accountNo;
    els.accountLabel.textContent = `Account: ${accountNo}`;
    return true;
  }
  return false;
}

// ====== API Functions ======
async function fetchTransactions() {
  if (!state.accountNumber) {
    showError("No account number provided in URL parameters");
    return;
  }

  showLoading(true);
  
  try {
    // Check if we have filters or pagination
    const hasFilters = state.type !== "ALL" || state.from || state.to || state.page > 0;
    
    let url;
    if (hasFilters) {
      // Use search endpoint with filters
      const params = new URLSearchParams();
      params.set("type", state.type);
      if (state.from) params.set("from", state.from);
      if (state.to) params.set("to", state.to);
      params.set("sortBy", state.sortBy);
      params.set("direction", state.direction);
      params.set("page", state.page);
      params.set("size", state.size);
      
      url = `${BASE_URL}/${encodeURIComponent(state.accountNumber)}/statement/search?${params.toString()}`;
    } else {
      // Use simple all transactions endpoint
      url = `${BASE_URL}/${encodeURIComponent(state.accountNumber)}`;
    }
    
    const response = await fetch(url);
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    const data = await response.json();
    
    if (hasFilters) {
      renderStatementResults(data);
    } else {
      renderEntityResults(data);
    }
    
    // Fetch account balance
    await fetchAccountBalance();
    
  } catch (error) {
    console.error('Error fetching transactions:', error);
    showError(`Failed to fetch transactions: ${error.message}`);
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
    renderStatementResults(data);
    els.resultMeta.textContent = `Latest ${data.length} transactions`;
    
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

// ====== Rendering Functions ======
function renderStatementResults(data) {
  const rows = data.map(item => {
    const amount = parseFloat(item.amount || 0);
    const isCredit = amount > 0;
    const amountClass = isCredit ? 'text-success' : 'text-danger';
    const amountPrefix = isCredit ? '+' : '';
    
    const formattedDate = item.transactionDate ? 
      new Date(item.transactionDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }) : 'N/A';
    
    const statusClass = getStatusClass(item.status);
    const typeClass = item.type === 'CREDIT' ? 'text-success' : 'text-danger';
    
    return `
      <tr>
        <td><strong>${item.transactionId || 'N/A'}</strong></td>
        <td>${formattedDate}</td>
        <td class="${amountClass}">
          ${amountPrefix}₹${Math.abs(amount).toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}
        </td>
        <td><span class="badge ${typeClass === 'text-success' ? 'bg-success' : 'bg-danger'}">${item.type || 'N/A'}</span></td>
        <td>${item.counterparty || 'N/A'}</td>
        <td>${item.paymentMethod || 'N/A'}</td>
        <td><span class="badge ${statusClass}">${item.status || 'N/A'}</span></td>
        <td>${item.remarks || 'No remarks'}</td>
        <td class="${amountClass}">₹${(item.balanceAfter || 0).toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
      </tr>
    `;
  }).join("");
  
  els.resultsBody.innerHTML = rows;
  updateMeta(data);
}

function renderEntityResults(data) {
  const rows = data.map(txn => {
    const amount = parseFloat(txn.amount || 0);
    const isCredit = amount > 0;
    const amountClass = isCredit ? 'text-success' : 'text-danger';
    const amountPrefix = isCredit ? '+' : '';
    
    // Determine transaction type and counterparty
    let txnType = 'UNKNOWN';
    let counterparty = 'N/A';
    
    if (txn.senderAccountNo && txn.receiverAccountNo) {
      if (txn.senderAccountNo.toString() === state.accountNumber.toString()) {
        txnType = 'DEBIT';
        counterparty = `To: ${txn.receiverAccountNo}`;
      } else if (txn.receiverAccountNo.toString() === state.accountNumber.toString()) {
        txnType = 'CREDIT';
        counterparty = `From: ${txn.senderAccountNo}`;
      }
    }
    
    const formattedDate = txn.transactionDate ? 
      new Date(txn.transactionDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }) : 'N/A';
    
    const statusClass = getStatusClass(txn.status);
    const typeClass = txnType === 'CREDIT' ? 'bg-success' : 'bg-danger';
    
    return `
      <tr>
        <td><strong>${txn.transactionId || 'N/A'}</strong></td>
        <td>${formattedDate}</td>
        <td class="${amountClass}">
          ${amountPrefix}₹${Math.abs(amount).toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}
        </td>
        <td><span class="badge ${typeClass}">${txnType}</span></td>
        <td><small>${counterparty}</small></td>
        <td>${txn.paymentMethod || 'N/A'}</td>
        <td><span class="badge ${statusClass}">${txn.status || 'UNKNOWN'}</span></td>
        <td><small>${txn.remarks || 'No remarks'}</small></td>
        <td class="${amountClass}">-</td>
      </tr>
    `;
  }).join("");
  
  els.resultsBody.innerHTML = rows;
  els.resultMeta.textContent = `Found ${data.length} transactions`;
}

function getStatusClass(status) {
  switch (status) {
    case 'COMPLETED': return 'bg-success';
    case 'PENDING': return 'bg-warning';
    case 'FAILED': return 'bg-danger';
    default: return 'bg-secondary';
  }
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

function updateStateFromForm() {
  state.type = els.type.value;
  state.from = els.from.value || "";
  state.to = els.to.value || "";
  state.sortBy = els.sortBy.value;
  state.direction = els.direction.value;
  state.size = parseInt(els.size.value, 10) || 10;
  state.page = 0; // Reset page when filters change
}

// ====== Event Listeners ======
els.form.addEventListener("submit", (ev) => {
  ev.preventDefault();
  updateStateFromForm();
  fetchTransactions();
});

els.prevBtn.addEventListener("click", () => {
  if (state.page > 0) {
    state.page -= 1;
    fetchTransactions();
  }
});

els.nextBtn.addEventListener("click", () => {
  state.page += 1;
  fetchTransactions();
});

els.clearBtn.addEventListener("click", () => {
  // Clear form fields
  els.type.value = "ALL";
  els.from.value = "";
  els.to.value = "";
  els.sortBy.value = "transactionDate";
  els.direction.value = "DESC";
  els.size.value = "10";
  
  // Reset state
  state.type = "ALL";
  state.from = "";
  state.to = "";
  state.sortBy = "transactionDate";
  state.direction = "DESC";
  state.page = 0;
  state.size = 10;
  
  fetchTransactions();
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
  a.download = `statement_${state.accountNumber}_${new Date().toISOString().slice(0,10)}.csv`;
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
    fetchTransactions();
  });
});

// ====== Initialization ======
document.addEventListener('DOMContentLoaded', () => {
  // Parse URL parameters and initialize
  if (parseUrlParams()) {
    // Account number found in URL, fetch transactions
    fetchTransactions();
  } else {
    // No account number in URL, show message
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
