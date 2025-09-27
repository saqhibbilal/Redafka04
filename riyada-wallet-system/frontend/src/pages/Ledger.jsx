import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { ledgerAPI, walletAPI } from '../services/api';
import { 
  PieChart, 
  Pie, 
  Cell, 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  LineChart,
  Line
} from 'recharts';

const Ledger = () => {
  const { user, token, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [financialSummary, setFinancialSummary] = useState(null);
  const [transactionCategories, setTransactionCategories] = useState([]);
  const [monthlyData, setMonthlyData] = useState([]);
  const [balance, setBalance] = useState(null);
  const [auditTrails, setAuditTrails] = useState([]);
  const [auditFilters, setAuditFilters] = useState({
    action: '',
    startDate: '',
    endDate: '',
    searchTerm: ''
  });
  const [auditLoading, setAuditLoading] = useState(false);

  // Chart colors
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D'];

  useEffect(() => {
    if (isAuthenticated && user && token) {
      loadFinancialData();
      loadBalance();
      loadAuditTrails();
    }
  }, [isAuthenticated, user, token]);

  useEffect(() => {
    // Reload audit trails when filters change
    loadAuditTrails();
  }, [auditFilters]);

  const loadFinancialData = async () => {
    if (!user || !token) return;
    
    setLoading(true);
    setError('');
    try {
      // Load financial summary
      const summaryResponse = await ledgerAPI.getFinancialSummary(user.id, token);
      if (summaryResponse.success) {
        setFinancialSummary(summaryResponse.summary);
      }

      // Load transaction categories data
      const categoriesResponse = await ledgerAPI.getUserTransactions(user.id, token, 0, 100);
      if (categoriesResponse.success) {
        const transactions = categoriesResponse.transactions || [];
        const categoryData = processTransactionCategories(transactions);
        setTransactionCategories(categoryData);
        
        // Process monthly data
        const monthlyData = processMonthlyData(transactions);
        setMonthlyData(monthlyData);
      }
    } catch (error) {
      setError('Failed to load financial data');
      console.error('Failed to load financial data:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadBalance = async () => {
    if (!user || !token) return;
    
    try {
      const response = await walletAPI.getBalance(user.id, token);
      if (response.success) {
        setBalance(response);
      }
    } catch (error) {
      console.error('Failed to load balance:', error);
    }
  };

  const loadAuditTrails = async () => {
    if (!user || !token) return;
    
    setAuditLoading(true);
    try {
      // Get all transactions first
      const transactionsResponse = await ledgerAPI.getUserTransactions(user.id, token, 0, 1000);
      if (transactionsResponse.success) {
        const transactions = transactionsResponse.transactions || [];
        const allAuditTrails = [];

        // Load audit trail for each transaction
        for (const transaction of transactions) {
          try {
            const auditResponse = await ledgerAPI.getAuditTrail(transaction.id, token);
            if (auditResponse.success && auditResponse.auditLogs) {
              auditResponse.auditLogs.forEach(log => {
                allAuditTrails.push({
                  ...log,
                  transactionId: transaction.id,
                  transactionAmount: transaction.amount,
                  transactionType: transaction.transactionType
                });
              });
            }
          } catch (error) {
            console.log(`Could not load audit trail for transaction ${transaction.id}:`, error);
          }
        }

        // Apply filters
        let filteredTrails = allAuditTrails;
        
        if (auditFilters.action) {
          filteredTrails = filteredTrails.filter(trail => 
            trail.action.toLowerCase().includes(auditFilters.action.toLowerCase())
          );
        }
        
        if (auditFilters.startDate) {
          filteredTrails = filteredTrails.filter(trail => 
            new Date(trail.createdAt) >= new Date(auditFilters.startDate)
          );
        }
        
        if (auditFilters.endDate) {
          filteredTrails = filteredTrails.filter(trail => 
            new Date(trail.createdAt) <= new Date(auditFilters.endDate + 'T23:59:59')
          );
        }
        
        if (auditFilters.searchTerm) {
          const searchLower = auditFilters.searchTerm.toLowerCase();
          filteredTrails = filteredTrails.filter(trail => 
            trail.action.toLowerCase().includes(searchLower) ||
            (trail.oldValues && trail.oldValues.toLowerCase().includes(searchLower)) ||
            (trail.newValues && trail.newValues.toLowerCase().includes(searchLower))
          );
        }

        // Sort by date (newest first)
        filteredTrails.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        
        setAuditTrails(filteredTrails);
      }
    } catch (error) {
      console.error('Failed to load audit trails:', error);
    } finally {
      setAuditLoading(false);
    }
  };

  const exportAuditReport = () => {
    if (auditTrails.length === 0) return;

    // Create CSV content
    const csvHeaders = ['Date', 'Action', 'Transaction ID', 'Transaction Type', 'Amount', 'Previous Values', 'Updated Values'];
    const csvRows = auditTrails.map(trail => [
      formatDate(trail.createdAt),
      trail.action,
      trail.transactionId,
      trail.transactionType,
      trail.transactionAmount,
      trail.oldValues ? (typeof trail.oldValues === 'string' ? trail.oldValues : JSON.stringify(trail.oldValues)) : '',
      trail.newValues ? (typeof trail.newValues === 'string' ? trail.newValues : JSON.stringify(trail.newValues)) : ''
    ]);

    // Convert to CSV string
    const csvContent = [csvHeaders, ...csvRows]
      .map(row => row.map(cell => `"${String(cell).replace(/"/g, '""')}"`).join(','))
      .join('\n');

    // Create and download file
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `audit-report-${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const processTransactionCategories = (transactions) => {
    const categories = {
      'TRANSFER': { name: 'Transfers', count: 0, amount: 0, color: '#0088FE' },
      'DEPOSIT': { name: 'Top-ups', count: 0, amount: 0, color: '#00C49F' },
      'WITHDRAWAL': { name: 'Withdrawals', count: 0, amount: 0, color: '#FFBB28' },
      'REFUND': { name: 'Refunds', count: 0, amount: 0, color: '#FF8042' }
    };

    transactions.forEach(transaction => {
      const type = transaction.transactionType || 'TRANSFER';
      if (categories[type]) {
        categories[type].count += 1;
        categories[type].amount += parseFloat(transaction.amount);
      }
    });

    return Object.values(categories).filter(cat => cat.count > 0);
  };

  const processMonthlyData = (transactions) => {
    const monthlyTotals = {};
    
    transactions.forEach(transaction => {
      const date = new Date(transaction.createdAt);
      const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      
      if (!monthlyTotals[monthKey]) {
        monthlyTotals[monthKey] = { month: monthKey, income: 0, expense: 0, net: 0 };
      }
      
      const amount = parseFloat(transaction.amount);
      const isSent = transaction.senderUserId === user.id;
      
      if (isSent) {
        monthlyTotals[monthKey].expense += amount;
      } else {
        monthlyTotals[monthKey].income += amount;
      }
      monthlyTotals[monthKey].net = monthlyTotals[monthKey].income - monthlyTotals[monthKey].expense;
    });

    return Object.values(monthlyTotals).sort((a, b) => a.month.localeCompare(b.month));
  };

  const formatCurrency = (amount) => {
    return `$${parseFloat(amount).toFixed(2)}`;
  };

  const formatMonth = (monthKey) => {
    const [year, month] = monthKey.split('-');
    const date = new Date(year, month - 1);
    return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto p-6">
        <div className="text-center">
          <div className="text-muted-foreground">Loading financial data...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">Financial Dashboard</h1>
        <p className="mt-2 text-muted-foreground">
          Complete financial overview and transaction analytics
        </p>
      </div>

      {error && (
        <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="text-red-600">{error}</div>
          <div className="text-center mt-2">
            <button 
              onClick={loadFinancialData}
              className="text-blue-600 hover:text-blue-800 underline"
            >
              Try again
            </button>
          </div>
        </div>
      )}

      {/* Balance Overview */}
      {balance && (
        <div className="mb-8">
          <div className="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg p-6 text-white">
            <h2 className="text-xl font-semibold mb-2">Current Balance</h2>
            <div className="text-3xl font-bold">
              {formatCurrency(balance.balance)} {balance.currency}
            </div>
          </div>
        </div>
      )}

      {/* Financial Summary Cards */}
      {financialSummary && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
            <h3 className="text-lg font-medium text-card-foreground mb-2">Total Income</h3>
            <div className="text-2xl font-bold text-green-600">
              {formatCurrency(financialSummary.totalIncome || 0)}
            </div>
          </div>
          <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
            <h3 className="text-lg font-medium text-card-foreground mb-2">Total Expenses</h3>
            <div className="text-2xl font-bold text-red-600">
              {formatCurrency(financialSummary.totalExpenses || 0)}
            </div>
          </div>
          <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
            <h3 className="text-lg font-medium text-card-foreground mb-2">Net Balance</h3>
            <div className={`text-2xl font-bold ${(financialSummary.netBalance || 0) >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              {formatCurrency(financialSummary.netBalance || 0)}
            </div>
          </div>
          <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
            <h3 className="text-lg font-medium text-card-foreground mb-2">Transaction Count</h3>
            <div className="text-2xl font-bold text-blue-600">
              {financialSummary.transactionCount || 0}
            </div>
          </div>
        </div>
      )}

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
        {/* Transaction Categories Pie Chart */}
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h3 className="text-lg font-medium text-card-foreground mb-4">Transaction Categories</h3>
          {transactionCategories.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={transactionCategories}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, count }) => `${name}: ${count}`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="count"
                >
                  {transactionCategories.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color || COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value, name) => [value, 'Transactions']} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="text-center text-muted-foreground py-12">
              No transaction data available
            </div>
          )}
        </div>

        {/* Monthly Income vs Expenses */}
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h3 className="text-lg font-medium text-card-foreground mb-4">Monthly Income vs Expenses</h3>
          {monthlyData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="month" 
                  tickFormatter={formatMonth}
                  angle={-45}
                  textAnchor="end"
                  height={80}
                />
                <YAxis />
                <Tooltip 
                  formatter={(value, name) => [formatCurrency(value), name]}
                  labelFormatter={formatMonth}
                />
                <Bar dataKey="income" fill="#00C49F" name="Income" />
                <Bar dataKey="expense" fill="#FF8042" name="Expenses" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="text-center text-muted-foreground py-12">
              No monthly data available
            </div>
          )}
        </div>
      </div>

      {/* Net Balance Trend */}
      {monthlyData.length > 0 && (
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm mb-8">
          <h3 className="text-lg font-medium text-card-foreground mb-4">Net Balance Trend</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="month" 
                tickFormatter={formatMonth}
                angle={-45}
                textAnchor="end"
                height={80}
              />
              <YAxis />
              <Tooltip 
                formatter={(value, name) => [formatCurrency(value), name]}
                labelFormatter={formatMonth}
              />
              <Line 
                type="monotone" 
                dataKey="net" 
                stroke="#8884d8" 
                strokeWidth={2}
                name="Net Balance"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Transaction Categories Table */}
      {transactionCategories.length > 0 && (
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h3 className="text-lg font-medium text-card-foreground mb-4">Transaction Categories Breakdown</h3>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border">
                  <th className="text-left py-3 px-4 font-medium text-muted-foreground">Category</th>
                  <th className="text-right py-3 px-4 font-medium text-muted-foreground">Count</th>
                  <th className="text-right py-3 px-4 font-medium text-muted-foreground">Total Amount</th>
                  <th className="text-right py-3 px-4 font-medium text-muted-foreground">Average</th>
                </tr>
              </thead>
              <tbody>
                {transactionCategories.map((category, index) => (
                  <tr key={index} className="border-b border-border last:border-b-0">
                    <td className="py-3 px-4">
                      <div className="flex items-center">
                        <div 
                          className="w-3 h-3 rounded-full mr-2" 
                          style={{ backgroundColor: category.color }}
                        ></div>
                        {category.name}
                      </div>
                    </td>
                    <td className="py-3 px-4 text-right">{category.count}</td>
                    <td className="py-3 px-4 text-right font-medium">
                      {formatCurrency(category.amount)}
                    </td>
                    <td className="py-3 px-4 text-right text-muted-foreground">
                      {formatCurrency(category.amount / category.count)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Advanced Audit Trail Browser */}
      <div className="bg-card border border-border rounded-lg p-6 shadow-sm mt-8">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-lg font-medium text-card-foreground">Advanced Audit Trail Browser</h3>
          <div className="flex gap-2">
            <button
              onClick={exportAuditReport}
              className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 text-sm"
              disabled={auditLoading || auditTrails.length === 0}
            >
              Export Report
            </button>
            <button
              onClick={loadAuditTrails}
              className="px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80 text-sm"
              disabled={auditLoading}
            >
              {auditLoading ? 'Loading...' : 'Refresh'}
            </button>
          </div>
        </div>

        {/* Audit Trail Filters */}
        <div className="mb-6 p-4 bg-muted rounded-lg">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-muted-foreground mb-1">Action Type</label>
              <select
                value={auditFilters.action}
                onChange={(e) => setAuditFilters(prev => ({ ...prev, action: e.target.value }))}
                className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              >
                <option value="">All Actions</option>
                <option value="TRANSACTION_CREATED">Transaction Created</option>
                <option value="STATUS_UPDATED">Status Updated</option>
                <option value="AMOUNT_CHANGED">Amount Changed</option>
                <option value="DESCRIPTION_UPDATED">Description Updated</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-muted-foreground mb-1">Start Date</label>
              <input
                type="date"
                value={auditFilters.startDate}
                onChange={(e) => setAuditFilters(prev => ({ ...prev, startDate: e.target.value }))}
                className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-muted-foreground mb-1">End Date</label>
              <input
                type="date"
                value={auditFilters.endDate}
                onChange={(e) => setAuditFilters(prev => ({ ...prev, endDate: e.target.value }))}
                className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-muted-foreground mb-1">Search</label>
              <input
                type="text"
                value={auditFilters.searchTerm}
                onChange={(e) => setAuditFilters(prev => ({ ...prev, searchTerm: e.target.value }))}
                placeholder="Search in audit data..."
                className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
              />
            </div>
          </div>
          <div className="mt-4 flex justify-between items-center">
            <button
              onClick={() => setAuditFilters({ action: '', startDate: '', endDate: '', searchTerm: '' })}
              className="px-4 py-2 bg-muted text-muted-foreground rounded-md hover:bg-muted/80 text-sm"
            >
              Clear Filters
            </button>
            <span className="text-sm text-muted-foreground">
              {auditTrails.length} audit entries found
            </span>
          </div>
        </div>

        {/* Audit Trail Timeline */}
        {auditLoading ? (
          <div className="text-center py-8">
            <div className="text-muted-foreground">Loading audit trails...</div>
          </div>
        ) : auditTrails.length > 0 ? (
          <div className="space-y-4 max-h-96 overflow-y-auto">
            {auditTrails.map((trail, index) => (
              <div key={index} className="border border-border rounded-lg p-4 hover:bg-muted/50 transition-colors">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-medium text-foreground">{trail.action}</span>
                      <span className="text-xs bg-primary/10 text-primary px-2 py-1 rounded">
                        Transaction #{trail.transactionId}
                      </span>
                    </div>
                    <div className="text-sm text-muted-foreground">
                      {formatDate(trail.createdAt)}
                    </div>
                  </div>
                  <div className="text-right text-sm">
                    <div className="text-muted-foreground">
                      {formatCurrency(trail.transactionAmount)} • {trail.transactionType}
                    </div>
                  </div>
                </div>
                
                {/* Old vs New Values */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-3">
                  {trail.oldValues && (
                    <div>
                      <div className="text-xs font-medium text-muted-foreground mb-1">Previous Values:</div>
                      <div className="text-xs bg-red-50 text-red-700 p-2 rounded font-mono">
                        {typeof trail.oldValues === 'string' ? trail.oldValues : JSON.stringify(trail.oldValues)}
                      </div>
                    </div>
                  )}
                  {trail.newValues && (
                    <div>
                      <div className="text-xs font-medium text-muted-foreground mb-1">Updated Values:</div>
                      <div className="text-xs bg-green-50 text-green-700 p-2 rounded font-mono">
                        {typeof trail.newValues === 'string' ? trail.newValues : JSON.stringify(trail.newValues)}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-8">
            <div className="text-muted-foreground">No audit trails found matching your filters</div>
          </div>
        )}
      </div>

      {/* Refresh Button */}
      <div className="mt-8 text-center">
        <button
          onClick={loadFinancialData}
          className="px-6 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-ring"
        >
          Refresh Data
        </button>
      </div>
    </div>
  );
};

export default Ledger;