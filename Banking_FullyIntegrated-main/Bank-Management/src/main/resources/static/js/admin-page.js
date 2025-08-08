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

document.getElementById("accountBtn").addEventListener("click", () => {
    window.location.href="/admin-info.html"
  });

document.getElementById("logoutBtn").addEventListener("click", () => {
    sessionStorage.clear();
    window.location.href = "/homepage.html";
});



document.addEventListener("DOMContentLoaded", () => {
  fetch("http://localhost:8081/bank/admin/request-count")
    .then(response => response.json())
    .then(data => {
      document.getElementById("pendingRequestCount").textContent = data;
    })
    .catch(error => {
      console.error("Error fetching request count:", error);
      document.getElementById("pendingRequestCount").textContent = "Error";
    });

  fetch("http://localhost:8081/bank/admin/customers/count")
    .then(response => response.json())
    .then(data => {
      document.getElementById("customerCount").textContent = data;
    })
    .catch(error => {
      console.error("Error fetching customer count:", error);
      document.getElementById("customerCount").textContent = "Error";
    });
	
	fetch("http://localhost:8081/bank/admin/accounts/status-count")
	  .then(response => response.json())
	  .then(data => {
	    const labels = Object.keys(data);
	    const values = Object.values(data);

	    const pieCtx = document.getElementById('pieChart').getContext('2d');
	    new Chart(pieCtx, {
	      type: 'pie',
	      data: {
	        labels: labels,
	        datasets: [{
	          data: values,
	          backgroundColor: ['#096102', '#b80006'],
	          borderWidth: 1
	        }]
	      },
	      options: {
	        responsive: true,
	        plugins: {
	          legend: {
	            position: 'bottom'
	          }
	        }
	      }
	    });
	  })
	  .catch(error => {
	    console.error("Failed to load pie chart data:", error);
	  });
	  
	  // Bar Chart for Account Type Distribution
	  fetch("http://localhost:8081/bank/admin/accounts/type-count")
	    .then(response => response.json())
	    .then(data => {
	      const labels = Object.keys(data);
	      const values = Object.values(data);

	      const barCtx = document.getElementById('barChart').getContext('2d');
	      new Chart(barCtx, {
	        type: 'bar',
	        data: {
	          labels: labels,
	          datasets: [{
	            label: 'Number of Accounts',
	            data: values,
	            backgroundColor: ['#007bff', '#28a745', '#ffc107'],
	            borderWidth: 1
	          }]
	        },
	        options: {
	          responsive: true,
	          scales: {
	            y: {
	              beginAtZero: true,
	              ticks: {
	                stepSize: 1
	              }
	            }
	          },
	          plugins: {
	            legend: {
	              display: false
	            }
	          }
	        }
	      });
	    })
	    .catch(error => {
	      console.error("Failed to load bar chart data:", error);
	    });

});

window.addEventListener("popstate", function () {
    window.location.href = "/admin-login.html";
  });


