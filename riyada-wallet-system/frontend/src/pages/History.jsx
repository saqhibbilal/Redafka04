import React from 'react';

const History = () => {
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

        {/* Transaction List */}
        <div className="px-4 py-6 sm:px-0">
          <div className="bg-card border border-border rounded-lg overflow-hidden shadow-sm">
            <div className="p-4 border-b border-border">
              <div className="flex justify-between items-center">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                    <span className="text-red-600 font-bold">-</span>
                  </div>
                  <div>
                    <div className="font-medium text-card-foreground">Payment to John Doe</div>
                    <div className="text-sm text-muted-foreground">john.doe@email.com</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-red-600 font-medium">-$50.00</div>
                  <div className="text-sm text-muted-foreground">Today, 2:30 PM</div>
                </div>
              </div>
              <div className="mt-2 text-sm text-muted-foreground">Lunch payment</div>
            </div>

            <div className="p-4 border-b border-border">
              <div className="flex justify-between items-center">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                    <span className="text-green-600 font-bold">+</span>
                  </div>
                  <div>
                    <div className="font-medium text-card-foreground">Payment from Jane Smith</div>
                    <div className="text-sm text-muted-foreground">jane.smith@email.com</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-green-600 font-medium">+$125.00</div>
                  <div className="text-sm text-muted-foreground">Yesterday, 5:15 PM</div>
                </div>
              </div>
              <div className="mt-2 text-sm text-muted-foreground">Shared dinner bill</div>
            </div>

            <div className="p-4 border-b border-border">
              <div className="flex justify-between items-center">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <span className="text-blue-600 font-bold">↗</span>
                  </div>
                  <div>
                    <div className="font-medium text-card-foreground">Wallet Top-up</div>
                    <div className="text-sm text-muted-foreground">Bank Transfer</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-green-600 font-medium">+$500.00</div>
                  <div className="text-sm text-muted-foreground">2 days ago</div>
                </div>
              </div>
              <div className="mt-2 text-sm text-muted-foreground">Monthly wallet funding</div>
            </div>

            <div className="p-4">
              <div className="flex justify-between items-center">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                    <span className="text-red-600 font-bold">-</span>
                  </div>
                  <div>
                    <div className="font-medium text-card-foreground">Payment to Coffee Shop</div>
                    <div className="text-sm text-muted-foreground">coffee@shop.com</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-red-600 font-medium">-$8.50</div>
                  <div className="text-sm text-muted-foreground">3 days ago</div>
                </div>
              </div>
              <div className="mt-2 text-sm text-muted-foreground">Morning coffee</div>
            </div>
          </div>
        </div>

        {/* Load More */}
        <div className="px-4 py-6 sm:px-0 text-center">
          <button className="bg-secondary text-secondary-foreground px-6 py-2 rounded-md hover:bg-secondary/90 transition-colors">
            Load More Transactions
          </button>
        </div>
      </div>
    </div>
  );
};

export default History;
