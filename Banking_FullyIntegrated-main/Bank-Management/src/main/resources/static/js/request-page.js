document.addEventListener("DOMContentLoaded", function () {
  const adminName = sessionStorage.getItem("name"); 

  if (adminName) {
    // Set admin name in the welcome heading
    document.getElementById("adminName").textContent = adminName;

    // Set admin name in the profile section
    document.getElementById("profileName").innerHTML = `${adminName}<br><small>Admin</small>`;
  }
});

document.getElementById("dashboardBtn").addEventListener("click", () => {
    window.location.href = "/admin-page.html";
});

document.getElementById("pendingBtn").addEventListener("click", () => {
    window.location.href = "/request-page.html";
});
  
document.getElementById("logoutBtn").addEventListener("click", () => {
    sessionStorage.clear();
    window.location.href = "/admin-login.html";
});

document.getElementById("accountBtn").addEventListener("click", () => {
    window.location.href="/admin-info.html"
  });

document.addEventListener("DOMContentLoaded", () => {
    fetch("http://localhost:8081/bank/admin/requests")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch requests");
            }
            return response.json();
        })
        .then(data => {
            // Fixed: Use the correct ID that matches your HTML
            const tableBody = document.getElementById("requestsTableBody");
            tableBody.innerHTML = ""; // clear existing rows

            data.forEach(request => {
                const fullName = `${request.title} ${request.firstName} ${request.middleName} ${request.lastName}`;
                const row = document.createElement("tr");

                // Updated to match your HTML table structure
                row.innerHTML = `
                    <td>${request.accountRequestID}</td>
                    <td>${fullName}</td>
                    <td>${request.mobileNo}</td>
                    <td>${request.accountType}</td>
                    <td class="actions">
                        <button class="approve" onclick="approveRequest(${request.accountRequestID})">Approve</button>
                        <button class="reject" onclick="rejectRequest(${request.accountRequestID})">Reject</button>
                        <button class="details-btn" onclick="toggleDetails(${request.accountRequestID})">
                            <i class="fas fa-eye"></i> Details
                        </button>
                    </td>
                `;

                // Create detail row that will be hidden by default
                const detailRow = document.createElement("tr");
                detailRow.id = `details-${request.accountRequestID}`;
                detailRow.style.display = "none";
                detailRow.innerHTML = `
                    <td colspan="5" style="padding: 0; border: none;">
                        <div class="details-container">
                            <div class="details-header">
                                <h4><i class="fas fa-user-circle"></i> Application Details</h4>
                                <span class="application-id">ID: ${request.accountRequestID}</span>
                            </div>
                            
                            <div class="details-grid">
                                <div class="detail-section">
                                    <h5><i class="fas fa-id-card"></i> Personal Information</h5>
                                    <div class="detail-row">
                                        <span class="detail-label">Email:</span>
                                        <span class="detail-value">${request.email}</span>
                                    </div>
                                    <div class="detail-row">
                                        <span class="detail-label">Date of Birth:</span>
                                        <span class="detail-value">${request.dob}</span>
                                    </div>
                                    <div class="detail-row">
                                        <span class="detail-label">Occupation:</span>
                                        <span class="detail-value">${request.occupation}</span>
                                    </div>
                                </div>

                                <div class="detail-section">
                                    <h5><i class="fas fa-file-alt"></i> Documents & Income</h5>
                                    <div class="detail-row">
                                        <span class="detail-label">Aadhar Number:</span>
                                        <span class="detail-value document-number">${request.aadharNo}</span>
                                    </div>
                                    <div class="detail-row">
                                        <span class="detail-label">PAN Number:</span>
                                        <span class="detail-value document-number">${request.panNo}</span>
                                    </div>
                                    <div class="detail-row">
                                        <span class="detail-label">Annual Income:</span>
                                        <span class="detail-value income">₹${request.annualIncome.toLocaleString()}</span>
                                    </div>
                                </div>

                                <div class="detail-section">
                                    <h5><i class="fas fa-calendar-alt"></i> Application Info</h5>
                                    <div class="detail-row">
                                        <span class="detail-label">Application Date:</span>
                                        <span class="detail-value">${request.applicationDate}</span>
                                    </div>
                                    <div class="detail-row">
                                        <span class="detail-label">Account Type:</span>
                                        <span class="detail-value account-type">${request.accountType}</span>
                                    </div>
                                </div>
                            </div>

                            <div class="address-section">
                                <h5><i class="fas fa-map-marker-alt"></i> Address Information</h5>
                                <div class="address-grid">
                                    <div class="address-block">
                                        <span class="address-type">Residential Address</span>
                                        <p class="address-text">${request.residentialAddress}</p>
                                    </div>
                                    <div class="address-block">
                                        <span class="address-type">Permanent Address</span>
                                        <p class="address-text">${request.permanentAddress}</p>
                                    </div>
                                </div>
                            </div>

                            <div class="remarks-section">
                                <h5><i class="fas fa-comment-alt"></i> Admin Remarks</h5>
                                <textarea class="remarks-input" id="remarks-${request.accountRequestID}" placeholder="Add your remarks for this application..."></textarea>
                            </div>
                        </div>
                    </td>
                `;

                tableBody.appendChild(row);
                tableBody.appendChild(detailRow);
            });
        })
        .catch(error => {
            console.error("Error loading data:", error);
            document.getElementById("requestsTableBody").innerHTML =
                `<tr><td colspan="5" class="text-danger">Failed to load data. Please check console for details.</td></tr>`;
        });
});

function toggleDetails(requestID) {
    const detailRow = document.getElementById(`details-${requestID}`);
    detailRow.style.display = detailRow.style.display === "none" ? "table-row" : "none";
}

// Functions for approve/reject functionality
function approveRequest(requestID) {
    const remarks = document.getElementById(`remarks-${requestID}`).value.trim();
    
    // Show confirmation dialog
    if (!confirm(`Are you sure you want to APPROVE request #${requestID}?`)) {
        return;
    }
    
    // Show loading state
    const approveBtn = event.target;
    const originalText = approveBtn.textContent;
    approveBtn.textContent = 'Processing...';
    approveBtn.disabled = true;
    
    // API call to approve the request
    fetch(`http://localhost:8081/bank/admin/handle-request/${requestID}`, {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({ 
            status: 'approved',
            remarks: remarks || 'No remarks provided'
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text(); // Your backend returns a String, not JSON
    })
    .then(data => {
        // Show success message with customer details
        showNotification('success', `✅ Request Approved Successfully!\n${data}`);
        
        // Remove the row from table with animation
        removeRequestRow(requestID);
    })
    .catch(error => {
        console.error('Error approving request:', error);
        showNotification('error', `❌ Failed to approve request: ${error.message}`);
        
        // Restore button state
        approveBtn.textContent = originalText;
        approveBtn.disabled = false;
    });
}

function rejectRequest(requestID) {
    const remarks = document.getElementById(`remarks-${requestID}`).value.trim();
    
    // Validate remarks for rejection
    if (!remarks) {
        showNotification('warning', '⚠️ Please provide remarks for rejection.');
        document.getElementById(`remarks-${requestID}`).focus();
        return;
    }
    
    // Show confirmation dialog
    if (!confirm(`Are you sure you want to REJECT request #${requestID}?\n\nRemarks: ${remarks}`)) {
        return;
    }
    
    // Show loading state
    const rejectBtn = event.target;
    const originalText = rejectBtn.textContent;
    rejectBtn.textContent = 'Processing...';
    rejectBtn.disabled = true;
    
    // API call to reject the request
    fetch(`http://localhost:8081/bank/admin/handle-request/${requestID}`, {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({ 
            status: 'rejected',
            remarks: remarks
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text(); // Your backend returns a String, not JSON
    })
    .then(data => {
        // Show success message
        showNotification('success', `✅ Request Rejected Successfully!\n${data}`);
        
        // Remove the row from table with animation
        removeRequestRow(requestID);
    })
    .catch(error => {
        console.error('Error rejecting request:', error);
        showNotification('error', `❌ Failed to reject request: ${error.message}`);
        
        // Restore button state
        rejectBtn.textContent = originalText;
        rejectBtn.disabled = false;
    });
}

// Helper function to show notifications
function showNotification(type, message) {
    // Remove existing notifications
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">×</button>
        </div>
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Helper function to remove request row with animation
function removeRequestRow(requestID) {
    const detailRow = document.getElementById(`details-${requestID}`);
    let targetMainRow = null;
    
    // Find the correct main row by checking all rows in the table body
    const allRows = document.querySelectorAll('#requestsTableBody tr');
    
    allRows.forEach(row => {
        // Skip detail rows (they have IDs starting with 'details-')
        if (row.id && row.id.startsWith('details-')) {
            return;
        }
        
        const firstCell = row.querySelector('td:first-child');
        if (firstCell && firstCell.textContent.trim() === requestID.toString()) {
            targetMainRow = row;
        }
    });
    
    if (targetMainRow) {
        // Add fade out animation to main row
        targetMainRow.style.transition = 'all 0.5s ease';
        targetMainRow.style.opacity = '0';
        targetMainRow.style.transform = 'translateX(-20px)';
        
        // Add fade out animation to detail row if it exists
        if (detailRow) {
            detailRow.style.transition = 'all 0.5s ease';
            detailRow.style.opacity = '0';
            detailRow.style.transform = 'translateX(-20px)';
        }
        
        // Remove after animation completes
        setTimeout(() => {
            if (targetMainRow.parentNode) {
                targetMainRow.remove();
            }
            if (detailRow && detailRow.parentNode) {
                detailRow.remove();
            }
            
            // Check if table is empty after removal
            const remainingMainRows = Array.from(document.querySelectorAll('#requestsTableBody tr'))
                .filter(row => !row.id || !row.id.startsWith('details-'));
            
            if (remainingMainRows.length === 0) {
                document.getElementById("requestsTableBody").innerHTML = 
                    `<tr><td colspan="5" class="text-center" style="padding: 40px; color: #6c757d; font-style: italic;">
                        <i class="fas fa-inbox" style="font-size: 24px; margin-bottom: 10px; display: block;"></i>
                        No pending requests found
                    </td></tr>`;
            }
        }, 500);
    } else {
        console.warn(`Could not find main row for request ID: ${requestID}`);
        // If we can't find the row, just refresh the page as fallback
        setTimeout(() => {
            location.reload();
        }, 2000);
    }
}