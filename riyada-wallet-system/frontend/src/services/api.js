// Real API service - makes HTTP requests to backend services
// User Service runs on port 8081

import { mockWallets, mockTransactions, mockNotifications } from '../data/mockData.js';

// API Base URLs
const USER_SERVICE_URL = 'http://localhost:8081/api/users';
const WALLET_SERVICE_URL = 'http://localhost:8082/api/wallets';

// Helper function to make HTTP requests
const apiRequest = async (url, options = {}) => {
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const config = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers,
    },
  };

  try {
    const response = await fetch(url, config);
    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
};

// Real authentication API
export const authAPI = {
  login: async (email, password) => {
    try {
      const response = await apiRequest(`${USER_SERVICE_URL}/login`, {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });

      if (response.success && response.data) {
        return {
          success: true,
          user: {
            id: response.data.userId,
            email: response.data.email,
            firstName: response.data.firstName,
            lastName: response.data.lastName,
            name: `${response.data.firstName} ${response.data.lastName}`,
          },
          token: response.data.token,
          tokenType: response.data.tokenType,
          expiresIn: response.data.expiresIn
        };
      }
      
      return {
        success: false,
        error: response.message || 'Login failed'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Login failed. Please try again.'
      };
    }
  },

  register: async (userData) => {
    try {
      const response = await apiRequest(`${USER_SERVICE_URL}/register`, {
        method: 'POST',
        body: JSON.stringify({
          email: userData.email,
          password: userData.password,
          firstName: userData.firstName,
          lastName: userData.lastName,
          phone: userData.phone
        }),
      });

      if (response.success && response.user) {
        return {
          success: true,
          user: {
            id: response.user.id,
            email: response.user.email,
            firstName: response.user.firstName,
            lastName: response.user.lastName,
            name: `${response.user.firstName} ${response.user.lastName}`,
            phone: response.user.phone
          },
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Registration failed'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Registration failed. Please try again.'
      };
    }
  },

  getUserProfile: async (userId, token) => {
    try {
      const response = await apiRequest(`${USER_SERVICE_URL}/profile/${userId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success && response.user) {
        return {
          success: true,
          user: {
            id: response.user.id,
            email: response.user.email,
            firstName: response.user.firstName,
            lastName: response.user.lastName,
            name: `${response.user.firstName} ${response.user.lastName}`,
            phone: response.user.phone,
            isActive: response.user.isActive,
            createdAt: response.user.createdAt,
            updatedAt: response.user.updatedAt
          }
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch user profile'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch user profile'
      };
    }
  }
};

// Real wallet API - connects to Wallet Service
export const walletAPI = {
  getBalance: async (userId, token) => {
    try {
      const response = await apiRequest(`${WALLET_SERVICE_URL}/user/${userId}/balance`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          balance: response.balance,
          currency: response.currency || 'USD'
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch balance'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch balance'
      };
    }
  },

  getWallet: async (userId, token) => {
    try {
      const response = await apiRequest(`${WALLET_SERVICE_URL}/user/${userId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          wallet: response.wallet
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch wallet'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch wallet'
      };
    }
  },

  getTransactions: async (userId, token) => {
    try {
      const response = await apiRequest(`${WALLET_SERVICE_URL}/user/${userId}/transactions`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          transactions: response.transactions
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch transactions'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch transactions'
      };
    }
  },

  createWallet: async (userId, token) => {
    try {
      const response = await apiRequest(`${WALLET_SERVICE_URL}/create-for-user/${userId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          wallet: response.wallet,
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to create wallet'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to create wallet'
      };
    }
  },

  creditWallet: async (userId, amount, description, token) => {
    try {
      const params = new URLSearchParams({
        amount: amount.toString(),
        ...(description && { description })
      });

      const response = await apiRequest(`${WALLET_SERVICE_URL}/user/${userId}/credit?${params}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          wallet: response.wallet,
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to credit wallet'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to credit wallet'
      };
    }
  },

  debitWallet: async (userId, amount, description, token) => {
    try {
      const params = new URLSearchParams({
        amount: amount.toString(),
        ...(description && { description })
      });

      const response = await apiRequest(`${WALLET_SERVICE_URL}/user/${userId}/debit?${params}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          wallet: response.wallet,
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to debit wallet'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to debit wallet'
      };
    }
  },

  // For now, transfer functionality will be handled by Payment Service
  // This is a placeholder that will be implemented later
  transfer: async (fromUserId, toEmail, amount, description, token) => {
    return {
      success: false,
      error: 'Transfer functionality will be implemented in the Payment Service'
    };
  }
};

// Mock notifications API
export const notificationsAPI = {
  getNotifications: async (userId) => {
    await delay(300);
    
    const notifications = mockNotifications.filter(n => n.userId === userId);
    return {
      success: true,
      notifications: notifications.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
    };
  },

  markAsRead: async (notificationId) => {
    await delay(200);
    
    const notification = mockNotifications.find(n => n.id === notificationId);
    if (notification) {
      notification.read = true;
    }
    
    return { success: true };
  }
};
