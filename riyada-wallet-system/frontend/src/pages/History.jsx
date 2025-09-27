import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { paymentAPI, ledgerAPI, userAPI } from '../services/api';

const History = () => {
  const { user, token, isAuthenticated } = useAuth();
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  
  // Filter state
  const [filters, setFilters] = useState({
    status: '',
    transactionType: '',
    startDate: '',
    endDate: ''
  });
  
  // Transaction details modal state
  const [selectedTransaction, setSelectedTransaction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [auditTrail, setAuditTrail] = useState([]);
  const [loadingAudit, setLoadingAudit] = useState(false);
  const [senderEmail, setSenderEmail] = useState('');
  const [receiverEmail, setReceiverEmail] = useState('');

  useEffect(() => {
    if (isAuthenticated && user && token) {
      loadTransactions();
    }
  }, [isAuthenticated, user, token, currentPage, pageSize]);

  useEffect(() => {
    // Reset to first page when filters change
    setCurrentPage(0);
    loadTransactions();
  }, [filters]);

  const loadTransactions = async () => {
    if (!user || !token) return;
    
    setLoading(true);
    setError('');
    try {
      // Use searchTransactions with filters if any filters are applied
      const hasFilters = filters.status || filters.transactionType || filters.startDate || filters.endDate;
      
      let response;
      if (hasFilters) {
        // Use search with filters
        response = await ledgerAPI.searchTransactions(user.id, token, {
          ...filters,
          page: currentPage,
          size: pageSize
        });
      } else {
        // Use regular pagination
        response = await ledgerAPI.getUserTransactions(user.id, token, currentPage, pageSize);
      }
      
      if (response.success) {
        setTransactions(response.transactions || []);
        setTotalPages(response.totalPages || 0);
        setTotalElements(response.totalElements || 0);
      } else {
        // Fallback to Payment Service for backward compatibility
        console.log('Ledger Service not available, falling back to Payment Service');
        const fallbackResponse = await paymentAPI.getUserPayments(user.id, token);
        
        if (fallbackResponse.success) {
          setTransactions(fallbackResponse.payments || []);
          setTotalPages(1);
          setTotalElements(fallbackResponse.payments?.length || 0);
        } else {
          setError(fallbackResponse.error || response.error);
        }
      }
    } catch (error) {
      setError('Failed to load transaction history');
      console.error('Failed to load transaction history:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadTransactionDetails = async (transaction) => {
    if (!token) return;
    
    setLoadingAudit(true);
    setSenderEmail('');
    setReceiverEmail('');
    
    try {
      // Load audit trail
      const auditResponse = await ledgerAPI.getAuditTrail(transaction.id, token);
      if (auditResponse.success) {
        setAuditTrail(auditResponse.auditLogs || []);
      }

      // Load sender and receiver emails
      if (transaction.senderUserId && transaction.senderUserId !== user.id) {
        try {
          const senderResponse = await userAPI.getUserProfile(transaction.senderUserId, token);
          if (senderResponse.success) {
            setSenderEmail(senderResponse.user.email);
          }
        } catch (error) {
          console.log('Could not load sender email:', error);
        }
      }

      if (transaction.receiverUserId && transaction.receiverUserId !== user.id) {
        try {
          const receiverResponse = await userAPI.getUserProfile(transaction.receiverUserId, token);
          if (receiverResponse.success) {
            setReceiverEmail(receiverResponse.user.email);
          }
        } catch (error) {
          console.log('Could not load receiver email:', error);
        }
      }
    } catch (error) {
      console.error('Failed to load transaction details:', error);
    } finally {
      setLoadingAudit(false);
    }
  };

  const handleTransactionClick = (transaction) => {
    setSelectedTransaction(transaction);
    setShowModal(true);
    loadTransactionDetails(transaction);
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize);
    setCurrentPage(0);
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

  const isTransactionSent = (transaction) => {
    // For ledger transactions, check senderUserId
    if (transaction.senderUserId) {
      return transaction.senderUserId === user.id;
    }
    // For payment fallback, check fromUserId
    return transaction.fromUserId === user.id;
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
              <select 
                value={filters.status}
                onChange={(e) => handleFilterChange('status', e.target.value)}
                className="px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              >
                <option value="">All Status</option>
                <option value="COMPLETED">Completed</option>
                <option value="FAILED">Failed</option>
                <option value="PENDING">Pending</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
              <select 
                value={filters.transactionType}
                onChange={(e) => handleFilterChange('transactionType', e.target.value)}
                className="px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              >
                <option value="">All Types</option>
                <option value="TRANSFER">Sent</option>
                <option value="DEPOSIT">Top-up</option>
                <option value="WITHDRAWAL">Withdrawal</option>
                <option value="REFUND">Refund</option>
              </select>
              <input
                type="date"
                value={filters.startDate}
                onChange={(e) => handleFilterChange('startDate', e.target.value)}
                placeholder="Start Date"
                className="px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              />
              <input
                type="date"
                value={filters.endDate}
                onChange={(e) => handleFilterChange('endDate', e.target.value)}
                placeholder="End Date"
                className="px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              />
              <button
                onClick={() => setFilters({ status: '', transactionType: '', startDate: '', endDate: '' })}
                className="px-4 py-2 bg-muted text-muted-foreground rounded-md hover:bg-muted/80 focus:outline-none focus:ring-2 focus:ring-ring"
              >
                Clear Filters
              </button>
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

          {!loading && !error && transactions.length === 0 && (
            <div className="bg-card border border-border rounded-lg p-8 text-center">
              <div className="text-muted-foreground">No transaction history found</div>
              <div className="text-sm text-muted-foreground mt-2">
                Your transaction history will appear here
              </div>
            </div>
          )}

          {!loading && !error && transactions.length > 0 && (
            <>
              <div className="bg-card border border-border rounded-lg overflow-hidden shadow-sm">
                {transactions.map((transaction) => {
                  const isSent = isTransactionSent(transaction);
                  const otherEmail = transaction.toEmail || 'Unknown';
                  const otherUser = isSent ? 'Payment to' : 'Payment from';
                  const displayId = transaction.referenceId || transaction.id;
                  
                  return (
                    <div 
                      key={transaction.id} 
                      className="p-4 border-b border-border last:border-b-0 cursor-pointer hover:bg-muted/50 transition-colors"
                      onClick={() => handleTransactionClick(transaction)}
                    >
                      <div className="flex justify-between items-center">
                        <div className="flex items-center space-x-4">
                          <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                            {getStatusIcon(transaction.status, isSent)}
                          </div>
                          <div>
                            <div className="font-medium text-card-foreground">
                              {otherUser} {otherEmail}
                            </div>
                            <div className="text-sm text-muted-foreground">
                              {displayId}
                            </div>
                            {transaction.transactionType && (
                              <div className="text-xs text-muted-foreground">
                                {transaction.transactionType}
                              </div>
                            )}
                          </div>
                        </div>
                        <div className="text-right">
                          <div className={`font-medium ${isSent ? 'text-red-600' : 'text-green-600'}`}>
                            {formatAmount(transaction.amount, isSent)}
                          </div>
                          <div className="text-sm text-muted-foreground">
                            {formatDate(transaction.createdAt)}
                          </div>
                          <div className={`text-xs ${getStatusColor(transaction.status)}`}>
                            {transaction.status}
                          </div>
                        </div>
                      </div>
                      {transaction.description && (
                        <div className="mt-2 text-sm text-muted-foreground">
                          {transaction.description}
                        </div>
                      )}
                      {transaction.failureReason && (
                        <div className="mt-2 text-sm text-red-600">
                          Error: {transaction.failureReason}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="mt-6 flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <span className="text-sm text-muted-foreground">
                      Showing {currentPage * pageSize + 1} to {Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements} transactions
                    </span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <select
                      value={pageSize}
                      onChange={(e) => handlePageSizeChange(parseInt(e.target.value))}
                      className="px-3 py-1 border border-input rounded-md bg-background text-foreground text-sm"
                    >
                      <option value={10}>10 per page</option>
                      <option value={20}>20 per page</option>
                      <option value={50}>50 per page</option>
                    </select>
                    <button
                      onClick={() => handlePageChange(currentPage - 1)}
                      disabled={currentPage === 0}
                      className="px-3 py-1 border border-input rounded-md bg-background text-foreground text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Previous
                    </button>
                    <span className="px-3 py-1 text-sm text-muted-foreground">
                      Page {currentPage + 1} of {totalPages}
                    </span>
                    <button
                      onClick={() => handlePageChange(currentPage + 1)}
                      disabled={currentPage >= totalPages - 1}
                      className="px-3 py-1 border border-input rounded-md bg-background text-foreground text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Next
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* Transaction Details Modal */}
      {showModal && selectedTransaction && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-background border border-border rounded-lg p-6 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-foreground">Transaction Details</h2>
              <button
                onClick={() => setShowModal(false)}
                className="text-muted-foreground hover:text-foreground"
              >
                ✕
              </button>
            </div>

            <div className="space-y-4">
              {/* Transaction Info */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Transaction ID</label>
                  <p className="text-foreground">{selectedTransaction.id}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Status</label>
                  <p className={`${getStatusColor(selectedTransaction.status)} font-medium`}>
                    {selectedTransaction.status}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Amount</label>
                  <p className="text-foreground font-medium">
                    ${parseFloat(selectedTransaction.amount).toFixed(2)} {selectedTransaction.currency}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Type</label>
                  <p className="text-foreground">{selectedTransaction.transactionType || 'TRANSFER'}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Date</label>
                  <p className="text-foreground">{formatDate(selectedTransaction.createdAt)}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Direction</label>
                  <p className="text-foreground">
                    {isTransactionSent(selectedTransaction) ? 'Sent' : 'Received'}
                  </p>
                </div>
              </div>

              {/* Sender/Receiver Information */}
              {selectedTransaction.senderUserId && selectedTransaction.senderUserId !== user.id && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">From (Sender)</label>
                  <p className="text-foreground">
                    {senderEmail || `User ID: ${selectedTransaction.senderUserId}`}
                  </p>
                </div>
              )}

              {selectedTransaction.receiverUserId && selectedTransaction.receiverUserId !== user.id && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">To (Receiver)</label>
                  <p className="text-foreground">
                    {receiverEmail || `User ID: ${selectedTransaction.receiverUserId}`}
                  </p>
                </div>
              )}

              {selectedTransaction.description && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Description</label>
                  <p className="text-foreground">{selectedTransaction.description}</p>
                </div>
              )}

              {selectedTransaction.failureReason && (
                <div>
                  <label className="text-sm font-medium text-muted-foreground">Failure Reason</label>
                  <p className="text-red-600">{selectedTransaction.failureReason}</p>
                </div>
              )}

              {/* Audit Trail */}
              <div>
                <label className="text-sm font-medium text-muted-foreground">Audit Trail</label>
                {loadingAudit ? (
                  <div className="text-muted-foreground">Loading audit trail...</div>
                ) : auditTrail.length > 0 ? (
                  <div className="mt-2 space-y-2">
                    {auditTrail.map((log, index) => (
                      <div key={index} className="p-3 bg-muted rounded-md">
                        <div className="flex justify-between items-start">
                          <div>
                            <p className="font-medium text-foreground">{log.action}</p>
                            <p className="text-sm text-muted-foreground">
                              {formatDate(log.createdAt)}
                            </p>
                          </div>
                        </div>
                        {log.oldValues && (
                          <div className="mt-2">
                            <p className="text-xs text-muted-foreground">Previous:</p>
                            <p className="text-xs text-foreground font-mono bg-background p-1 rounded">
                              {log.oldValues}
                            </p>
                          </div>
                        )}
                        {log.newValues && (
                          <div className="mt-1">
                            <p className="text-xs text-muted-foreground">Updated:</p>
                            <p className="text-xs text-foreground font-mono bg-background p-1 rounded">
                              {log.newValues}
                            </p>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-muted-foreground">No audit trail available</div>
                )}
              </div>
            </div>

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default History;
