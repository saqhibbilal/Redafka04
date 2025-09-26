import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Helper function to check if token is expired
  const isTokenExpired = (token) => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch (error) {
      return true; // If we can't parse the token, consider it expired
    }
  };

  // Check for existing token on app load
  useEffect(() => {
    const savedToken = localStorage.getItem('riyada_token');
    const savedUser = localStorage.getItem('riyada_user');
    
    if (savedToken && savedUser) {
      // Check if token is expired
      if (isTokenExpired(savedToken)) {
        // Token is expired, clear it
        localStorage.removeItem('riyada_token');
        localStorage.removeItem('riyada_user');
      } else {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      }
    }
    
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      setLoading(true);
      const response = await authAPI.login(email, password);
      
      if (response.success) {
        setUser(response.user);
        setToken(response.token);
        
        // Save to localStorage (in real app, this would be httpOnly cookies)
        localStorage.setItem('riyada_token', response.token);
        localStorage.setItem('riyada_user', JSON.stringify(response.user));
        
        return { success: true };
      } else {
        return { success: false, error: response.error };
      }
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, error: 'Login failed. Please try again.' };
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    try {
      setLoading(true);
      const response = await authAPI.register(userData);
      
      if (response.success) {
        setUser(response.user);
        localStorage.setItem('riyada_user', JSON.stringify(response.user));
        
        // Auto-login user after successful registration to get JWT token
        try {
          const loginResponse = await authAPI.login(userData.email, userData.password);
          if (loginResponse.success) {
            setUser(loginResponse.user);
            setToken(loginResponse.token);
            localStorage.setItem('riyada_token', loginResponse.token);
            localStorage.setItem('riyada_user', JSON.stringify(loginResponse.user));
            return { success: true, message: response.message };
          } else {
            // If auto-login fails, user is still registered but needs manual login
            return { success: true, message: response.message + ". Please login to continue." };
          }
        } catch (loginError) {
          // Registration successful but auto-login failed
          return { success: true, message: response.message + ". Please login to continue." };
        }
      } else {
        return { success: false, error: response.error };
      }
    } catch (error) {
      console.error('Registration error:', error);
      return { success: false, error: 'Registration failed. Please try again.' };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('riyada_token');
    localStorage.removeItem('riyada_user');
  };

  // Function to validate token before making API calls
  const validateToken = () => {
    if (token && isTokenExpired(token)) {
      logout();
      return false;
    }
    return !!token;
  };

  const value = {
    user,
    token,
    loading,
    login,
    register,
    logout,
    validateToken,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
