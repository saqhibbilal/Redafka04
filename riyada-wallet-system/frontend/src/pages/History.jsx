import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { paymentAPI } from '../services/api';

const History = () => {
  const { user, token, isAuthenticated } = useAuth();
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isAuthenticated && user && token) {
      loadPaymentHistory();
    }
  }, [isAuthenticated, user, token]);

  const loadPaymentHistory = async () => {
    if (!user || !token) return;
    
    setLoading(true);
    setError('');
    try {
      const response = await paymentAPI.getUserPayments(user.id, token);
      
      if (response.success) {
        setPayments(response.payments);
      } else {
        setError(response.error);
      }
    } catch (error) {
      setError('Failed to load payment history');
      console.error('Failed to load payment history:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
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

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'text-green-600';
      case 'FAILED':
        return 'text-red-600';
      case 'PENDING':
        return 'text-yellow-600';
      case 'PROCESSING':
        return 'text-blue-600';
      case 'CANCELLED':
        return 'text-gray-600';
      default:
        return 'text-gray-600';
    }
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
    } else if (status === 'PROCESSING') {
      return <span className="text-blue-600 font-bold">⟳</span>;
    } else if (status === 'CANCELLED') {
      return <span className="text-gray-600 font-bold">⊘</span>;
    }
    return <span className="text-gray-600 font-bold">?</span>;
  };
  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="px-4 py-6 sm:px-0">
          <h1 className="text-3xl font-bold text-foreground">Transaction History</h1>
          <p className="mt-2 text-muted-foreground">View all your wallet transactions</p>
        </div>

        {/* Filters */}
        <div className="px-4 py-6 sm:px-0">
          <div className="bg-card border border-border rounded-lg p-4 shadow-sm">
            <div className="flex flex-wrap gap-4">
              <select className="px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring">
                <option>All Transactions</option>
                <option>Sent</option>
                <option>Received</option>
                <option>Top-up</option>
              </select>
              <select className="px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring">
                <option>All Time</option>
                <option>Last 7 days</option>
                <option>Last 30 days</option>
                <option>Last 3 months</option>
              </select>
              <input
                type="text"
                placeholder="Search transactions..."
                className="px-3 py-2 border border-input rounded-md bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              />
            </div>
          </div>
        </div>

        {/* Payment List */}
        <div className="px-4 py-6 sm:px-0">
          {loading && (
            <div className="bg-card border border-border rounded-lg p-8 text-center">
              <div className="text-muted-foreground">Loading payment history...</div>
            </div>
          )}

          {error && (
            <div className="bg-card border border-border rounded-lg p-4">
              <div className="text-red-600 text-center">{error}</div>
              <div className="text-center mt-2">
                <button 
                  onClick={loadPaymentHistory}
                  className="text-blue-600 hover:text-blue-800 underline"
                >
                  Try again
                </button>
              </div>
            </div>
          )}

          {!loading && !error && payments.length === 0 && (
            <div className="bg-card border border-border rounded-lg p-8 text-center">
              <div className="text-muted-foreground">No payment history found</div>
              <div className="text-sm text-muted-foreground mt-2">
                Your payment transactions will appear here
              </div>
            </div>
          )}

          {!loading && !error && payments.length > 0 && (
            <div className="bg-card border border-border rounded-lg overflow-hidden shadow-sm">
              {payments.map((payment) => {
                const isSent = payment.fromUserId === user.id;
                const otherEmail = isSent ? payment.toEmail : 'Unknown';
                const otherUser = isSent ? 'Payment to' : 'Payment from';
                
                return (
                  <div key={payment.id} className="p-4 border-b border-border last:border-b-0">
                    <div className="flex justify-between items-center">
                      <div className="flex items-center space-x-4">
                        <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                          {getStatusIcon(payment.status, isSent)}
                        </div>
                        <div>
                          <div className="font-medium text-card-foreground">
                            {otherUser} {otherEmail}
                          </div>
                          <div className="text-sm text-muted-foreground">
                            {payment.referenceId}
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className={`font-medium ${isSent ? 'text-red-600' : 'text-green-600'}`}>
                          {formatAmount(payment.amount, isSent)}
                        </div>
                        <div className="text-sm text-muted-foreground">
                          {formatDate(payment.createdAt)}
                        </div>
                        <div className={`text-xs ${getStatusColor(payment.status)}`}>
                          {payment.status}
                        </div>
                      </div>
                    </div>
                    {payment.description && (
                      <div className="mt-2 text-sm text-muted-foreground">
                        {payment.description}
                      </div>
                    )}
                    {payment.failureReason && (
                      <div className="mt-2 text-sm text-red-600">
                        Error: {payment.failureReason}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default History;
