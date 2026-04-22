document.addEventListener('DOMContentLoaded', function() {
    animateStats();
    initBulkActions();
    initUserStatusToggle();
});

function animateStats() {
    const statValues = document.querySelectorAll('.stat-value');

    statValues.forEach(stat => {
        const target = parseInt(stat.textContent);
        if (isNaN(target)) return;

        let current = 0;
        const increment = target / 50;
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                stat.textContent = target;
                clearInterval(timer);
            } else {
                stat.textContent = Math.floor(current);
            }
        }, 20);
    });
}

function initBulkActions() {
    const selectAllCheckbox = document.querySelector('#selectAll');
    const itemCheckboxes = document.querySelectorAll('.select-item');

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', (e) => {
            itemCheckboxes.forEach(checkbox => {
                checkbox.checked = e.target.checked;
            });
        });
    }
}

function initUserStatusToggle() {
    const toggleForms = document.querySelectorAll('form[action*="/admin/users/toggle"]');

    toggleForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const button = form.querySelector('button');
            const action = button.textContent.includes('Deactivate') ? 'deactivate' : 'activate';

            if (!confirm(`Are you sure you want to ${action} this user?`)) {
                e.preventDefault();
            }
        });
    });
}

function validateEventForm(form) {
    const title = form.querySelector('[name="title"]').value.trim();
    const description = form.querySelector('[name="description"]').value.trim();
    const location = form.querySelector('[name="location"]').value.trim();
    const eventDate = form.querySelector('[name="eventDate"]').value;

    let errors = [];

    if (title.length < 3) {
        errors.push('Title must be at least 3 characters long');
    }

    if (description.length < 10) {
        errors.push('Description must be at least 10 characters long');
    }

    if (location.length < 3) {
        errors.push('Location must be at least 3 characters long');
    }

    if (!eventDate) {
        errors.push('Please select a valid date');
    } else {
        const selectedDate = new Date(eventDate);
        const now = new Date();
        if (selectedDate < now) {
            errors.push('Event date must be in the future');
        }
    }

    if (errors.length > 0) {
        alert(errors.join('\n'));
        return false;
    }

    return true;
}