import React, { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is already logged in on mount
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
            setUser(currentUser);
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await authService.login(email, password);
            if (response.success) {
                setUser(response.data.user);
                return { success: true };
            }
            return { success: false, error: response.message };
        } catch (error) {
            return { success: false, error: error.message };
        }
    };

    const register = async (username, email, password, fullName) => {
        try {
            const response = await authService.register(username, email, password, fullName);
            console.log('[AuthContext] Register response:', response);
            console.log('[AuthContext] response.success:', response.success);
            if (response.success) {
                console.log('[AuthContext] Registration SUCCESS detected');
                return { success: true, message: 'Registration successful!' };
            }
            console.log('[AuthContext] Registration FAILED - returning error:', response.error || 'Registration failed');
            return { success: false, error: response.error || 'Registration failed' };
        } catch (error) {
            console.error('[AuthContext] Register exception:', error);
            return { success: false, error: error.message };
        }
    };

    const logout = () => {
        authService.logout();
        setUser(null);
    };

    const value = {
        user,
        login,
        register,
        logout,
        isAuthenticated: !!user,
        loading,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export { AuthContext };
