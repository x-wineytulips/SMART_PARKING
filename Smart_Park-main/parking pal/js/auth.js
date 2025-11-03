class AuthUI {
    constructor() {
        this.initializeTabs();
        this.initializePasswordToggles();
        this.setupFormSubmission();
    }

    initializeTabs() {
        const tabs = document.querySelectorAll('.auth-tab');
        const forms = document.querySelectorAll('.auth-form');

        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                // Remove active class from all tabs and forms
                tabs.forEach(t => t.classList.remove('active'));
                forms.forEach(f => f.classList.remove('active'));

                // Add active class to clicked tab and corresponding form
                tab.classList.add('active');
                const formId = `${tab.dataset.tab}Form`;
                document.getElementById(formId).classList.add('active');
            });
        });
    }

    initializePasswordToggles() {
        const toggles = document.querySelectorAll('.toggle-password');
        toggles.forEach(toggle => {
            toggle.addEventListener('click', (e) => {
                const input = e.target.previousElementSibling;
                if (input.type === 'password') {
                    input.type = 'text';
                    toggle.classList.remove('fa-eye');
                    toggle.classList.add('fa-eye-slash');
                } else {
                    input.type = 'password';
                    toggle.classList.remove('fa-eye-slash');
                    toggle.classList.add('fa-eye');
                }
            });
        });
    }

    setupFormSubmission() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');

        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });

        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleRegistration();
        });
    }

    handleLogin() {
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        const rememberMe = document.getElementById('rememberMe').checked;

        // Add your login API call here
        console.log('Login:', { email, password, rememberMe });
        
        // Simulate successful login
        this.showMessage('Login successful!', 'success');
        setTimeout(() => {
            window.location.href = '../index.html';
        }, 1500);
    }

    handleRegistration() {
        const name = document.getElementById('registerName').value;
        const email = document.getElementById('registerEmail').value;
        const phone = document.getElementById('registerPhone').value;
        const password = document.getElementById('registerPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            this.showMessage('Passwords do not match!', 'error');
            return;
        }

        // Add your registration API call here
        console.log('Registration:', { name, email, phone, password });
        
        // Simulate successful registration
        this.showMessage('Registration successful!', 'success');
        setTimeout(() => {
            document.querySelector('[data-tab="login"]').click();
        }, 1500);
    }

    showMessage(message, type) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `auth-message ${type}`;
        messageDiv.textContent = message;
        
        document.querySelector('.auth-container').prepend(messageDiv);
        
        setTimeout(() => {
            messageDiv.remove();
        }, 3000);
    }
}

// Initialize authentication UI
document.addEventListener('DOMContentLoaded', () => {
    new AuthUI();
}); 