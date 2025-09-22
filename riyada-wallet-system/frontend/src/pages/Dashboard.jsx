import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useWallet } from '../contexts/WalletContext';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { balance, transactions, loading, refreshWallet } = useWallet();

  const formatAmount = (amount, type) => {
    const formatted = `$${amount.toFixed(2)}`;
    return type === 'SENT' ? `-${formatted}` : `+${formatted}`;
  };

  const formatDate = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) return 'Today';
    if (diffDays === 2) return 'Yesterday';
    if (diffDays <= 7) return `${diffDays - 1} days ago`;
    return date.toLocaleDateString();
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
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
        <h2 className="text-lg font-medium text-foreground mb-4">Recent Transactions</h2>
        <div className="bg-card border border-border rounded-lg overflow-hidden">
          {loading ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
              <p className="mt-2 text-muted-foreground">Loading transactions...</p>
            </div>
          ) : transactions.length > 0 ? (
            transactions.slice(0, 3).map((transaction) => (
              <div key={transaction.id} className="p-4 border-b border-border last:border-b-0">
                <div className="flex justify-between items-center">
                  <div>
                    <div className="font-medium text-card-foreground">
                      {transaction.type === 'SENT' && `Payment to ${transaction.recipientEmail}`}
                      {transaction.type === 'RECEIVED' && `Payment from ${transaction.senderEmail}`}
                      {transaction.type === 'TOP_UP' && 'Wallet Top-up'}
                    </div>
                    <div className="text-sm text-muted-foreground">
                      {formatDate(transaction.timestamp)}
                    </div>
                    {transaction.description && (
                      <div className="text-sm text-muted-foreground mt-1">
                        {transaction.description}
                      </div>
                    )}
                  </div>
                  <div className={`font-medium ${
                    transaction.type === 'SENT' ? 'text-red-600' : 'text-green-600'
                  }`}>
                    {formatAmount(transaction.amount, transaction.type)}
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="p-8 text-center text-muted-foreground">
              No transactions yet
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;