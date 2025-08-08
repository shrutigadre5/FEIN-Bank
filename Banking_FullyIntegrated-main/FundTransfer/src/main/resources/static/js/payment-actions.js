// Payment actions script

// Function to cancel payment and redirect to account page
function cancelPayment() {
    // You can add any cleanup logic here if needed
    console.log("Payment cancelled by user");
    
    // Redirect to account page
    window.location.href = "http://localhost:8081/account.html";
    
    // Prevent default form submission
    return false;
}

// Function to show success/failure message and refresh page
function showTransactionResult(isSuccess, message, transactionData = null) {
    // Create message container
    const messageContainer = document.createElement('div');
    messageContainer.className = `transaction-message ${isSuccess ? 'success' : 'error'}`;
    
    let transactionInfo = '';
    if (isSuccess && transactionData) {
        transactionInfo = `
            <div class="transaction-details">
                ${transactionData.transactionId ? `<p><strong>Transaction ID:</strong> ${transactionData.transactionId}</p>` : ''}
                ${transactionData.amount ? `<p><strong>Amount:</strong> ₹${transactionData.amount}</p>` : ''}
                ${transactionData.fromAccount ? `<p><strong>From Account:</strong> ${transactionData.fromAccount}</p>` : ''}
                ${transactionData.toAccount ? `<p><strong>To Account:</strong> ${transactionData.toAccount}</p>` : ''}
                ${transactionData.paymentMethod ? `<p><strong>Payment Method:</strong> ${transactionData.paymentMethod}</p>` : ''}
                ${transactionData.recipientName ? `<p><strong>Recipient:</strong> ${transactionData.recipientName}</p>` : ''}
                ${transactionData.remarks ? `<p><strong>Remarks:</strong> ${transactionData.remarks}</p>` : ''}
            </div>
        `;
    }
    
    messageContainer.innerHTML = `
        <div class="message-content">
            <div class="message-icon">${isSuccess ? '✅' : '❌'}</div>
            <div class="message-text">
                <h3>${isSuccess ? 'Transaction Successful!' : 'Transaction Failed!'}</h3>
                <p>${message}</p>
                ${transactionInfo}
                <p><small>Page will refresh in few seconds...</small></p>
            </div>
        </div>
    `;
    
    // Add message to page
    document.body.appendChild(messageContainer);
    
    // Show message with animation
    setTimeout(() => {
        messageContainer.classList.add('show');
    }, 100);
    
    // Refresh page after 2 seconds
    setTimeout(() => {
        window.location.reload();
    }, 5000);
}

// Function to handle successful transaction
function handleTransactionSuccess(response) {
    const message = response.message || 'Your transaction has been processed successfully.';
    
    // Prepare transaction data for display
    const transactionData = {
        transactionId: response.transactionId,
        amount: response.amount || getCurrentTransactionAmount(),
        fromAccount: getCurrentFromAccount(),
        toAccount: getCurrentToAccount(),
        paymentMethod: getCurrentPaymentMethod(),
        recipientName: getCurrentRecipientName(),
        remarks: getCurrentRemarks()
    };
    
    showTransactionResult(true, message, transactionData);
}

// Helper functions to get current form data
function getCurrentTransactionAmount() {
    const selectedMethod = getSelectedTransferMethod();
    switch(selectedMethod) {
        case 'self-deposit':
            return document.getElementById('self-amount')?.value;
        case 'payee-transfer':
            return document.getElementById('payee-amount')?.value;
        case 'manual-transfer':
            return document.getElementById('manual-amount')?.value;
        default:
            return null;
    }
}

function getCurrentFromAccount() {
    return document.getElementById('account-number')?.value;
}

function getCurrentToAccount() {
    const selectedMethod = getSelectedTransferMethod();
    switch(selectedMethod) {
        case 'self-deposit':
            return document.getElementById('account-number')?.value;
        case 'payee-transfer':
            const payeeSelect = document.getElementById('payee-select');
            return payeeSelect?.selectedOptions[0]?.getAttribute('data-account');
        case 'manual-transfer':
            return document.getElementById('recipient-account')?.value;
        default:
            return null;
    }
}

function getCurrentPaymentMethod() {
    const selectedMethod = getSelectedTransferMethod();
    switch(selectedMethod) {
        case 'self-deposit':
            return 'SELF_DEPOSIT';
        case 'payee-transfer':
            return document.getElementById('payee-method')?.value;
        case 'manual-transfer':
            return document.getElementById('manual-method')?.value;
        default:
            return null;
    }
}

function getCurrentRecipientName() {
    const selectedMethod = getSelectedTransferMethod();
    switch(selectedMethod) {
        case 'self-deposit':
            return document.getElementById('customer-name')?.value;
        case 'payee-transfer':
            const payeeSelect = document.getElementById('payee-select');
            return payeeSelect?.selectedOptions[0]?.text;
        case 'manual-transfer':
            return document.getElementById('recipient-name')?.value;
        default:
            return null;
    }
}

function getCurrentRemarks() {
    const selectedMethod = getSelectedTransferMethod();
    switch(selectedMethod) {
        case 'self-deposit':
            return document.getElementById('self-remarks')?.value;
        case 'payee-transfer':
            return document.getElementById('payee-remarks')?.value;
        case 'manual-transfer':
            return document.getElementById('manual-remarks')?.value;
        default:
            return null;
    }
}

function getSelectedTransferMethod() {
    const selectedOption = document.querySelector('.transfer-option.selected');
    return selectedOption?.getAttribute('data-method');
}

// Function to handle failed transaction
function handleTransactionFailure(errorMessage) {
    const message = errorMessage || 'An error occurred while processing your transaction.';
    showTransactionResult(false, message);
}

// Initialize cancel button if it exists
document.addEventListener("DOMContentLoaded", function() {
    const cancelBtn = document.getElementById("cancelPaymentBtn");
    if (cancelBtn) {
        cancelBtn.addEventListener("click", cancelPayment);
    }
    
    // Add CSS styles for transaction messages
    addTransactionMessageStyles();
});

// Function to add CSS styles for transaction messages
function addTransactionMessageStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .transaction-message {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 10000;
            opacity: 0;
            transition: opacity 0.3s ease;
        }
        
        .transaction-message.show {
            opacity: 1;
        }
        
        .message-content {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            text-align: center;
            max-width: 400px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
        }
        
        .message-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        
        .transaction-message.success .message-content {
            border-left: 5px solid #800020;
        }
        
        .transaction-message.error .message-content {
            border-left: 5px solid #dc3545;
        }
        
        .message-text h3 {
            color: #333;
            margin-bottom: 0.5rem;
            font-size: 1.5rem;
        }
        
        .transaction-message.success .message-text h3 {
            color: #800020;
        }
        
        .transaction-message.error .message-text h3 {
            color: #dc3545;
        }
        
        .message-text p {
            color: #666;
            margin-bottom: 0.5rem;
            line-height: 1.4;
        }
        
        .transaction-details {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 1rem;
            margin: 1rem 0;
            text-align: left;
        }
        
        .transaction-details p {
            margin-bottom: 0.3rem;
            font-size: 0.9rem;
        }
        
        .transaction-details strong {
            color: #800020;
        }
        
        .message-text small {
            color: #999;
            font-style: italic;
        }
    `;
    document.head.appendChild(style);
}
