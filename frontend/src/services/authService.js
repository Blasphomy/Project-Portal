import apiClient from './api';

// Authentication service - handles all auth-related API calls
export const authService = {
    // Login user
    async login(email, password) {
        const response = await apiClient.post('/api/auth/login', {
            email,
            password,
        });

        if (response.success && response.data.accessToken) {
            localStorage.setItem('token', response.data.accessToken);
            localStorage.setItem('user', JSON.stringify(response.data.user));
        }

        return response;
    },

    // Register new user
    async register(username, email, password, fullName) {
        const response = await apiClient.post('/api/auth/register', {
            username,
            email,
            password,
            fullName,
        });

        return response;
    },

    // Logout user
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    // Get current user from storage
    getCurrentUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    // Check if user is authenticated
    isAuthenticated() {
        return !!localStorage.getItem('token');
    },

    // Get stored token
    getToken() {
        return localStorage.getItem('token');
    },
};

export default authService;
