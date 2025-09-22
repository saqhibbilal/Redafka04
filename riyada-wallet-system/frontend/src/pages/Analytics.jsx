import React from 'react';

const Analytics = () => {
  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">Analytics</h1>
        <p className="mt-2 text-muted-foreground">
          Transaction analytics and spending insights
        </p>
      </div>

      {/* Placeholder Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Spending Patterns</h2>
          <p className="text-muted-foreground">
            Visual charts showing spending patterns over time will be displayed here.
          </p>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Transaction Volume</h2>
          <p className="text-muted-foreground">
            Real-time transaction volume metrics will be shown here.
          </p>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">User Activity</h2>
          <p className="text-muted-foreground">
            User activity tracking and analytics will be available here.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Monthly Summary</h2>
          <p className="text-muted-foreground">
            Monthly spending and transaction summaries will be generated here.
          </p>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Export Reports</h2>
          <p className="text-muted-foreground">
            Export functionality for analytics reports will be available here.
          </p>
        </div>
      </div>

      {/* Coming Soon Banner */}
      <div className="mt-8 bg-primary/10 border border-primary/20 rounded-lg p-6 text-center">
        <h3 className="text-lg font-medium text-primary mb-2">Coming in Phase 5</h3>
        <p className="text-primary/80">
          Complete analytics dashboard with InfluxDB integration, real-time charts, and reporting
        </p>
      </div>
    </div>
  );
};

export default Analytics;
