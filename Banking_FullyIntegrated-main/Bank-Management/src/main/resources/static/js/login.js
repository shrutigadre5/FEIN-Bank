let generatedCaptcha = '';
let attempts = 0;

// Generate random captcha text
function generateCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
  generatedCaptcha = '';
  for (let i = 0; i < 6; i++) {
    generatedCaptcha += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  document.getElementById('captchaCode').textContent = generatedCaptcha;
  document.getElementById('captchaInput').value = '';
  validateCaptcha();
}

// Enable or disable login button based on CAPTCHA match
function validateCaptcha() {
  const input = document.getElementById('captchaInput').value;
  const loginBtn = document.getElementById('loginBtn');
  loginBtn.disabled = (input !== generatedCaptcha);
}

// When DOM is fully loaded
window.addEventListener('DOMContentLoaded', () => {
  generateCaptcha();

  document.getElementById('refreshCaptcha').addEventListener('click', generateCaptcha);
  document.getElementById('captchaInput').addEventListener('input', validateCaptcha);

  document.getElementById('loginForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const enteredCaptcha = document.getElementById('captchaInput').value;
    const loginBtn = document.getElementById('loginBtn');

    if (enteredCaptcha !== generatedCaptcha) {
      alert("Incorrect CAPTCHA");
      generateCaptcha();
      attempts++;
      if (attempts >= 3) {
        loginBtn.disabled = true;
        alert("Too many incorrect attempts. Login locked.");
      }
      return;
    }

    const customerId = document.getElementById('customerId').value;
    const password = document.getElementById('password').value;

    // TODO: Add your backend call here (fetch POST /api/customers/login)
    console.log("Login submitted for:", customerId, password);

    // Sample dummy login success response
	fetch("http://localhost:8081/api/customers/login", {
	  method: "POST",
	  headers: {
	    "Content-Type": "application/json"
	  },
	  body: JSON.stringify({
	    customerId: customerId,
	    loginPassword: password
	  })
	})
	.then(response => {
	  if (!response.ok) {
	    throw new Error("Invalid credentials");
	  }
	  return response.json();
	})
	.then(data => {
	  console.log("Login success response:", data);

	  // Store customerId in sessionStorage
	  sessionStorage.setItem("customerId", data.customerId); // Or data.id if that's the field

	  // Redirect to dashboard
	  window.location.href = "http://localhost:8081/account.html";
	})

	
	.catch(error => {
	  alert("❌ Login failed: " + error.message);
	  attempts++;
	  if (attempts >= 3) {
	    document.getElementById('loginBtn').disabled = true;
	    alert("Login locked after 3 failed attempts.");
	  }
	});
	});
	});
