// Admin Account Page JavaScript
document.addEventListener("DOMContentLoaded", function () {
    // Check if admin is logged in and load basic data
    checkAuthenticationAndLoadData();
    
    // Initialize event listeners
    initializeEventListeners();
    
    // Load admin data into the page
    loadAdminData();
    
    // Update last login time
    updateLastLoginTime();
});

// Check authentication and load basic data using your existing function
function checkAuthenticationAndLoadData() {
    const adminName = sessionStorage.getItem("name");
    const adminId = sessionStorage.getItem("adminId");
    const username = sessionStorage.getItem("username");

    // Check if all required data is present
    if (!adminName || !adminId || !username) {
        alert("Please log in first!");
        window.location.href = "/admin-login.html";
        return;
    }

    // Set admin name in the welcome heading (your existing code)
    if (adminName) {
        document.getElementById("adminName").textContent = adminName;
        document.getElementById("profileName").innerHTML = `${adminName}<br><small>Admin</small>`;
    }
}

// Load admin data into the account details section
function loadAdminData() {
    const adminId = sessionStorage.getItem("adminId");
    const username = sessionStorage.getItem("username");
    const name = sessionStorage.getItem("name");

    // Update profile card
    const displayNameElement = document.getElementById("displayName");
    if (displayNameElement && name) {
        displayNameElement.textContent = name;
    }

    // Update account details
    const adminIdDisplay = document.getElementById("adminIdDisplay");
    const usernameDisplay = document.getElementById("usernameDisplay");
    const fullNameDisplay = document.getElementById("fullNameDisplay");

    if (adminIdDisplay && adminId) {
        adminIdDisplay.textContent = adminId;
    }

    if (usernameDisplay && username) {
        usernameDisplay.textContent = username;
    }

    if (fullNameDisplay && name) {
        fullNameDisplay.textContent = name;
    }

    // Show success message
    showNotification('success', '✅ Admin profile loaded successfully!');
}

// Initialize event listeners
function initializeEventListeners() {
    // Navigation buttons
    const dashboardBtn = document.getElementById("dashboardBtn");
    const pendingBtn = document.getElementById("pendingBtn");
    const accountBtn = document.getElementById("accountBtn");
    const logoutBtn = document.getElementById("logoutBtn");
    const logoutMainBtn = document.getElementById("logoutMainBtn");
    const refreshDataBtn = document.getElementById("refreshDataBtn");

    if (dashboardBtn) {
        dashboardBtn.addEventListener("click", () => {
            window.location.href = "/admin-page.html";
        });
    }

    if (pendingBtn) {
        pendingBtn.addEventListener("click", () => {
            window.location.href = "/request-page.html";
        });
    }

    if (accountBtn) {
        accountBtn.addEventListener("click", () => {
            // Already on account page, just refresh data
            loadAdminData();
        });
    }

    // Logout functionality
    if (logoutBtn) {
        logoutBtn.addEventListener("click", handleLogout);
    }

    if (logoutMainBtn) {
        logoutMainBtn.addEventListener("click", handleLogout);
    }

    // Refresh data functionality
    if (refreshDataBtn) {
        refreshDataBtn.addEventListener("click", () => {
            refreshDataBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Refreshing...';
            refreshDataBtn.disabled = true;
            
            setTimeout(() => {
                loadAdminData();
                refreshDataBtn.innerHTML = '<i class="fas fa-sync-alt"></i> Refresh Data';
                refreshDataBtn.disabled = false;
                showNotification('success', '✅ Data refreshed successfully!');
            }, 1000);
        });
    }
}

// Handle logout functionality
function handleLogout() {
    if (confirm("Are you sure you want to logout?")) {
        // Clear session storage
        sessionStorage.clear();
        
        // Show logout message
        showNotification('success', '✅ Logged out successfully!');
        
        // Redirect to login page after a short delay
        setTimeout(() => {
            window.location.href = "/admin-login.html";
        }, 1500);
    }
}

// Update last login time
function updateLastLoginTime() {
    const lastLoginElement = document.getElementById("lastLoginTime");
    if (lastLoginElement) {
        const now = new Date();
        const timeString = now.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        lastLoginElement.textContent = timeString;
    }
}

// Notification system (reusing from your request page)
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
    
    // Auto remove after 4 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 4000);
}

