// CAPTCHA functionality
class CaptchaManager {
    constructor() {
        this.currentCode = '';
        this.generateCaptcha();
        this.initEventListeners();
    }

    generateCaptcha() {
        // Generate 5-6 digit random number
        const length = Math.random() < 0.5 ? 5 : 6;
        let code = '';
        
        // First digit should not be 0
        code += Math.floor(Math.random() * 9) + 1;
        
        // Generate remaining digits
        for (let i = 1; i < length; i++) {
            code += Math.floor(Math.random() * 10);
        }
        
        this.currentCode = code;
        document.getElementById('captchaQuestion').textContent = code;
        document.getElementById('captchaInput').value = '';
        document.getElementById('captchaInput').setAttribute('maxlength', length);
        this.hideCaptchaError();
    }

    validateCaptcha() {
        const userInput = document.getElementById('captchaInput').value.trim();
        return userInput === this.currentCode;
    }

    showCaptchaError() {
        document.getElementById('captchaError').style.display = 'block';
        document.getElementById('captchaInput').style.borderColor = '#dc3545';
    }

    hideCaptchaError() {
        document.getElementById('captchaError').style.display = 'none';
        document.getElementById('captchaInput').style.borderColor = '#ddd';
    }

    initEventListeners() {
        document.getElementById('refreshCaptcha').addEventListener('click', () => {
            this.generateCaptcha();
        });

        document.getElementById('captchaInput').addEventListener('input', () => {
            this.hideCaptchaError();
            
            // Auto-format input - only allow numbers
            const input = document.getElementById('captchaInput');
            input.value = input.value.replace(/[^0-9]/g, '');
        });

        // Auto-submit when user types the complete code
        document.getElementById('captchaInput').addEventListener('input', (e) => {
            if (e.target.value.length === this.currentCode.length) {
                if (this.validateCaptcha()) {
                    this.hideCaptchaError();
                    e.target.style.borderColor = '#28a745';
                } else {
                    this.showCaptchaError();
                }
            }
        });
    }
}

// Initialize CAPTCHA
const captcha = new CaptchaManager();

// Enhanced login form handler
document.getElementById("loginForm").addEventListener("submit", function (event) {
    event.preventDefault();

    // Validate CAPTCHA first
    if (!captcha.validateCaptcha()) {
        captcha.showCaptchaError();
        captcha.generateCaptcha(); // Generate new CAPTCHA on wrong answer
        return;
    }

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const loginButton = document.getElementById("loginButton");
    
    // Disable button during request
    loginButton.disabled = true;
    loginButton.textContent = "Logging in...";

    fetch("/bank/admin/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
    })
    .then(response => {
        if (response.status === 302 || response.status === 200) {
            return response.json();
        } else {
            throw new Error("Login failed");
        }
    })
    .then(data => {
        if (data && data.message === "Successfully logged in") {
            sessionStorage.setItem("adminId", data.adminId);
            sessionStorage.setItem("username", data.username);
            sessionStorage.setItem("name", data.name);

            document.getElementById("error").style.color = "green";
            document.getElementById("error").textContent = "Successfully logged in!";
            
            // Redirect after short delay
            setTimeout(() => {
                window.location.href = "/admin-page.html";
            }, 1000);
        } else {
            document.getElementById("error").textContent = data.message || "Invalid credentials.";
            captcha.generateCaptcha(); // Generate new CAPTCHA on login failure
        }
    })
    .catch(err => {
        document.getElementById("error").textContent = "Login failed. Please try again.";
        console.error(err);
        captcha.generateCaptcha(); // Generate new CAPTCHA on error
    })
    .finally(() => {
        // Re-enable button
        loginButton.disabled = false;
        loginButton.textContent = "Login";
    });
});

window.addEventListener("popstate", function () {
    window.location.href = "/home-page.html";
});