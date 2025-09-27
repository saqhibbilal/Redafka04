// Real API service - makes HTTP requests to backend services
// User Service runs on port 8081

import { mockWallets, mockTransactions, mockNotifications } from '../data/mockData.js';

// API Base URLs
const USER_SERVICE_URL = 'http://localhost:8081/api/users';
const WALLET_SERVICE_URL = 'http://localhost:8082/api/wallets';
const PAYMENT_SERVICE_URL = 'http://localhost:8083/api/payments';
const LEDGER_SERVICE_URL = 'http://localhost:8084/api/ledger';

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

// Real user API
export const userAPI = {
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

  // Transfer functionality is now handled by Payment Service
  transfer: async (fromUserId, toEmail, amount, description, token) => {
    try {
      const response = await apiRequest(`${PAYMENT_SERVICE_URL}/transfer`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          toEmail,
          amount,
          description
        })
      });

      if (response.success) {
        return {
          success: true,
          payment: response.payment,
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Transfer failed'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Transfer failed. Please try again.'
      };
    }
  }
};

// Payment Service API
export const paymentAPI = {
  // Process a payment transfer
  transfer: async (toEmail, amount, description, token) => {
    try {
      const response = await apiRequest(`${PAYMENT_SERVICE_URL}/transfer`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          toEmail,
          amount,
          description
        })
      });

      if (response.success) {
        return {
          success: true,
          payment: response.payment,
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Transfer failed'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Transfer failed. Please try again.'
      };
    }
  },

  // Get payment by ID
  getPayment: async (paymentId, token) => {
    try {
      const response = await apiRequest(`${PAYMENT_SERVICE_URL}/${paymentId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          payment: response.payment
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch payment'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch payment'
      };
    }
  },

  // Get payment status by reference ID
  getPaymentStatus: async (referenceId) => {
    try {
      const response = await apiRequest(`${PAYMENT_SERVICE_URL}/status/${referenceId}`, {
        method: 'GET'
      });

      if (response.success) {
        return {
          success: true,
          status: response.status
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch payment status'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch payment status'
      };
    }
  },

  // Get user's payment history
  getUserPayments: async (userId, token) => {
    try {
      const response = await apiRequest(`${PAYMENT_SERVICE_URL}/user/${userId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          payments: response.payments
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch payment history'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch payment history'
      };
    }
  },

  // Cancel a pending payment
  cancelPayment: async (paymentId, token) => {
    try {
      const response = await apiRequest(`${PAYMENT_SERVICE_URL}/${paymentId}/cancel`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          payment: response.payment,
          message: response.message
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to cancel payment'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to cancel payment'
      };
    }
  }
};

// Ledger Service API - Real transaction history and audit trail
export const ledgerAPI = {
  // Get user transactions with pagination
  getUserTransactions: async (userId, token, page = 0, size = 20) => {
    try {
      const response = await apiRequest(`${LEDGER_SERVICE_URL}/transactions?page=${page}&size=${size}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          transactions: response.data.content || [],
          totalElements: response.data.totalElements || 0,
          totalPages: response.data.totalPages || 0,
          currentPage: response.data.number || 0,
          size: response.data.size || size
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

  // Get recent transactions for a user
  getRecentTransactions: async (userId, token, limit = 10) => {
    try {
      const response = await apiRequest(`${LEDGER_SERVICE_URL}/transactions/recent?limit=${limit}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          transactions: response.data || []
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch recent transactions'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch recent transactions'
      };
    }
  },

  // Get transaction by ID
  getTransactionById: async (transactionId, token) => {
    try {
      const response = await apiRequest(`${LEDGER_SERVICE_URL}/transactions/${transactionId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          transaction: response.data
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch transaction'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch transaction'
      };
    }
  },

  // Get audit trail for a transaction
  getAuditTrail: async (transactionId, token) => {
    try {
      const response = await apiRequest(`${LEDGER_SERVICE_URL}/transactions/${transactionId}/audit-trail`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          auditLogs: response.data || []
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch audit trail'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch audit trail'
      };
    }
  },

  // Get financial summary for user
  getFinancialSummary: async (userId, token) => {
    try {
      const response = await apiRequest(`${LEDGER_SERVICE_URL}/reports/summary`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          summary: response.data
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to fetch financial summary'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to fetch financial summary'
      };
    }
  },

  // Search transactions with filters
  searchTransactions: async (userId, token, filters = {}) => {
    try {
      const params = new URLSearchParams();
      
      if (filters.status) params.append('status', filters.status);
      if (filters.transactionType) params.append('transactionType', filters.transactionType);
      if (filters.startDate) params.append('startDate', filters.startDate);
      if (filters.endDate) params.append('endDate', filters.endDate);
      if (filters.page !== undefined) params.append('page', filters.page);
      if (filters.size !== undefined) params.append('size', filters.size);
      
      const queryString = params.toString();
      const url = `${LEDGER_SERVICE_URL}/search${queryString ? '?' + queryString : ''}`;

      const response = await apiRequest(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.success) {
        return {
          success: true,
          transactions: response.data.content || [],
          totalElements: response.data.totalElements || 0,
          totalPages: response.data.totalPages || 0,
          currentPage: response.data.number || 0,
          size: response.data.size || 20
        };
      }
      
      return {
        success: false,
        error: response.message || 'Failed to search transactions'
      };
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Failed to search transactions'
      };
    }
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
