

  // Global variables
  let urlCustomerId = null;
  let urlAccountNumber = null;
  let selectedMethod = null;
  let customerData = null;
  let accountData = null;

  // Initialize page
  document.addEventListener('DOMContentLoaded', function() {
    extractUrlParameters();
    loadAccountInformation();
    setupEventListeners();
  });

  function extractUrlParameters() {
    const urlParams = new URLSearchParams(window.location.search);
    urlCustomerId = urlParams.get('customerId') || urlParams.get('customerid');
    urlAccountNumber = urlParams.get('accountNo') || urlParams.get('accountno') || urlParams.get('accountNo');
    
    if (!urlAccountNumber) {
      showError('Missing required URL parameter: accountNo (Account Number)');
      return;
    }
    
    // Display the URL parameters immediately
    if (urlCustomerId) {
      document.getElementById('customer-id').value = urlCustomerId;
    } else {
      document.getElementById('customer-id').value = 'Loading...';
    }
    document.getElementById('account-number').value = urlAccountNumber;
  }

  async function loadAccountInformation() {
    if (!urlAccountNumber) {
      showError('Account number not provided in URL');
      return;
    }
    
    try {
      // Load account information directly using account number
      const accountResponse = await fetch(`/api/account/${urlAccountNumber}`);
      const accountResult = await accountResponse.json();
      
      if (accountResponse.ok) {
        accountData = accountResult;
        customerData = accountResult.customerInfo;
        
        // Set customer ID if not provided in URL
        if (!urlCustomerId && customerData) {
          urlCustomerId = customerData.customerId.toString();
          document.getElementById('customer-id').value = urlCustomerId;
        }
        
        // Update UI with account information
        document.getElementById('customer-name').value = `${customerData.firstName} ${customerData.lastName}`;
        document.getElementById('account-balance').value = `₹${accountData.balance || 0}`;
        
        document.getElementById('account-info').innerHTML = `
          <div class="success">
            ✅ Account loaded successfully<br>
            <strong>Account Type:</strong> ${accountData.accountType || 'N/A'}<br>
            <strong>Customer ID:</strong> ${customerData.customerId}
          </div>
        `;
        document.getElementById('account-info').className = 'info-display show success';
      } else {
        throw new Error(accountResult.error || 'Account not found');
      }
      
    } catch (error) {
      showError('Error loading account information: ' + error.message);
      document.getElementById('account-info').innerHTML = `
        <div class="error">
          ❌ ${error.message}
        </div>
      `;
      document.getElementById('account-info').className = 'info-display show error';
    }
  }

  function setupEventListeners() {
    // Transfer method selection
    document.querySelectorAll('.transfer-option').forEach(option => {
      option.addEventListener('click', () => selectTransferMethod(option.dataset.method));
    });
    
    // Validation buttons
    document.getElementById('validate-ifsc').addEventListener('click', validateIfsc);
    document.getElementById('validate-account').addEventListener('click', validateAccount);
    
    // Submit button
    document.getElementById('submit-transfer').addEventListener('click', submitTransfer);
    
    // Recipient name field event listeners for debugging and user feedback
    const recipientNameField = document.getElementById('recipient-name');
    if (recipientNameField) {
      recipientNameField.addEventListener('input', function(e) {
        console.log('Recipient name changed to:', e.target.value);
      });
      
      recipientNameField.addEventListener('focus', function(e) {
        console.log('Recipient name field focused - you can type now!');
        e.target.style.borderColor = '#007bff';
      });
      
      recipientNameField.addEventListener('blur', function(e) {
        e.target.style.borderColor = '#ddd';
      });
    }
  }

  function selectTransferMethod(method) {
    selectedMethod = method;
    
    // Update UI
    document.querySelectorAll('.transfer-option').forEach(opt => opt.classList.remove('selected'));
    document.querySelector(`[data-method="${method}"]`).classList.add('selected');
    
    // Show appropriate form
    document.querySelectorAll('.transfer-form').forEach(form => form.style.display = 'none');
    document.getElementById(`${method}-form`).style.display = 'block';
    
    // Load payees if needed
    if (method === 'payee-transfer') {
      loadPayees();
    }
    
    showStep(2);
  }

  async function loadPayees() {
    // Ensure we have customer ID (either from URL or loaded from account)
    if (!urlCustomerId && customerData) {
      urlCustomerId = customerData.customerId.toString();
    }
    
    if (!urlCustomerId) {
      showError('Customer ID not available. Please ensure account information is loaded.');
      return;
    }
    
    try {
      // Fetch payees directly from database using customerId
      const response = await fetch(`http://localhost:8082/api/payees/customer/${urlCustomerId}`);
      const payees = await response.json();
      
      const payeeSelect = document.getElementById('payee-select');
      payeeSelect.innerHTML = '<option value="">Select Payee</option>';
      
      if (response.ok && payees.length > 0) {
        payees.forEach(payee => {
          const option = document.createElement('option');
          option.value = payee.payeeId || payee.id;
          // Support different field names for payee data
          const payeeName = payee.payeeName || payee.name || payee.recipientName || 'Unknown';
          const payeeAccount = payee.payeeAccountNumber || payee.accountNumber || payee.recipientAccount || 'N/A';
          option.textContent = `${payeeName} (${payeeAccount})`;
          
          // Store additional data attributes for summary display
          option.setAttribute('data-payee-name', payeeName);
          option.setAttribute('data-payee-account', payeeAccount);
          option.setAttribute('data-payee-ifsc', payee.ifscCode || payee.payeeIfsc || '');
          
          payeeSelect.appendChild(option);
        });
      } else {
        payeeSelect.innerHTML = '<option value="">No payees found</option>';
      }
    } catch (error) {
      document.getElementById('payee-select').innerHTML = '<option value="">Error loading payees</option>';
      showError('Failed to load payees: ' + error.message);
    }
  }

  async function validateIfsc() {
    const ifscCode = document.getElementById('ifsc-code').value.trim().toUpperCase();
    
    if (!ifscCode) {
      showError('Please enter IFSC code');
      return;
    }
    
    try {
      const response = await fetch(`/api/validate-ifsc/${ifscCode}`);
      const data = await response.json();
      
      const infoDiv = document.getElementById('validation-info');
      
      if (data.valid) {
        infoDiv.innerHTML = `
          <div><strong>Bank:</strong> ${data.bankName}</div>
          <div><strong>IFSC:</strong> ${data.ifscCode}</div>
        `;
        infoDiv.className = 'info-display show success';
        document.getElementById('ifsc-code').value = data.ifscCode;
      } else {
        infoDiv.innerHTML = `❌ ${data.error}`;
        infoDiv.className = 'info-display show error';
      }
    } catch (error) {
      showError('Error validating IFSC code');
    }
  }

  async function validateAccount() {
    const accountNumber = document.getElementById('recipient-account').value;
    const ifscCode = document.getElementById('ifsc-code').value.trim().toUpperCase();
    
    if (!accountNumber || !ifscCode) {
      showError('Please enter both account number and IFSC code');
      return;
    }
    
    try {
      const response = await fetch(`/api/validate-account/${accountNumber}/ifsc/${ifscCode}`);
      const data = await response.json();
      
      if (data.valid) {
        // Don't auto-fill recipient name - let user enter it manually
        showSuccess(`Account validated successfully. Please enter recipient name manually.`);
      } else {
        document.getElementById('recipient-name').value = '';
        showError(data.error);
      }
    } catch (error) {
      showError('Error validating account');
    }
  }

  async function submitTransfer() {
    console.log('submitTransfer called, selectedMethod:', selectedMethod);
    const submitBtn = document.getElementById('submit-transfer');
    const loading = document.getElementById('loading');
    
    if (!validateForm()) return;
    
    // Store original button text
    const originalText = submitBtn.textContent;
    
    submitBtn.disabled = true;
    loading.style.display = 'block';
    
    // Update button text based on transfer type
    switch (selectedMethod) {
      case 'self-deposit':
        submitBtn.textContent = '💰 Processing Deposit...';
        break;
      case 'payee-transfer':
        submitBtn.textContent = '👥 Sending to Payee...';
        break;
      case 'manual-transfer':
        submitBtn.textContent = '🏛️ Processing Transfer...';
        break;
    }
    
    try {
      const transferData = prepareTransferData();
      console.log('Transfer data prepared:', transferData);
      console.log('Transaction password being sent:', transferData.data.transactionPassword);
      const response = await fetch(transferData.url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transferData.data)
      });
      
      console.log('Response received:', response.status, response.statusText);
      const result = await response.json();
      console.log('Response body:', result);
      
      if (response.ok) {
        submitBtn.textContent = '✅ Transfer Successful!';
        
        // Use the new transaction result function
        handleTransactionSuccess(result);
      } else {
        submitBtn.textContent = '❌ Transfer Failed';
        
        // Use the new transaction result function
        handleTransactionFailure(result.error || 'Transfer failed');
      }
    } catch (error) {
      submitBtn.textContent = '❌ Transfer Failed';
      
      // Use the new transaction result function
      handleTransactionFailure('Transfer failed: ' + error.message);
    } finally {
      submitBtn.disabled = false;
      loading.style.display = 'none';
    }
  }

  function prepareTransferData() {
    let url, data;
    
    switch (selectedMethod) {
      case 'self-deposit':
        url = `/api/${urlCustomerId}/${urlAccountNumber}/self-deposit`;
        data = {
          amount: parseFloat(document.getElementById('self-amount').value),
          paymentMethod: 'SELF_DEPOSIT',
          remarks: document.getElementById('self-remarks').value || 'Self deposit',
          transactionPassword: document.getElementById('self-transaction-password').value
        };
        break;
        
      case 'payee-transfer':
        const payeeId = document.getElementById('payee-select').value;
        url = `/api/${urlCustomerId}/${urlAccountNumber}/payee-transfer/${payeeId}`;
        data = {
          amount: parseFloat(document.getElementById('payee-amount').value),
          paymentMethod: document.getElementById('payee-method').value,
          remarks: document.getElementById('payee-remarks').value || 'Payee transfer',
          transactionPassword: document.getElementById('payee-transaction-password').value
        };
        break;
        
      case 'manual-transfer':
        // Ensure we have customer ID
        if (!urlCustomerId) {
          throw new Error('Customer ID not available. Please wait for account information to load.');
        }
        url = `/api/manual-transfer`;
        data = {
          fromAccountNumber: urlAccountNumber,
          toAccountNumber: document.getElementById('recipient-account').value,
          recipientName: document.getElementById('recipient-name').value,
          ifscCode: document.getElementById('ifsc-code').value,
          amount: parseFloat(document.getElementById('manual-amount').value),
          paymentMethod: document.getElementById('manual-method').value,
          remarks: document.getElementById('manual-remarks').value || 'Manual transfer',
          transactionPassword: document.getElementById('manual-transaction-password').value,
          customerId: urlCustomerId
        };
        break;
    }
    
    return { url, data };
  }

  function validateForm() {
    console.log('validateForm called');
    if (!urlAccountNumber || !selectedMethod) {
      console.log('Missing basic info - urlAccountNumber:', urlAccountNumber, 'selectedMethod:', selectedMethod);
      showError('Missing required information. Please check URL parameters and select transfer method.');
      return false;
    }
    
    // Ensure we have customer ID (either from URL or loaded from account)
    if (!urlCustomerId && customerData) {
      urlCustomerId = customerData.customerId.toString();
    }
    
    if (!urlCustomerId) {
      showError('Customer ID not available. Please ensure account information is loaded.');
      return false;
    }
    
    switch (selectedMethod) {
      case 'self-deposit':
        const selfAmount = document.getElementById('self-amount').value;
        const selfPassword = document.getElementById('self-transaction-password').value;
        if (!selfAmount || parseFloat(selfAmount) <= 0) {
          showError('Please enter a valid amount');
          return false;
        }
        if (!selfPassword || selfPassword.trim() === '') {
          showError('Please enter your transaction password');
          return false;
        }
        break;
        
      case 'payee-transfer':
        const payeeId = document.getElementById('payee-select').value;
        const payeeAmount = document.getElementById('payee-amount').value;
        const payeePassword = document.getElementById('payee-transaction-password').value;
        if (!payeeId) {
          showError('Please select a payee');
          return false;
        }
        if (!payeeAmount || parseFloat(payeeAmount) <= 0) {
          showError('Please enter a valid amount');
          return false;
        }
        if (!payeePassword || payeePassword.trim() === '') {
          showError('Please enter your transaction password');
          return false;
        }
        break;
        
      case 'manual-transfer':
        const recipientAccount = document.getElementById('recipient-account').value;
        const recipientName = document.getElementById('recipient-name').value;
        const ifscCode = document.getElementById('ifsc-code').value;
        const manualAmount = document.getElementById('manual-amount').value;
        const manualPassword = document.getElementById('manual-transaction-password').value;
        
        if (!recipientAccount || !recipientName || !ifscCode || !manualAmount) {
          showError('Please fill all required fields');
          return false;
        }
        if (parseFloat(manualAmount) <= 0) {
          showError('Please enter a valid amount');
          return false;
        }
        if (!manualPassword || manualPassword.trim() === '') {
          showError('Please enter your transaction password');
          return false;
        }
        break;
    }
    
    // Show transfer summary before final confirmation
    showTransferSummary();
    return true;
  }

  function showTransferSummary() {
    let summaryHtml = '<h4>Transfer Summary</h4>';
    summaryHtml += `<p><strong>From Account:</strong> ${urlAccountNumber}</p>`;
    summaryHtml += `<p><strong>Customer:</strong> ${document.getElementById('customer-name').value}</p>`;
    
    // Update button text based on transfer type
    const submitButton = document.getElementById('submit-transfer');
    
    switch (selectedMethod) {
      case 'self-deposit':
        summaryHtml += `<p><strong>Type:</strong> Self Deposit</p>`;
        summaryHtml += `<p><strong>Amount:</strong> ₹${document.getElementById('self-amount').value}</p>`;
        summaryHtml += `<p><strong>Remarks:</strong> ${document.getElementById('self-remarks').value || 'Self deposit'}</p>`;
        if (submitButton) submitButton.textContent = '💰 Deposit Money';
        break;
        
      case 'payee-transfer':
        const payeeText = document.getElementById('payee-select').selectedOptions[0].textContent;
        const payeeName = payeeText.split(' (')[0]; // Extract payee name
        summaryHtml += `<p><strong>Type:</strong> Payee Transfer</p>`;
        summaryHtml += `<p><strong>To:</strong> ${payeeText}</p>`;
        summaryHtml += `<p><strong>Amount:</strong> ₹${document.getElementById('payee-amount').value}</p>`;
        summaryHtml += `<p><strong>Method:</strong> ${document.getElementById('payee-method').value}</p>`;
        summaryHtml += `<p><strong>Remarks:</strong> ${document.getElementById('payee-remarks').value || 'Payee transfer'}</p>`;
        if (submitButton) submitButton.textContent = `👥 Send to ${payeeName}`;
        break;
        
      case 'manual-transfer':
        const recipientName = document.getElementById('recipient-name').value;
        summaryHtml += `<p><strong>Type:</strong> Manual Transfer</p>`;
        summaryHtml += `<p><strong>To Account:</strong> ${document.getElementById('recipient-account').value}</p>`;
        summaryHtml += `<p><strong>Recipient:</strong> ${recipientName}</p>`;
        summaryHtml += `<p><strong>IFSC:</strong> ${document.getElementById('ifsc-code').value}</p>`;
        summaryHtml += `<p><strong>Amount:</strong> ₹${document.getElementById('manual-amount').value}</p>`;
        summaryHtml += `<p><strong>Method:</strong> ${document.getElementById('manual-method').value}</p>`;
        summaryHtml += `<p><strong>Remarks:</strong> ${document.getElementById('manual-remarks').value || 'Manual transfer'}</p>`;
        if (submitButton) submitButton.textContent = `🏛️ Send to ${recipientName}`;
        break;
    }
    
    document.getElementById('summary-content').innerHTML = summaryHtml;
    showStep(3);
  }

  function showStep(step) {
    // Update step indicators
    document.querySelectorAll('.step').forEach((s, index) => {
      if (index + 1 <= step) {
        s.className = 'step active';
      } else {
        s.className = 'step inactive';
      }
    });
    
    switch (step) {
      case 2:
        document.getElementById('transfer-details').style.display = 'block';
        break;
      case 3:
        document.getElementById('submit-section').style.display = 'block';
        break;
    }
  }

  function hideStep(step) {
    document.getElementById(`step-${step}`).className = 'step inactive';
    
    switch (step) {
      case 2:
        document.getElementById('transfer-details').style.display = 'none';
        hideStep(3);
        break;
      case 3:
        document.getElementById('submit-section').style.display = 'none';
        break;
    }
  }

  function showSuccess(message) {
    alert('✅ ' + message);
  }

  function showError(message) {
    alert('❌ ' + message);
  }

  function resetForm() {
    // Reset form but keep account information
    selectedMethod = null;
    document.querySelectorAll('.transfer-option').forEach(opt => opt.classList.remove('selected'));
    document.querySelectorAll('.transfer-form').forEach(form => form.style.display = 'none');
    document.getElementById('transfer-details').style.display = 'none';
    document.getElementById('submit-section').style.display = 'none';
    
    // Reset step indicators
    document.getElementById('step-1').className = 'step active';
    document.getElementById('step-2').className = 'step inactive';
    document.getElementById('step-3').className = 'step inactive';
    
    // Clear form inputs
    document.querySelectorAll('input[type="number"], input[type="text"]:not([readonly])').forEach(input => {
      input.value = '';
    });
    document.querySelectorAll('select').forEach(select => {
      if (select.id !== 'payee-method' && select.id !== 'manual-method') {
        select.selectedIndex = 0;
      }
    });
  }
