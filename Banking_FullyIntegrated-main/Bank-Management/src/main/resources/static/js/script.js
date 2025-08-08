document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('accountRequestForm');

    const validators = {
        mobileNo: value => /^[6-9]\d{9}$/.test(value),
        aadharNo: value => /^\d{12}$/.test(value),
        panNo: value => /^[A-Z]{5}[0-9]{4}[A-Z]$/.test(value),
        email: value => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
    };

    const showError = (input, message) => {
        input.classList.add('invalid');
        input.title = message;
    };

    const clearError = (input) => {
        input.classList.remove('invalid');
        input.removeAttribute('title');
    };

    Object.keys(validators).forEach(id => {
        const input = document.getElementById(id);
        input.addEventListener('input', () => {
            if (validators[id](input.value.trim())) {
                clearError(input);
            } else {
                showError(input, `Invalid ${id}`);
            }
        });
    });

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        let valid = true;
        const inputs = form.querySelectorAll('input, select, textarea');

        inputs.forEach(input => {
            if (input.hasAttribute('required') && !input.value.trim()) {
                showError(input, 'Field is required');
                valid = false;
            } else {
                clearError(input);
            }
        });

        for (const [id, validate] of Object.entries(validators)) {
            const input = document.getElementById(id);
            if (!validate(input.value.trim())) {
                showError(input, `Invalid ${id}`);
                valid = false;
            }
        }

        if (!valid) {
            alert("❌ Please fill all required fields correctly.");
            return;
        }

        const data = {
            title: document.getElementById("title").value,
            firstName: document.getElementById("firstName").value,
            middleName: document.getElementById("middleName").value,
            lastName: document.getElementById("lastName").value,
            mobileNo: document.getElementById("mobileNo").value,
            email: document.getElementById("email").value,
            aadharNo: document.getElementById("aadharNo").value,
            panNo: document.getElementById("panNo").value,
            dob: document.getElementById("dob").value,
            residentialAddress: document.getElementById("residentialAddress").value,
            permanentAddress: document.getElementById("permanentAddress").value,
            occupation: document.getElementById("occupation").value,
            annualIncome: document.getElementById("annualIncome").value,
            accountType: document.getElementById("accountType").value.toUpperCase(), // ✅ Convert to UPPERCASE
            applicationDate: new Date().toISOString().split("T")[0]
        };

        fetch("http://localhost:8081/api/accountrequest", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || "Submission failed.");
                });
            }
            return response.json();
        })
        .then(result => {
            window.location.href = "/success.html"; // Redirect on success
        })
        .catch(error => {
            console.error("Error:", error);
            alert("❌ Failed to create account request: " + error.message);
        });
    });
});
