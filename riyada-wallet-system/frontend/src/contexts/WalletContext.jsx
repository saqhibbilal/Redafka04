import React, { createContext, useContext, useState, useEffect } from 'react';
import { walletAPI } from '../services/api';
import { useAuth } from './AuthContext';

const WalletContext = createContext();

export const useWallet = () => {
  const context = useContext(WalletContext);
  if (!context) {
    throw new Error('useWallet must be used within a WalletProvider');
  }
  return context;
};

export const WalletProvider = ({ children }) => {
  const { user, isAuthenticated } = useAuth();
  const [balance, setBalance] = useState(0);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  // Load wallet data when user is authenticated
  useEffect(() => {
    if (isAuthenticated && user) {
      loadWalletData();
    } else {
      setBalance(0);
      setTransactions([]);
    }
  }, [isAuthenticated, user]);

  const loadWalletData = async () => {
    if (!user) return;
    
    setLoading(true);
    try {
      // Load balance and transactions in parallel
      const [balanceResponse, transactionsResponse] = await Promise.all([
        walletAPI.getBalance(user.id),
        walletAPI.getTransactions(user.id)
      ]);

      if (balanceResponse.success) {
        setBalance(balanceResponse.balance);
      }

      if (transactionsResponse.success) {
        setTransactions(transactionsResponse.transactions);
      }
    } catch (error) {
      console.error('Failed to load wallet data:', error);
    } finally {
      setLoading(false);
    }
  };

  const transfer = async (toEmail, amount, description) => {
    if (!user) return { success: false, error: 'Not authenticated' };

    setLoading(true);
    try {
      const response = await walletAPI.transfer(user.id, toEmail, amount, description);
      
      if (response.success) {
        // Update local state
        setBalance(response.newBalance);
        setTransactions(prev => [response.transaction, ...prev]);
        
        return { success: true, transaction: response.transaction };
      } else {
        return { success: false, error: response.error };
      }
    } catch (error) {
      return { success: false, error: 'Transfer failed. Please try again.' };
    } finally {
      setLoading(false);
    }
  };

  const refreshWallet = () => {
    if (isAuthenticated && user) {
      loadWalletData();
    }
  };

  const value = {
    balance,
    transactions,
    loading,
    transfer,
    refreshWallet,
    currency: 'USD'
  };

  return (
    <WalletContext.Provider value={value}>
      {children}
    </WalletContext.Provider>
  );
};
