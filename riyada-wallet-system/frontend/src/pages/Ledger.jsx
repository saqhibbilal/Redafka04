import React from 'react';

const Ledger = () => {
  return (
    <div className="max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">Ledger</h1>
        <p className="mt-2 text-muted-foreground">
          Complete transaction ledger and audit trail
        </p>
      </div>

      {/* Placeholder Content */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Audit Trail</h2>
          <p className="text-muted-foreground">
            Complete audit trail for all transactions will be displayed here.
            This feature will be implemented in Phase 3 with the Ledger Service.
          </p>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Transaction Categories</h2>
          <p className="text-muted-foreground">
            Transaction categorization and tagging will be available here.
            This feature will be implemented in Phase 3.
          </p>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Financial Reports</h2>
          <p className="text-muted-foreground">
            Monthly and yearly financial summaries will be generated here.
            This feature will be implemented in Phase 3.
          </p>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 shadow-sm">
          <h2 className="text-lg font-medium text-card-foreground mb-4">Double-Entry Bookkeeping</h2>
          <p className="text-muted-foreground">
            Double-entry bookkeeping principles will be implemented here.
            This feature will be implemented in Phase 3.
          </p>
        </div>
      </div>

      {/* Coming Soon Banner */}
      <div className="mt-8 bg-primary/10 border border-primary/20 rounded-lg p-6 text-center">
        <h3 className="text-lg font-medium text-primary mb-2">Coming in Phase 3</h3>
        <p className="text-primary/80">
          Complete ledger functionality with audit trails, categorization, and financial reporting
        </p>
      </div>
    </div>
  );
};

export default Ledger;
