import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useWallet } from '../contexts/WalletContext';
import { walletAPI, ledgerAPI } from '../services/api';
import Modal from '../components/Modal';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user, token } = useAuth();
  const { balance, transactions, loading, refreshWallet } = useWallet();
  
  // Recent transactions from Ledger Service
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loadingTransactions, setLoadingTransactions] = useState(false);
  
  // Modal state
  const [isAddMoneyModalOpen, setIsAddMoneyModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [isErrorModalOpen, setIsErrorModalOpen] = useState(false);
  const [amount, setAmount] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  // Load recent transactions from Ledger Service
  useEffect(() => {
    if (user && token) {
      loadRecentTransactions();
    }
  }, [user, token]);

  const loadRecentTransactions = async () => {
    if (!user || !token) return;
    
    setLoadingTransactions(true);
    try {
      const response = await ledgerAPI.getRecentTransactions(user.id, token, 5);
      if (response.success) {
        setRecentTransactions(response.transactions || []);
      }
    } catch (error) {
      console.error('Failed to load recent transactions:', error);
    } finally {
      setLoadingTransactions(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatAmount = (amount, isSent) => {
    const formattedAmount = parseFloat(amount).toFixed(2);
    return isSent ? `-$${formattedAmount}` : `+$${formattedAmount}`;
  };

  const isTransactionSent = (transaction) => {
    return transaction.senderUserId === user.id;
  };

  const getStatusIcon = (status, isSent) => {
    if (status === 'COMPLETED') {
      return isSent ? (
        <span className="text-red-600 font-bold">-</span>
      ) : (
        <span className="text-green-600 font-bold">+</span>
      );
    } else if (status === 'FAILED') {
      return <span className="text-red-600 font-bold">✗</span>;
    } else if (status === 'PENDING') {
      return <span className="text-yellow-600 font-bold">⏳</span>;
    }
    return <span className="text-gray-600 font-bold">?</span>;
  };

  const handleAddMoney = () => {
    setAmount('');
    setIsAddMoneyModalOpen(true);
  };

  const handleSubmitAddMoney = async () => {
    if (!amount || isNaN(amount) || parseFloat(amount) <= 0) {
      setErrorMessage('Please enter a valid amount greater than 0');
      setIsErrorModalOpen(true);
      return;
    }

    setIsProcessing(true);
    try {
      const response = await walletAPI.creditWallet(
        user.id, 
        parseFloat(amount), 
        'Added via Dashboard', 
        token
      );
      
      if (response.success) {
        refreshWallet(); // Refresh to show new balance
        loadRecentTransactions(); // Refresh recent transactions
        setIsAddMoneyModalOpen(false);
        setIsSuccessModalOpen(true);
      } else {
        setErrorMessage(`Error: ${response.error}`);
        setIsErrorModalOpen(true);
      }
    } catch (error) {
      setErrorMessage('Failed to add money. Please try again.');
      setIsErrorModalOpen(true);
    } finally {
      setIsProcessing(false);
    }
  };


  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">Dashboard</h1>
        <p className="mt-2 text-muted-foreground">
          Welcome back, {user?.name || 'User'}
        </p>
      </div>

      {/* Balance Card */}
      <div className="mb-8">
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-medium text-card-foreground">Wallet Balance</h2>
            <button
              onClick={refreshWallet}
              disabled={loading}
              className="text-sm text-primary hover:text-primary/80 disabled:opacity-50"
            >
              {loading ? 'Refreshing...' : 'Refresh'}
            </button>
          </div>
          <div className="text-4xl font-bold text-primary mb-2">
            ${balance.toFixed(2)}
          </div>
          <p className="text-sm text-muted-foreground">Available balance</p>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="mb-8">
        <h2 className="text-lg font-medium text-foreground mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button 
            onClick={handleAddMoney}
            className="bg-green-600 text-white p-4 rounded-lg hover:bg-green-700 transition-colors"
          >
            <div className="text-lg font-medium">Add Money</div>
            <div className="text-sm opacity-90">Top up your wallet</div>
          </button>
          <button 
            onClick={() => navigate('/transfer')}
            className="bg-primary text-primary-foreground p-4 rounded-lg hover:bg-primary/90 transition-colors"
          >
            <div className="text-lg font-medium">Send Money</div>
            <div className="text-sm opacity-90">Transfer to another wallet</div>
          </button>
          <button 
            onClick={() => navigate('/history')}
            className="bg-secondary text-secondary-foreground p-4 rounded-lg hover:bg-secondary/90 transition-colors"
          >
            <div className="text-lg font-medium">View History</div>
            <div className="text-sm opacity-90">See all transactions</div>
          </button>
        </div>
      </div>

      {/* Recent Transactions */}
      <div className="mb-8">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-medium text-foreground">Recent Transactions</h2>
          <button
            onClick={() => navigate('/history')}
            className="text-sm text-primary hover:text-primary/80 underline"
          >
            View All
          </button>
        </div>
        <div className="bg-card border border-border rounded-lg overflow-hidden">
          {loadingTransactions ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
              <p className="mt-2 text-muted-foreground">Loading recent transactions...</p>
            </div>
          ) : recentTransactions.length > 0 ? (
            recentTransactions.slice(0, 5).map((transaction) => {
              const isSent = isTransactionSent(transaction);
              const otherEmail = transaction.toEmail || 'Unknown';
              const otherUser = isSent ? 'Payment to' : 'Payment from';
              
              return (
                <div key={transaction.id} className="p-4 border-b border-border last:border-b-0">
                  <div className="flex justify-between items-center">
                    <div className="flex items-center space-x-3">
                      <div className="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center">
                        {getStatusIcon(transaction.status, isSent)}
                      </div>
                      <div>
                        <div className="font-medium text-card-foreground">
                          {otherUser} {otherEmail}
                        </div>
                        <div className="text-sm text-muted-foreground">
                          {formatDate(transaction.createdAt)}
                        </div>
                        {transaction.description && (
                          <div className="text-sm text-muted-foreground mt-1">
                            {transaction.description}
                          </div>
                        )}
                      </div>
                    </div>
                    <div className={`font-medium ${isSent ? 'text-red-600' : 'text-green-600'}`}>
                      {formatAmount(transaction.amount, isSent)}
                    </div>
                  </div>
                </div>
              );
            })
          ) : (
            <div className="p-8 text-center text-muted-foreground">
              No recent transactions
            </div>
          )}
        </div>
      </div>

      {/* Add Money Modal */}
      <Modal
        isOpen={isAddMoneyModalOpen}
        onClose={() => setIsAddMoneyModalOpen(false)}
        title="Add Money"
      >
        <div className="space-y-4">
          <div>
            <label htmlFor="amount" className="block text-sm font-medium text-card-foreground mb-2">
              Enter amount to add
            </label>
            <input
              id="amount"
              type="number"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="0.00"
              className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-ring"
              onKeyPress={(e) => e.key === 'Enter' && handleSubmitAddMoney()}
              autoFocus
            />
          </div>
          
          <div className="flex gap-3 pt-4">
            <button
              onClick={() => setIsAddMoneyModalOpen(false)}
              className="flex-1 px-4 py-2 border border-border rounded-md text-foreground hover:bg-muted transition-colors"
              disabled={isProcessing}
            >
              Cancel
            </button>
            <button
              onClick={handleSubmitAddMoney}
              disabled={isProcessing || !amount}
              className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {isProcessing ? 'Processing...' : 'Add Money'}
            </button>
          </div>
        </div>
      </Modal>

      {/* Success Modal */}
      <Modal
        isOpen={isSuccessModalOpen}
        onClose={() => setIsSuccessModalOpen(false)}
        title="Success"
      >
        <div className="text-center space-y-4">
          <div className="flex justify-center">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
              <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>
          <div>
            <h3 className="text-lg font-medium text-card-foreground">Successfully Added!</h3>
            <p className="text-muted-foreground mt-1">
              ${amount} has been added to your wallet.
            </p>
          </div>
          <button
            onClick={() => setIsSuccessModalOpen(false)}
            className="w-full px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
          >
            Close
          </button>
        </div>
      </Modal>

      {/* Error Modal */}
      <Modal
        isOpen={isErrorModalOpen}
        onClose={() => setIsErrorModalOpen(false)}
        title="Error"
      >
        <div className="text-center space-y-4">
          <div className="flex justify-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
              <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
          </div>
          <div>
            <h3 className="text-lg font-medium text-card-foreground">Operation Failed</h3>
            <p className="text-muted-foreground mt-1">
              {errorMessage}
            </p>
          </div>
          <button
            onClick={() => setIsErrorModalOpen(false)}
            className="w-full px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
          >
            Close
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default Dashboard;