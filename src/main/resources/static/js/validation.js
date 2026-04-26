document.addEventListener('DOMContentLoaded', function() {
    
    // 1. Setup Password Matching Validation
    const passwordInputs = document.querySelectorAll('input[type="password"]:not(#confirmPassword)');
    const confirmPasswordInputs = document.querySelectorAll('#confirmPassword');

    if (confirmPasswordInputs.length > 0) {
        confirmPasswordInputs.forEach(confirmInput => {
            const form = confirmInput.closest('form');
            if (!form) return;
            
            // Try to find the matching password input in the same form
            const passwordInput = form.querySelector('input[type="password"]:not(#confirmPassword)');
            
            if (passwordInput) {
                // Validate on input
                confirmInput.addEventListener('input', () => validatePasswordsMatch(passwordInput, confirmInput));
                passwordInput.addEventListener('input', () => {
                    if (confirmInput.value) validatePasswordsMatch(passwordInput, confirmInput);
                });

                // Validate on submit
                form.addEventListener('submit', (e) => {
                    if (!validatePasswordsMatch(passwordInput, confirmInput)) {
                        e.preventDefault();
                    }
                });
            }
        });
    }

    // 2. Setup Future Date Validation for Events
    const dateInputs = document.querySelectorAll('input[type="date"]');
    if (dateInputs.length > 0) {
        dateInputs.forEach(dateInput => {
            // Set min date to today if it's an event date (don't restrict birthdates if any)
            if (dateInput.id === 'date' || dateInput.id === 'eventDate') {
                const today = new Date().toISOString().split('T')[0];
                dateInput.min = today;

                const form = dateInput.closest('form');
                if (form) {
                    form.addEventListener('submit', (e) => {
                        if (!validateFutureDate(dateInput)) {
                            e.preventDefault();
                        }
                    });
                    dateInput.addEventListener('change', () => validateFutureDate(dateInput));
                }
            }
        });
    }

    // 3. Generic Form Validation for all required fields
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        // Skip search forms so they can be submitted quickly without strict UI blocks
        if (form.id === 'searchForm') return;

        form.addEventListener('submit', function(e) {
            let isValid = true;
            const requiredFields = form.querySelectorAll('[required]');
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    showFieldError(field, 'This field is required');
                    isValid = false;
                } else {
                    // Check email validity if it's an email field
                    if (field.type === 'email' && !validateEmailFormat(field)) {
                        isValid = false;
                    } else {
                        clearFieldError(field);
                    }
                }
            });

            if (!isValid) {
                e.preventDefault();
            }
        });

        // Clear error on input
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('input', function() {
                if (this.required && !this.value.trim()) {
                    showFieldError(this, 'This field is required');
                } else if (this.type === 'email' && this.value.trim()) {
                    validateEmailFormat(this);
                } else {
                    clearFieldError(this);
                }
            });
        });
    });
});

function validatePasswordsMatch(passwordInput, confirmInput) {
    // If both are empty (e.g., profile update optional password), it's valid
    if (!passwordInput.value && !confirmInput.value && !passwordInput.required) {
        clearFieldError(confirmInput);
        return true;
    }

    if (passwordInput.value !== confirmInput.value) {
        showFieldError(confirmInput, 'Passwords do not match');
        return false;
    }
    
    clearFieldError(confirmInput);
    return true;
}

function validateFutureDate(dateInput) {
    if (!dateInput.value) return true; // Handled by required validation

    const selectedDate = new Date(dateInput.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
        showFieldError(dateInput, 'Date cannot be in the past');
        return false;
    }

    clearFieldError(dateInput);
    return true;
}

function validateEmailFormat(emailInput) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(emailInput.value)) {
        showFieldError(emailInput, 'Please enter a valid email address');
        return false;
    }
    clearFieldError(emailInput);
    return true;
}

function showFieldError(field, message) {
    field.classList.add('field-error');
    field.style.borderColor = 'var(--danger)'; // Explicitly set border color

    let errorDiv = field.parentElement.querySelector('.error-message');
    if (!errorDiv) {
        errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.style.color = 'var(--danger)';
        errorDiv.style.fontSize = '0.75rem';
        errorDiv.style.marginTop = '0.25rem';
        field.parentElement.appendChild(errorDiv);
    }

    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function clearFieldError(field) {
    field.classList.remove('field-error');
    field.style.borderColor = ''; // Reset border color

    const errorDiv = field.parentElement.querySelector('.error-message');
    if (errorDiv) {
        errorDiv.style.display = 'none';
    }
}