// Logout function
function logout() {
  // Clear all session storage
  sessionStorage.clear();
  
  // Redirect to homepage or login page
  window.location.href = "/homepage.html";
}

document.addEventListener("DOMContentLoaded", () => {
	const customerId = parseInt(sessionStorage.getItem("customerId"));
	if (!customerId) {
	  alert("Customer ID not found. Please log in again.");
	  window.location.href = "/homepage.html"; // or login page
	}
	console.log("Parsed customerId from session:", customerId);

  const mainContent = document.getElementById("main-content");

  const accountDetailsBtn = document.getElementById("menu-account-details");
  const accountBalanceBtn = document.getElementById("menu-account-balance");
  const updateDetailsBtn = document.getElementById("menu-update-details");
  const payeeDetailsBtn = document.getElementById("menu-payee-details");
  const transferMoneyBtn = document.getElementById("menu-transfer-money");
  const accountStatementBtn = document.getElementById("menu-account-statement");

  let accounts = [];
  let selectedAccount = null;
  const accountSelect = document.getElementById("accountSelect");

  // Fetch accounts and fill summary + dropdown
  fetch(`/api/accounts/customer/${customerId}`)
    .then(res => {
      console.log("Response status:", res.status); // Debug log
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${res.statusText}`);
      }
      return res.json();
    })
    .then(data => {
      console.log("Accounts response:", data); // Debug log
      accounts = Array.isArray(data) ? data : [];
      if (accounts.length === 0) {
        console.log("No accounts found for customer:", customerId);
        accountSelect.innerHTML = '<option value="">No accounts found</option>';
        updateAccountSummary(null);
        loadRecentTransactions(null);
        return;
      }
      // Populate dropdown
      accountSelect.innerHTML = accounts.map(acc =>
        `<option value="${acc.accountNo}">${acc.accountNo} (${acc.accountType})</option>`
      ).join('');
      selectedAccount = accounts[0];
      accountSelect.value = selectedAccount.accountNo;
      updateAccountSummary(selectedAccount);
      loadRecentTransactions(selectedAccount.accountNo);
    })
    .catch(err => {
      console.error("Error fetching accounts:", err);
      alert("Failed to load account data. Please check if you have any accounts or contact support.");
      accountSelect.innerHTML = '<option value="">Error loading accounts</option>';
      updateAccountSummary(null);
      loadRecentTransactions(null);
    });

  // Change summary and transactions on selection
  accountSelect.addEventListener("change", () => {
    selectedAccount = accounts.find(acc => acc.accountNo == accountSelect.value);
    updateAccountSummary(selectedAccount);
    loadRecentTransactions(selectedAccount.accountNo);
  });

  // --- Account Details ---
  accountDetailsBtn?.addEventListener("click", async () => {
    try {
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) {
        // Handle specific case for "no accounts found"
        if (response.status === 404) {
          const errorData = await response.json();
          if (errorData.error === "Account Not Found") {
            mainContent.innerHTML = `<div class="alert alert-warning"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
            return;
          }
        }
        throw new Error(`Failed to fetch account details: ${response.status} ${response.statusText}`);
      }
      const accounts = await response.json();

      if (accounts.length === 0) {
        mainContent.innerHTML = `<div class="alert alert-info"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
        return;
      }

      const account = accounts[0];
      mainContent.innerHTML = `
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h2 class="mb-0">Account Details</h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="col-md-6">
                <div class="card mb-3">
                  <div class="card-header bg-info text-white">Account Info</div>
                  <div class="card-body">
                    <p><strong>Account No:</strong> ${account.accountNo}</p>
                    <p><strong>Account Type:</strong> ${account.accountType}</p>
                    <p><strong>Status:</strong> <span class="badge bg-success">${account.status}</span></p>
                    <p><strong>Balance:</strong> ₹${account.balance.toFixed(2)}</p>
                    <p><strong>Application Date:</strong> ${account.applicationDate}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card">
                  <div class="card-header bg-info text-white">Customer Info</div>
                  <div class="card-body">
                    <p><strong>Name:</strong> ${account.fullName}</p>
                    <p><strong>Email:</strong> ${account.email}</p>
                    <p><strong>Mobile:</strong> ${account.mobileNo}</p>
                    <p><strong>Aadhar No:</strong> ${account.aadharNo}</p>
                    <p><strong>PAN No:</strong> ${account.panNo}</p>
                    <p><strong>Occupation:</strong> ${account.occupation}</p>
                    <p><strong>Annual Income:</strong> ₹${account.annualIncome.toLocaleString()}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
    } catch (error) {
      console.error("Error loading account details:", error);
      mainContent.innerHTML = `<div class="alert alert-danger"><h2>Error loading account details. Please try again later.</h2><p>${error.message}</p></div>`;
    }
  });

  // --- Account Balance ---
  accountBalanceBtn?.addEventListener("click", async () => {
    try {
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) {
        // Handle specific case for "no accounts found"
        if (response.status === 404) {
          const errorData = await response.json();
          if (errorData.error === "Account Not Found") {
            mainContent.innerHTML = `<div class="alert alert-warning"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
            return;
          }
        }
        throw new Error(`Failed to fetch account data: ${response.status} ${response.statusText}`);
      }
      const accounts = await response.json();

      if (accounts.length === 0) {
        mainContent.innerHTML = `<div class="alert alert-info"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
        return;
      }

      const account = accounts[0];
      mainContent.innerHTML = `
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h2 class="mb-0">Account Balance</h2>
          </div>
          <div class="card-body">
            <p><strong>Account No:</strong> ${account.accountNo}</p>
            <p><strong>Balance:</strong> ₹${account.balance.toFixed(2)}</p>
            <canvas id="balanceChart" width="400" height="200"></canvas>
          </div>
        </div>
      `;

      const ctx = document.getElementById('balanceChart').getContext('2d');
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Available Balance'],
          datasets: [{
            label: '₹ Balance',
            data: [account.balance],
            backgroundColor: '#8D1B3D',
            borderColor: '#8D1B3D',
            borderWidth: 1,
            borderRadius: 8
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                callback: value => '₹' + value.toLocaleString()
              }
            }
          }
        }
      });
    } catch (error) {
      console.error("Error loading balance:", error);
      mainContent.innerHTML = `<div class="alert alert-danger"><h2>Error loading balance.</h2><p>${error.message}</p></div>`;
    }
  });

  // --- Update Details ---
  updateDetailsBtn?.addEventListener("click", async () => {
    try {
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) {
        // Handle specific case for "no accounts found"
        if (response.status === 404) {
          const errorData = await response.json();
          if (errorData.error === "Account Not Found") {
            mainContent.innerHTML = `<div class="alert alert-warning"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
            return;
          }
        }
        throw new Error(`Failed to fetch customer details: ${response.status} ${response.statusText}`);
      }
      const accounts = await response.json();
      const account = accounts[0];

      mainContent.innerHTML = `
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h2 class="mb-0">Update Contact Details</h2>
          </div>
          <div class="card-body">
            <form id="update-form">
              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" name="email" value="${account.email}" required />
              </div>
              <div class="mb-3">
                <label for="mobileNo" class="form-label">Mobile</label>
                <input type="text" class="form-control" id="mobileNo" name="mobileNo" value="${account.mobileNo}" required />
              </div>
              <button type="submit" class="btn btn-primary">Update</button>
            </form>
            <div id="update-status" class="mt-3"></div>
          </div>
        </div>
      `;

      document.getElementById("update-form").addEventListener("submit", async (e) => {
        e.preventDefault();
        const updatedEmail = e.target.email.value;
        const updatedMobile = e.target.mobileNo.value;

        try {
          const updateResponse = await fetch(`/api/accounts/${account.customerId}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: updatedEmail, mobileNo: updatedMobile })
          });

          if (!updateResponse.ok) throw new Error("Failed to update");
          document.getElementById("update-status").innerHTML = "<div class='alert alert-success'>Details updated successfully!</div>";
        } catch (err) {
          console.error("Update error:", err);
          document.getElementById("update-status").innerHTML = "<div class='alert alert-danger'>Failed to update. Try again later.</div>";
        }
      });
    } catch (error) {
      console.error("Error loading update form:", error);
      mainContent.innerHTML = `<div class="alert alert-danger"><h2>Error loading update form.</h2><p>${error.message}</p></div>`;
    }
  });

  // --- Payee Details (View/Add/Delete) ---
  payeeDetailsBtn?.addEventListener("click", () => {
    fetchPayees();
  });

  function fetchPayees() {
    fetch(`/api/accounts/customer/${customerId}/payees`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch payee data.");
        return response.json();
      })
      .then(payees => {
        console.log("Payees data:", payees); // Debugging line
        if (!Array.isArray(payees) || payees.length === 0) {
          mainContent.innerHTML = `
            <div class="card">
              <div class="card-header bg-primary text-white">
                <h2 class="mb-0">Payee Details</h2>
              </div>
              <div class="card-body">
                <p class="text-muted">No payees found.</p>
                <button id="addPayeeBtn" class="btn btn-success">Add Payee</button>
              </div>
            </div>`;
          document.getElementById("addPayeeBtn")?.addEventListener("click", showAddPayeeForm);
          return;
        }

        // Debugging: Log each payee to see the structure
        payees.forEach(p => console.log("Payee:", p));

        const rows = payees.map((p, index) => `
          <tr>
            <td>${p.payeeName}</td>
            <td>${p.bankName}</td>
            <td>${p.payeeAccountNumber}</td>
            <td>${p.ifscCode}</td>
            <td>
              <button class="btn btn-sm btn-info view-payee-btn" data-payee-id="${p.id || 'index-' + index}">View</button>
              <button class="btn btn-sm btn-danger delete-payee-btn" data-payee-id="${p.id || 'index-' + index}">Delete</button>
            </td>
          </tr>`).join("");

        mainContent.innerHTML = `
          <div class="card">
            <div class="card-header bg-primary text-white">
              <h2 class="mb-0">Payee Details</h2>
            </div>
            <div class="card-body">
              <button id="addPayeeBtn" class="btn btn-success mb-3">Add Payee</button>
              <div class="table-responsive">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Bank</th>
                      <th>Account Number</th>
                      <th>IFSC Code</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>${rows}</tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- Delete Confirmation Modal -->
          <div class="modal fade" id="deletePayeeModal" tabindex="-1" aria-labelledby="deletePayeeModalLabel" aria-hidden="true">
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="deletePayeeModalLabel">Confirm Delete</h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  Are you sure you want to delete this payee?
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                  <button type="button" class="btn btn-danger" id="confirmDeletePayeeBtn">Delete</button>
                </div>
              </div>
            </div>
          </div>
        `;

        document.getElementById("addPayeeBtn")?.addEventListener("click", showAddPayeeForm);
        
        // Add event listeners for delete buttons
        document.querySelectorAll(".delete-payee-btn").forEach(button => {
          button.addEventListener("click", function() {
            const payeeIdToDelete = this.getAttribute("data-payee-id");
            const deleteModal = new bootstrap.Modal(document.getElementById('deletePayeeModal'));
            deleteModal.show();

            document.getElementById('confirmDeletePayeeBtn').onclick = () => {
              // Remove focus before hiding modal to avoid aria-hidden warning
              document.getElementById('confirmDeletePayeeBtn').blur();
              deletePayee(payeeIdToDelete);
              deleteModal.hide();
            };
          });
        });
        
        // Add event listeners for view buttons
        document.querySelectorAll(".view-payee-btn").forEach(button => {
          button.addEventListener("click", function() {
            const payeeId = this.getAttribute("data-payee-id");
            showPayeeDetails(payeeId);
          });
        });
      })
      .catch(error => {
        console.error("Fetch error:", error);
        mainContent.innerHTML = `<div class="alert alert-danger">Failed to load payee data.</div>`;
      });
  }

  function showPayeeDetails(payeeId) {
    // In a real application, you would fetch payee details by ID
    // For now, we'll just show an alert with the ID
    alert(`Viewing details for payee with ID: ${payeeId}`);
  }

  function showAddPayeeForm() {
    mainContent.innerHTML = `
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h2 class="mb-0">Add New Payee</h2>
        </div>
        <div class="card-body">
          <form id="addPayeeForm">
            <div class="mb-3">
              <label for="payeeName" class="form-label">Payee Name</label>
              <input type="text" class="form-control" id="payeeName" name="payeeName" required />
            </div>
            <div class="mb-3">
              <label for="bankName" class="form-label">Bank Name</label>
              <input type="text" class="form-control" id="bankName" name="bankName" required />
            </div>
            <div class="mb-3">
              <label for="accountNumber" class="form-label">Account Number</label>
              <input type="text" class="form-control" id="accountNumber" name="accountNumber" required />
            </div>
            <div class="mb-3">
              <label for="ifscCode" class="form-label">IFSC Code</label>
              <input type="text" class="form-control" id="ifscCode" name="ifscCode" required />
            </div>
            <button type="submit" class="btn btn-success">Add Payee</button>
            <button type="button" id="cancelPayeeBtn" class="btn btn-secondary">Cancel</button>
          </form>
          <div id="payee-status" class="mt-3"></div>
        </div>
      </div>
    `;

    document.getElementById("addPayeeForm")?.addEventListener("submit", async (e) => {
      e.preventDefault();
      const payeeData = {
        payeeName: e.target.payeeName.value,
        bankName: e.target.bankName.value,
        payeeAccountNumber: e.target.accountNumber.value,
        ifscCode: e.target.ifscCode.value,
        customerId: customerId
      };

      try {
        const response = await fetch(`/api/payee-ui/add`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payeeData)
        });

        if (!response.ok) throw new Error("Failed to add payee.");
        document.getElementById("payee-status").innerHTML = "<div class='alert alert-success'>Payee added successfully!</div>";
        setTimeout(() => document.getElementById("quickPayeeBtn").click(), 1000);
      } catch (error) {
        document.getElementById("payee-status").innerHTML = "<div class='alert alert-danger'>Failed to add payee. Please try again.</div>";
      }
    });

    document.getElementById("cancelPayeeBtn")?.addEventListener("click", () => {
      document.getElementById("quickPayeeBtn").click();
    });
  }

  // Delete Payee
  function deletePayee(payeeId) {
    if (!payeeId) {
      alert("Cannot delete payee: Invalid payee ID");
      return;
    }
    fetch(`/api/payee-ui/delete/${payeeId}`, {
      method: "DELETE"
    })
    .then(response => {
      if (!response.ok) throw new Error("Failed to delete payee.");
      document.getElementById("quickPayeeBtn").click();
    })
    .catch(error => {
      alert("Failed to delete payee. Please try again.");
    });
  }

  // --- Contact Us ---
  document.getElementById("menu-contact-us")?.addEventListener("click", () => {
    mainContent.innerHTML = `
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h2 class="mb-0">Contact Us</h2>
        </div>
        <div class="card-body">
          <p><strong>Email:</strong> support@QBank.com</p>
          <p><strong>Phone:</strong> 1800-123-4567</p>
          <p><strong>Address:</strong> 101 QBank Towers, Mumbai, India</p>
        </div>
      </div>
    `;
  });

  // --- Transfer Money ---
  transferMoneyBtn?.addEventListener("click", async () => {
    try {
      // First, get the customer's accounts to get the account number
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch account data: ${response.status} ${response.statusText}`);
      }
      const accounts = await response.json();
      
      if (!accounts || accounts.length === 0) {
        mainContent.innerHTML = `<div class="alert alert-info"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
        return;
      }
      
      const account = accounts[0]; // Use the first account for now
      const accountNo = account.accountNo;
      
      // Fetch payees for this account
      const payeesResponse = await fetch(`/api/accounts/customer/${customerId}/payees`);
      let payees = [];
      if (payeesResponse.ok) {
        payees = await payeesResponse.json(); // fixed error here
      }
      
      // Create payee options for the dropdown
      const payeeOptions = payees.map(payee => 
        `<option value="${payee.id}" data-account="${payee.payeeAccountNumber}">${payee.payeeName} - ${payee.payeeAccountNumber}</option>`
      ).join('');
      
      mainContent.innerHTML = `
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h2 class="mb-0">Transfer Money</h2>
          </div>
          <div class="card-body">
            <form id="transfer-form">
              <div class="mb-3">
                <label for="fromAccount" class="form-label">From Account:</label>
                <input type="text" class="form-control" id="fromAccount" name="fromAccount" value="${accountNo}" readonly />
              </div>
              <div class="mb-3">
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" id="transferToPayee" name="transferType" value="payee" checked>
                  <label class="form-check-label" for="transferToPayee">Transfer to Saved Payee</label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" id="transferToManual" name="transferType" value="manual">
                  <label class="form-check-label" for="transferToManual">Transfer to Manual Account</label>
                </div>
              </div>
              <div id="payeeSelection" class="mb-3">
                <label for="toPayee" class="form-label">To Payee:</label>
                <select class="form-select" id="toPayee" name="toPayee">
                  <option value="">Select a payee</option>
                  ${payeeOptions}
                </select>
              </div>
              <div id="manualAccountInput" style="display: none;" class="mb-3">
                <div class="mb-3">
                  <label for="manualAccountNumber" class="form-label">To Account Number:</label>
                  <input type="text" class="form-control" id="manualAccountNumber" name="manualAccountNumber" />
                </div>
                <div class="mb-3">
                  <label for="manualIFSC" class="form-label">IFSC Code:</label>
                  <input type="text" class="form-control" id="manualIFSC" name="manualIFSC" />
                </div>
              </div>
              <div class="mb-3">
                <label for="amount" class="form-label">Amount (₹):</label>
                <input type="number" class="form-control" id="amount" name="amount" min="1" step="0.01" required />
              </div>
              <div class="mb-3">
                <label for="transactionPassword" class="form-label">Transaction Password:</label>
                <input type="password" class="form-control" id="transactionPassword" name="transactionPassword" required />
              </div>
              <div class="mb-3">
                <label for="remarks" class="form-label">Remarks:</label>
                <textarea class="form-control" id="remarks" name="remarks"></textarea>
              </div>
              <button type="submit" class="btn btn-primary">Transfer Money</button>
            </form>
            <div id="transfer-status" class="mt-3"></div>
          </div>
        </div>
      `;
      
      // Add event listeners for transfer type radio buttons
      document.getElementById("transferToPayee").addEventListener("change", function() {
        document.getElementById("payeeSelection").style.display = "block";
        document.getElementById("manualAccountInput").style.display = "none";
        document.getElementById("toPayee").required = true;
        document.getElementById("manualAccountNumber").required = false;
        document.getElementById("manualIFSC").required = false;
      });
      
      document.getElementById("transferToManual").addEventListener("change", function() {
        document.getElementById("payeeSelection").style.display = "none";
        document.getElementById("manualAccountInput").style.display = "block";
        document.getElementById("toPayee").required = false;
        document.getElementById("manualAccountNumber").required = true;
        document.getElementById("manualIFSC").required = true;
      });
      
      // Add event listener for form submission
      document.getElementById("transfer-form").addEventListener("submit", async (e) => {
        e.preventDefault();
        
        const transferType = document.querySelector('input[name="transferType"]:checked').value;
        const amount = parseFloat(e.target.amount.value);
        const transactionPassword = e.target.transactionPassword.value;
        const remarks = e.target.remarks.value;
        
        if (amount <= 0) {
          document.getElementById("transfer-status").innerHTML = "<div class='alert alert-danger'>Please enter a valid amount.</div>";
          return;
        }
        
        // Prepare the transfer request
        const transferData = {
          customerId: customerId,
          accountNumber: accountNo,
          amount: amount,
          transactionPassword: transactionPassword,
          remarks: remarks
        };
        
        // Add payeeId or manual account details based on transfer type
        if (transferType === "payee") {
          const toPayeeId = e.target.toPayee.value;
          if (!toPayeeId) {
            document.getElementById("transfer-status").innerHTML = "<div class='alert alert-danger'>Please select a payee.</div>";
            return;
          }
          transferData.payeeId = toPayeeId;
        } else {
          const manualAccountNumber = e.target.manualAccountNumber.value;
          const manualIFSC = e.target.manualIFSC.value;
          
          if (!manualAccountNumber || !manualIFSC) {
            document.getElementById("transfer-status").innerHTML = "<div class='alert alert-danger'>Please enter account number and IFSC code.</div>";
            return;
          }
          
          // For manual transfers, we'll use the manual account number as payeeId for now
          // In a real implementation, you might want to create a temporary payee
          transferData.payeeId = manualAccountNumber;
          transferData.manualAccountNumber = manualAccountNumber;
          transferData.manualIFSC = manualIFSC;
        }
        
        try {
          // Make the transfer API call
          const transferResponse = await fetch(`/api/accounts/${accountNo}/transfer`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(transferData)
          });
          
          if (!transferResponse.ok) {
            const errorData = await transferResponse.json();
            throw new Error(errorData.message || "Transfer failed");
          }
          
          const result = await transferResponse.json();
          document.getElementById("transfer-status").innerHTML = 
            `<div class='alert alert-success'>Transfer successful! Transaction ID: ${result.id}</div>`;
          
          // Reset the form
          e.target.reset();
        } catch (err) {
          console.error("Transfer error:", err);
          document.getElementById("transfer-status").innerHTML = 
            `<div class='alert alert-danger'>Transfer failed: ${err.message}</div>`;
        }
      });
    } catch (error) {
      console.error("Error loading transfer form:", error);
      mainContent.innerHTML = `<div class="alert alert-danger"><h2>Error loading transfer form.</h2><p>${error.message}</p></div>`;
    }
  });

  // --- Account Statement ---
  accountStatementBtn?.addEventListener("click", async () => {
    try {
      // First, get the customer's accounts to get the account number
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch account data: ${response.status} ${response.statusText}`);
      }
      const accounts = await response.json();
      
      if (!accounts || accounts.length === 0) {
        mainContent.innerHTML = `<div class="alert alert-info"><h2>No account data available.</h2><p>You don't have any accounts yet. Please contact the bank to create an account.</p></div>`;
        return;
      }
      
      const account = accounts[0]; // Use the first account for now
      const accountNo = account.accountNo;
      
      // Fetch transaction history for this account from statement service (port 8085)
      const transactionsResponse = await fetch(`http://localhost:8085/api/accounts/${accountNo}/transactions`);
      let transactions = [];
      if (transactionsResponse.ok) {
        transactions = await transactionsResponse.json();
      }
      
      // Create transaction rows for the table
      const transactionRows = transactions.map(transaction => `
        <tr>
          <td>${transaction.id}</td>
          <td>${transaction.transactionDate}</td>
          <td>${transaction.transactionType}</td>
          <td>${transaction.amount}</td>
          <td>${transaction.remarks || 'N/A'}</td>
          <td>${transaction.status}</td>
        </tr>
      `).join("");
      
      mainContent.innerHTML = `
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h2 class="mb-0">Account Statement</h2>
          </div>
          <div class="card-body">
            <div class="mb-3">
              <p><strong>Account No:</strong> ${accountNo}</p>
              <p><strong>Account Holder:</strong> ${account.fullName}</p>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="startDate" class="form-label">Start Date:</label>
                <input type="date" class="form-control" id="startDate">
              </div>
              <div class="col-md-6">
                <label for="endDate" class="form-label">End Date:</label>
                <input type="date" class="form-control" id="endDate">
              </div>
            </div>
            <button id="filterStatementBtn" class="btn btn-primary mb-3">Filter Statement</button>
            <button id="downloadStatementBtn" class="btn btn-secondary mb-3 ms-2">Download as PDF</button>
            <div class="table-responsive">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th>Transaction ID</th>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Amount (₹)</th>
                    <th>Remarks</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  ${transactionRows || '<tr><td colspan="6">No transactions found.</td></tr>'}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      `;

      // Add event listeners for filter and download buttons
      document.getElementById("filterStatementBtn")?.addEventListener("click", () => {
        const startDate = document.getElementById("startDate").value;
        const endDate = document.getElementById("endDate").value;
        alert(`Filtering statement from ${startDate} to ${endDate}`);
        // Implement actual filtering logic here
      });

      document.getElementById("downloadStatementBtn")?.addEventListener("click", () => {
        alert("Downloading statement as PDF...");
        // Implement actual PDF download logic here
      });

    } catch (error) {
      console.error("Error loading account statement:", error);
      mainContent.innerHTML = `<div class="alert alert-danger"><h2>Error loading account statement.</h2><p>${error.message}</p></div>`;
    }
  });

  // --- Date/Time Display ---
  const datetimeElem = document.getElementById("datetime");
  if (datetimeElem) {
    const now = new Date();
    datetimeElem.textContent = now.toLocaleString();
  }

  // Quick Actions
  document.getElementById("quickTransferBtn")?.addEventListener("click", () => {
    // Use selectedAccount if available, else fallback to first account in accounts
    const account = selectedAccount || accounts[0];
    if (!account) {
      alert("No account selected for transfer.");
      return;
    }
    window.location.href = `http://localhost:8083/index.html?customerId=${customerId}&accountNo=${account.accountNo}`;
  });

  document.getElementById("quickPayeeBtn")?.addEventListener("click", () => {
    // Redirect to payee management UI and send customerId as query param
    window.location.href = `http://localhost:8082/index.html?customerId=${customerId}`;
  });

  document.getElementById("quickStatementBtn")?.addEventListener("click", () => {
    // Redirect to statement UI on port 8085, passing accountNo and customerId
    if (!selectedAccount) {
      alert("No account selected.");
      return;
    }
    window.location.href = `http://localhost:8085/index1.html?accountNo=${selectedAccount.accountNo}&customerId=${customerId}`;
  });
});

// Helper functions for account summary and transactions
function updateAccountSummary(account) {
  if (!account) return;
  document.getElementById("summaryAccountNo").textContent = account.accountNo || "---";
  document.getElementById("summaryAccountType").textContent = account.accountType || "---";
  document.getElementById("summaryStatus").textContent = account.status || "---";
  document.getElementById("summaryBalance").textContent = account.balance ? account.balance.toFixed(2) : "0.00";
}

function loadRecentTransactions(accountNo) {
  if (!accountNo) return;

  // Fetch transactions from your backend for this accountNo
  fetch(`/api/accounts/${accountNo}/transactions`)
    .then(response => {
      if (!response.ok) throw new Error("Failed to fetch transactions");
      return response.json();
    })
    .then(transactions => {
      const tbody = document.getElementById("transactionsBody");
      if (!transactions || transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No recent transactions</td></tr>';
        return;
      }
      // Show only the latest 5 transactions
      const recentTransactions = transactions.slice(0, 5);
      tbody.innerHTML = recentTransactions.map(transaction => `
        <tr>
          <td>${transaction.transactionDate || "---"}</td>
          <td>${transaction.remarks || transaction.transactionType || "---"}</td>
          <td>₹${transaction.amount ? transaction.amount.toFixed(2) : "0.00"}</td>
          <td>${transaction.transactionType || "---"}</td>
        </tr>
      `).join("");
    })
    .catch(error => {
      console.error("Error loading transactions:", error);
      document.getElementById("transactionsBody").innerHTML =
        '<tr><td colspan="4" class="text-center text-muted">Error loading transactions</td></tr>';
    });
}


