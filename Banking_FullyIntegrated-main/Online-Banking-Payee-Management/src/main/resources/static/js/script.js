// Legacy JS version, no KnockoutJS

document.addEventListener('DOMContentLoaded', function () {
    const customerId = sessionStorage.getItem('customerId');

    const payeeNameInput = document.getElementById('payeeName');
    const bankNameInput = document.getElementById('bankName');
    const payeeAccountNumberInput = document.getElementById('payeeAccountNumber');
    const ifscCodeInput = document.getElementById('ifscCode');
    const nicknameInput = document.getElementById('nickname');
    const addPayeeForm = document.getElementById('addPayeeForm');
    const payeesTableBody = document.getElementById('payeesTableBody');
	
	

    function loadPayees() {
        fetch(`/api/payees/customer/${customerId}/payees`)
            .then(response => response.json())
            .then(data => {
                payeesTableBody.innerHTML = '';
                data.forEach(payee => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${payee.payeeName || ''}</td>
                        <td>${payee.bankName || ''}</td>
                        <td>${payee.payeeAccountNumber || ''}</td>
                        <td>${payee.ifscCode || ''}</td>
                        <td>${payee.nickname || ''}</td>
                        <td>
                            <button class="btn btn-sm btn-danger delete-payee" data-id="${payee.payeeId}"><i class="fas fa-trash-alt"></i> Remove</button>
                            <button class="btn btn-sm btn-primary use-payee" data-id="${payee.payeeId}"><i class="fas fa-check"></i> Use</button>
                        </td>
                    `;
                    payeesTableBody.appendChild(row);
                });
            });
    }

    addPayeeForm.addEventListener('submit', function (e) {
        e.preventDefault();
        
        // Validate required fields
        if (!payeeNameInput.value.trim()) {
            alert('Please enter payee name');
            return;
        }
        
        if (!payeeAccountNumberInput.value.trim()) {
            alert('Please enter payee account number');
            return;
        }
        
        if (!/^\d+$/.test(payeeAccountNumberInput.value.trim())) {
            alert('Please enter a valid account number (numbers only)');
            return;
        }
        
        const newPayee = {
            payeeName: payeeNameInput.value.trim(),
            bankName: bankNameInput.value.trim(),
            payeeAccountNumber: parseInt(payeeAccountNumberInput.value.trim()),
            ifscCode: ifscCodeInput.value.trim(),
            nickname: nicknameInput.value.trim(),
            customerId: customerId
            // Note: payeeId is intentionally omitted - let the database generate it
        };
        
        console.log('Adding payee:', newPayee);
        
        fetch('/api/payees/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newPayee)
        })
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Failed to add payee');
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Payee added successfully:', data);
            loadPayees();
            payeeNameInput.value = '';
            bankNameInput.value = '';
            payeeAccountNumberInput.value = '';
            ifscCodeInput.value = '';
            nicknameInput.value = '';
            alert('Payee added successfully!');
        })
        .catch(error => {
            console.error('Error adding payee:', error);
            alert('Error adding payee: ' + error.message);
        });
    });

    payeesTableBody.addEventListener('click', function (e) {
        if (e.target.classList.contains('delete-payee') || e.target.closest('.delete-payee')) {
            const btn = e.target.closest('.delete-payee');
            const payeeId = btn.getAttribute('data-id');
            if (confirm('Are you sure you want to remove this payee?')) {
                fetch(`/api/payees/${customerId}/${payeeId}`, {
                    method: 'DELETE'
                }).then(() => loadPayees());
            }
        }
        if (e.target.classList.contains('use-payee') || e.target.closest('.use-payee')) {
            const btn = e.target.closest('.use-payee');
            const payeeId = btn.getAttribute('data-id');
            // Find the payee row in the table
            const row = btn.closest('tr');
            const payeeName = row.children[0].textContent;
            const bankName = row.children[1].textContent;
            const payeeAccountNumber = row.children[2].textContent;
            const ifscCode = row.children[3].textContent;
            const nickname = row.children[4].textContent;

            // Prepare the payee object
            const payeeDetails = {
                payeeId: payeeId,
                payeeName: payeeName,
                bankName: bankName,
                payeeAccountNumber: payeeAccountNumber,
                ifscCode: ifscCode,
                nickname: nickname,
                customerId: customerId
            };

            // First, store the payee in Payee Management session
            fetch('/api/payees/store-selected', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payeeDetails)
            })
            .then(response => {
                if (response.ok) {
                    console.log('Payee stored in Payee Management session');
                    
                    // Then send to TransactionService
                    return fetch('http://localhost:8083/transactions/receive-payee', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payeeDetails)
                    });
                } else {
                    throw new Error('Failed to store payee in session');
                }
            })
            .then(response => {
                if (response.ok) {
                    alert('Payee details sent to TransactionService successfully!');
                    // Optionally redirect to TransactionService
                    window.location.href = 'http://localhost:8083/transfer-form';
                } else {
                    alert('Failed to send payee details to TransactionService.');
                }
            })
            .catch(error => {
                console.error('Error processing payee:', error);
                alert('Error: ' + error.message);
            });
        }
    });

    loadPayees();
});