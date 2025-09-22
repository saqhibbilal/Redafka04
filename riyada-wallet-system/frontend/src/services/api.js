// Mock API service - returns mock data
// In Phase 2, this will make real HTTP requests to backend services

import { mockUsers, mockWallets, mockTransactions, mockNotifications } from '../data/mockData.js';

// Simulate API delay
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Mock authentication
export const authAPI = {
  login: async (email, password) => {
    await delay(1000); // Simulate network delay
    
    const user = mockUsers.find(u => u.email === email && u.password === password);
    
    if (user) {
      // Mock JWT token
      const token = `mock-jwt-token-${user.id}-${Date.now()}`;
      return {
        success: true,
        user: {
          id: user.id,
          email: user.email,
          name: user.name,
          phone: user.phone
        },
        token
      };
    }
    
    return {
      success: false,
      error: 'Invalid email or password'
    };
  },

  register: async (userData) => {
    await delay(1000);
    
    // Check if user already exists
    const existingUser = mockUsers.find(u => u.email === userData.email);
    if (existingUser) {
      return {
        success: false,
        error: 'User already exists'
      };
    }
    
    // Create new user (in real app, this would be saved to database)
    const newUser = {
      id: mockUsers.length + 1,
      ...userData
    };
    
    return {
      success: true,
      user: {
        id: newUser.id,
        email: newUser.email,
        name: newUser.name,
        phone: newUser.phone
      },
      token: `mock-jwt-token-${newUser.id}-${Date.now()}`
    };
  }
};

// Mock wallet API
export const walletAPI = {
  getBalance: async (userId) => {
    await delay(500);
    
    const wallet = mockWallets.find(w => w.userId === userId);
    return {
      success: true,
      balance: wallet ? wallet.balance : 0,
      currency: wallet ? wallet.currency : 'USD'
    };
  },

  getTransactions: async (userId) => {
    await delay(500);
    
    const transactions = mockTransactions.filter(t => t.userId === userId);
    return {
      success: true,
      transactions: transactions.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
    };
  },

  transfer: async (fromUserId, toEmail, amount, description) => {
    await delay(2000); // Simulate processing time
    
    const fromWallet = mockWallets.find(w => w.userId === fromUserId);
    if (!fromWallet || fromWallet.balance < amount) {
      return {
        success: false,
        error: 'Insufficient balance'
      };
    }
    
    // Update balance (in real app, this would be atomic database operation)
    fromWallet.balance -= amount;
    
    // Create transaction record
    const newTransaction = {
      id: mockTransactions.length + 1,
      userId: fromUserId,
      type: 'SENT',
      amount,
      recipientEmail: toEmail,
      description: description || 'Transfer',
      timestamp: new Date().toISOString(),
      status: 'COMPLETED'
    };
    
    mockTransactions.unshift(newTransaction);
    
    return {
      success: true,
      transaction: newTransaction,
      newBalance: fromWallet.balance
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
