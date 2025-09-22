// Mock data for testing UI components
// This will be replaced with real API calls in Phase 2

export const mockUsers = [
  {
    id: 1,
    email: 'demo@riyada.com',
    password: 'password123',
    name: 'Demo User',
    phone: '+1234567890'
  },
  {
    id: 2,
    email: 'test@riyada.com',
    password: 'test123',
    name: 'Test User',
    phone: '+0987654321'
  }
];

export const mockWallets = [
  {
    id: 1,
    userId: 1,
    balance: 2450.00,
    currency: 'USD',
    status: 'ACTIVE'
  },
  {
    id: 2,
    userId: 2,
    balance: 1250.50,
    currency: 'USD',
    status: 'ACTIVE'
  }
];

export const mockTransactions = [
  {
    id: 1,
    userId: 1,
    type: 'SENT',
    amount: 50.00,
    recipientEmail: 'john.doe@email.com',
    description: 'Lunch payment',
    timestamp: new Date().toISOString(),
    status: 'COMPLETED'
  },
  {
    id: 2,
    userId: 1,
    type: 'RECEIVED',
    amount: 125.00,
    senderEmail: 'jane.smith@email.com',
    description: 'Shared dinner bill',
    timestamp: new Date(Date.now() - 86400000).toISOString(), // Yesterday
    status: 'COMPLETED'
  },
  {
    id: 3,
    userId: 1,
    type: 'TOP_UP',
    amount: 500.00,
    description: 'Monthly wallet funding',
    timestamp: new Date(Date.now() - 172800000).toISOString(), // 2 days ago
    status: 'COMPLETED'
  },
  {
    id: 4,
    userId: 1,
    type: 'SENT',
    amount: 8.50,
    recipientEmail: 'coffee@shop.com',
    description: 'Morning coffee',
    timestamp: new Date(Date.now() - 259200000).toISOString(), // 3 days ago
    status: 'COMPLETED'
  }
];

export const mockNotifications = [
  {
    id: 1,
    userId: 1,
    type: 'TRANSACTION',
    title: 'Payment Sent',
    message: 'You sent $50.00 to john.doe@email.com',
    timestamp: new Date().toISOString(),
    read: false
  },
  {
    id: 2,
    userId: 1,
    type: 'TRANSACTION',
    title: 'Payment Received',
    message: 'You received $125.00 from jane.smith@email.com',
    timestamp: new Date(Date.now() - 86400000).toISOString(),
    read: true
  }
];
