document.getElementById("loginForm").addEventListener("submit", function (event) {
  event.preventDefault();

  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value.trim();

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
      window.location.href = "/admin-page.html";
    } else {
      document.getElementById("error").textContent = data.message || "Invalid credentials.";
    }
  })
  .catch(err => {
    document.getElementById("error").textContent = "Login failed. Please try again.";
    console.error(err);
  });
});

window.addEventListener("popstate", function () {
    window.location.href = "/home-page.html";
  });

