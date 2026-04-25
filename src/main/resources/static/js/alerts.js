/**
 * alerts.js - Handles interactive alerts with auto-hide and manual dismiss.
 */
document.addEventListener('DOMContentLoaded', function() {
    // Find all alert elements
    const alerts = document.querySelectorAll('.alert');

    alerts.forEach(function(alert) {
        // 1. Add a Close Button (X) to each alert
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '&times;'; // The 'X' symbol
        closeBtn.className = 'alert-close';
        closeBtn.setAttribute('aria-label', 'Close');
        
        closeBtn.addEventListener('click', function() {
            dismissAlert(alert);
        });

        alert.appendChild(closeBtn);

        // 2. Set a timer to auto-hide the alert after 5 seconds
        // (Success messages only, error messages stay until closed)
        if (alert.classList.contains('alert-success')) {
            setTimeout(function() {
                dismissAlert(alert);
            }, 5000); // 5000ms = 5 seconds
        }
    });

    /**
     * Helper function to fade out and remove an alert
     */
    function dismissAlert(alertElement) {
        if (!alertElement) return;
        
        // Add the fade-out class for smooth transition
        alertElement.classList.add('alert-fade-out');
        
        // Wait for the transition to finish (0.5s) and then remove from DOM
        setTimeout(function() {
            alertElement.remove();
        }, 500);
    }
});
