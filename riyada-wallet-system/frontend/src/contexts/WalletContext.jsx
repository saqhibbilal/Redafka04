import React, { createContext, useContext, useState, useEffect } from 'react';
import { walletAPI, paymentAPI } from '../services/api';
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
  const { user, token, isAuthenticated } = useAuth();
  const [balance, setBalance] = useState(0);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [wallet, setWallet] = useState(null);

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
    if (!user || !token) return;
    
    setLoading(true);
    try {
      // Load balance and transactions in parallel
      const [balanceResponse, transactionsResponse] = await Promise.all([
        walletAPI.getBalance(user.id, token),
        walletAPI.getTransactions(user.id, token)
      ]);

      if (balanceResponse.success) {
        setBalance(balanceResponse.balance);
      }

      if (transactionsResponse.success) {
        setTransactions(transactionsResponse.transactions);
      }
    } catch (error) {
      console.error('Failed to load wallet data:', error);
      // If wallet doesn't exist, try to create one
      if (error.message && error.message.includes('Wallet not found')) {
        await createWalletIfNeeded();
      }
    } finally {
      setLoading(false);
    }
  };

  const createWalletIfNeeded = async () => {
    if (!user || !token) return;
    
    try {
      const response = await walletAPI.createWallet(user.id, token);
      if (response.success) {
        setWallet(response.wallet);
        setBalance(response.wallet.balance);
        console.log('Wallet created successfully');
      }
    } catch (error) {
      console.error('Failed to create wallet:', error);
    }
  };

  const transfer = async (toEmail, amount, description) => {
    if (!user || !token) return { success: false, error: 'Not authenticated' };

    setLoading(true);
    try {
      // Use Payment Service for transfers
      const response = await paymentAPI.transfer(toEmail, amount, description, token);
      
      if (response.success) {
        // Refresh wallet data to get updated balance and transactions
        await loadWalletData();
        
        return { 
          success: true, 
          payment: response.payment,
          message: response.message 
        };
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
    wallet,
    transfer,
    refreshWallet,
    createWalletIfNeeded,
    currency: 'USD'
  };

  return (
    <WalletContext.Provider value={value}>
      {children}
    </WalletContext.Provider>
  );
};
