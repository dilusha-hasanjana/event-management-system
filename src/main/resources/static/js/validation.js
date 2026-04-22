document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.querySelector('form[th\\:action="@{/register}"]');
    if (registerForm) {
        initRegistrationValidation(registerForm);
    }

    const eventForms = document.querySelectorAll('form[th\\:object="${event}"]');
    eventForms.forEach(form => {
        initEventValidation(form);
    });
});

function initRegistrationValidation(form) {
    const usernameInput = form.querySelector('#username');
    const emailInput = form.querySelector('#email');
    const passwordInput = form.querySelector('#password');

    if (usernameInput) {
        usernameInput.addEventListener('blur', function() {
            validateUsername(this.value);
        });
    }

    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            validateEmail(this.value);
        });
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', debounce(function() {
            validatePassword(this.value);
        }, 500));
    }

    form.addEventListener('submit', function(e) {
        let isValid = true;

        if (!validateUsername(usernameInput.value)) isValid = false;
        if (!validateEmail(emailInput.value)) isValid = false;
        if (!validatePassword(passwordInput.value)) isValid = false;

        if (!isValid) {
            e.preventDefault();
        }
    });
}

function initEventValidation(form) {
    const dateInput = form.querySelector('#eventDate');

    if (dateInput) {
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        dateInput.min = now.toISOString().slice(0, 16);

        dateInput.addEventListener('change', function() {
            validateEventDate(this.value);
        });
    }

    form.addEventListener('submit', function(e) {
        if (!validateEventForm(this)) {
            e.preventDefault();
        }
    });
}

function validateUsername(username) {
    const minLength = 3;
    const maxLength = 20;
    const regex = /^[a-zA-Z0-9_]+$/;

    if (username.length < minLength) {
        showFieldError('username', `Username must be at least ${minLength} characters`);
        return false;
    }

    if (username.length > maxLength) {
        showFieldError('username', `Username must be less than ${maxLength} characters`);
        return false;
    }

    if (!regex.test(username)) {
        showFieldError('username', 'Username can only contain letters, numbers, and underscores');
        return false;
    }

    clearFieldError('username');
    return true;
}

function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regex.test(email)) {
        showFieldError('email', 'Please enter a valid email address');
        return false;
    }

    clearFieldError('email');
    return true;
}

function validatePassword(password) {
    const minLength = 6;

    if (password.length < minLength) {
        showFieldError('password', `Password must be at least ${minLength} characters`);
        return false;
    }

    clearFieldError('password');
    return true;
}

function validateEventDate(dateString) {
    if (!dateString) {
        showFieldError('eventDate', 'Please select a date');
        return false;
    }

    const selectedDate = new Date(dateString);
    const now = new Date();

    if (selectedDate < now) {
        showFieldError('eventDate', 'Event date must be in the future');
        return false;
    }

    clearFieldError('eventDate');
    return true;
}

function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    field.classList.add('field-error');

    let errorDiv = field.parentElement.querySelector('.error-message');
    if (!errorDiv) {
        errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        field.parentElement.appendChild(errorDiv);
    }

    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function clearFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    field.classList.remove('field-error');

    const errorDiv = field.parentElement.querySelector('.error-message');
    if (errorDiv) {
        errorDiv.style.display = 'none';
    }
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}