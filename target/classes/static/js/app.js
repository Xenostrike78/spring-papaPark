/**
 * Smart Parking System - Frontend Javascript Utilities
 * Handles API calls, authentication, and UI updates
 */

class ApiClient {
    constructor() {
        this.BASE_URL = ''; // Same origin
    }

    /**
     * Get JWT token from localStorage
     */
    getToken() {
        return localStorage.getItem('token');
    }

    /**
     * Get User object from localStorage
     */
    getUser() {
        const userStr = localStorage.getItem('user');
        if (!userStr) return null;
        try {
            return JSON.parse(userStr);
        } catch (e) {
            return null;
        }
    }

    /**
     * Check if user is logged in
     */
    isLoggedIn() {
        return !!this.getToken();
    }

    /**
     * Check if user has ADMIN role
     */
    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'ROLE_ADMIN';
    }

    /**
     * Clear session and logout
     */
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
    }

    /**
     * Generic fetch wrapper handling auth and common errors
     * @param {string} method - HTTP method
     * @param {string} url - API endpoint
     * @param {object} body - Request body payload
     * @returns {Promise<any>}
     */
    async request(method, url, body = null) {
        const headers = {
            'Content-Type': 'application/json'
        };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const options = {
            method,
            headers
        };

        if (body) {
            options.body = JSON.stringify(body);
        }

        try {
            const response = await fetch(this.BASE_URL + url, options);

            // Handle unauthorized - token expired or invalid
            if (response.status === 401 && !url.includes('/api/auth/')) {
                this.logout();
                throw new Error("Session expired. Please login again.");
            }

            // Handle forbidden
            if (response.status === 403) {
                showAlert("Access Denied", "danger");
                throw new Error("Access Denied");
            }

            // For empty responses
            if (response.status === 204) {
                return null;
            }

            const data = await response.json().catch(() => ({}));

            if (!response.ok) {
                const errorMsg = data.message || data.error || 'An error occurred';
                throw new Error(errorMsg);
            }

            return data;
        } catch (error) {
            console.error(`API Error (${method} ${url}):`, error);
            throw error;
        }
    }

    async get(url) { return this.request('GET', url); }
    async post(url, body) { return this.request('POST', url, body); }
    async put(url, body) { return this.request('PUT', url, body); }
    async delete(url) { return this.request('DELETE', url); }
}

// Global API instance
const api = new ApiClient();

/* --- Auth Functions --- */

async function login(email, password) {
    try {
        const response = await api.post('/api/auth/login', { email, password });
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify({
            id: response.id,
            email: response.email,
            fullName: response.fullName,
            role: response.role
        }));
        
        showAlert("Login successful", "success");
        setTimeout(() => {
            window.location.href = '/dashboard';
        }, 1000);
        return true;
    } catch (error) {
        showAlert(error.message, "danger");
        return false;
    }
}

async function register(data) {
    try {
        await api.post('/api/auth/register', data);
        showAlert("Registration successful! Please login.", "success");
        setTimeout(() => {
            window.location.href = '/login';
        }, 1500);
        return true;
    } catch (error) {
        showAlert(error.message, "danger");
        return false;
    }
}

/* --- UI Utilities --- */

/**
 * Show a toast alert
 * @param {string} message - Alert text
 * @param {string} type - 'success', 'danger', 'warning', 'info'
 */
function showAlert(message, type = 'info') {
    let container = document.getElementById('alert-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'alert-container';
        document.body.appendChild(container);
    }

    const alertEl = document.createElement('div');
    alertEl.className = `alert alert-${type}`;
    
    // Icon based on type
    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'danger') icon = '❌';
    if (type === 'warning') icon = '⚠️';

    alertEl.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(alertEl);

    // Auto dismiss
    setTimeout(() => {
        alertEl.classList.add('hiding');
        setTimeout(() => alertEl.remove(), 300);
    }, 4000);
}

function showModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function hideModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '₹0.00';
    return '₹' + Number(amount).toFixed(2);
}

function showLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="loading-container">
                <div class="loading-spinner"></div>
                <p>Loading...</p>
            </div>
        `;
    }
}

function hideLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '';
    }
}

function confirmAction(message) {
    return new Promise((resolve) => {
        const result = window.confirm(message);
        resolve(result);
    });
}

/* --- Page Guards --- */

function requireAuth() {
    if (!api.isLoggedIn()) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

function requireAdmin() {
    if (!requireAuth()) return false;
    if (!api.isAdmin()) {
        showAlert("Admin access required", "danger");
        setTimeout(() => {
            window.location.href = '/dashboard';
        }, 1000);
        return false;
    }
    return true;
}

function initSidebar() {
    // Mobile menu toggle
    const toggleBtn = document.getElementById('mobile-menu-toggle');
    const sidebar = document.getElementById('sidebar');
    
    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (window.innerWidth <= 768 && 
                !sidebar.contains(e.target) && 
                !toggleBtn.contains(e.target) && 
                sidebar.classList.contains('open')) {
                sidebar.classList.remove('open');
            }
        });
    }

    // Set active nav link based on current URL
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        } else if (href === '/' && currentPath === '/') {
            link.classList.add('active');
        }
    });

    // Handle Admin sections visibility
    const adminSections = document.querySelectorAll('.admin-only');
    if (!api.isAdmin()) {
        adminSections.forEach(el => el.style.display = 'none');
    }
}

/* --- Global Initialization --- */
document.addEventListener('DOMContentLoaded', () => {
    // Setup UI for authenticated user if applicable
    if (api.isLoggedIn()) {
        const user = api.getUser();
        
        // Update user names in UI if elements exist
        const nameEls = document.querySelectorAll('.user-name-display');
        nameEls.forEach(el => el.textContent = user.fullName || user.email);
        
        const roleEls = document.querySelectorAll('.user-role-display');
        roleEls.forEach(el => el.textContent = user.role.replace('ROLE_', ''));

        // Init sidebar if it exists
        if (document.getElementById('sidebar')) {
            initSidebar();
        }

        // Wire up logout buttons
        const logoutBtns = document.querySelectorAll('.btn-logout, #logout-btn');
        logoutBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                api.logout();
            });
        });
    }

    // Close modal on outside click or close button
    const modals = document.querySelectorAll('.modal-overlay');
    modals.forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        });
    });

    const closeBtns = document.querySelectorAll('.btn-close');
    closeBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const modal = this.closest('.modal-overlay');
            if (modal) modal.classList.remove('active');
        });
    });
});
