import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useWallet } from '../contexts/WalletContext';

const Transfer = () => {
  const navigate = useNavigate();
  const { balance, transfer, loading } = useWallet();
  const [formData, setFormData] = useState({
    recipient: '',
    amount: '',
    description: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.recipient || !formData.amount) {
      setError('Please fill in all required fields');
      return;
    }

    const amount = parseFloat(formData.amount);
    if (amount <= 0) {
      setError('Amount must be greater than 0');
      return;
    }

    if (amount > balance) {
      setError('Insufficient balance');
      return;
    }

    const result = await transfer(formData.recipient, amount, formData.description);
    
    if (result.success) {
      setSuccess('Transfer successful!');
      setFormData({ recipient: '', amount: '', description: '' });
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);
    } else {
      setError(result.error);
    }
  };

  const totalAmount = parseFloat(formData.amount) || 0;

  return (
    <div className="max-w-2xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">Send Money</h1>
        <p className="mt-2 text-muted-foreground">Transfer money to another wallet</p>
      </div>

      {/* Current Balance */}
      <div className="mb-6">
        <div className="bg-card border border-border rounded-lg p-4">
          <div className="flex justify-between items-center">
            <span className="text-sm text-muted-foreground">Available Balance:</span>
            <span className="text-lg font-semibold text-card-foreground">
              ${balance.toFixed(2)}
            </span>
          </div>
        </div>
      </div>

      {/* Transfer Form */}
      <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <div className="bg-destructive/10 border border-destructive/20 text-destructive px-4 py-3 rounded-md text-sm">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-100 border border-green-200 text-green-800 px-4 py-3 rounded-md text-sm">
              {success}
            </div>
          )}

          <div>
            <label htmlFor="recipient" className="block text-sm font-medium text-card-foreground">
              Recipient Email *
            </label>
            <input
              id="recipient"
              name="recipient"
              type="email"
              required
              value={formData.recipient}
              onChange={handleChange}
              className="mt-1 block w-full px-3 py-2 border border-input rounded-md shadow-sm bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent"
              placeholder="Enter recipient's email"
            />
          </div>

          <div>
            <label htmlFor="amount" className="block text-sm font-medium text-card-foreground">
              Amount *
            </label>
            <div className="mt-1 relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span className="text-muted-foreground sm:text-sm">$</span>
              </div>
              <input
                id="amount"
                name="amount"
                type="number"
                step="0.01"
                min="0.01"
                max={balance}
                required
                value={formData.amount}
                onChange={handleChange}
                className="block w-full pl-7 pr-3 py-2 border border-input rounded-md shadow-sm bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent"
                placeholder="0.00"
              />
            </div>
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium text-card-foreground">
              Description (Optional)
            </label>
            <textarea
              id="description"
              name="description"
              rows={3}
              value={formData.description}
              onChange={handleChange}
              className="mt-1 block w-full px-3 py-2 border border-input rounded-md shadow-sm bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent"
              placeholder="Add a note about this transfer"
            />
          </div>

          <div className="bg-muted p-4 rounded-md">
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Transfer Fee:</span>
              <span className="text-card-foreground">$0.00</span>
            </div>
            <div className="flex justify-between text-sm mt-1">
              <span className="text-muted-foreground">Total Amount:</span>
              <span className="font-medium text-card-foreground">
                ${totalAmount.toFixed(2)}
              </span>
            </div>
          </div>

          <div className="flex space-x-4">
            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="flex-1 bg-secondary text-secondary-foreground py-2 px-4 rounded-md hover:bg-secondary/90 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading || !formData.recipient || !formData.amount}
              className="flex-1 bg-primary text-primary-foreground py-2 px-4 rounded-md hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Sending...' : 'Send Money'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Transfer;
