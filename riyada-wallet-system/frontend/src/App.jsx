import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { WalletProvider } from './contexts/WalletContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Transfer from './pages/Transfer';
import History from './pages/History';
import Ledger from './pages/Ledger';
import Analytics from './pages/Analytics';

function AppRoutes() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <Routes>
      <Route 
        path="/login" 
        element={isAuthenticated ? <Navigate to="/dashboard" /> : <Login />} 
      />
      <Route 
        path="/dashboard" 
        element={isAuthenticated ? (
          <Layout>
            <Dashboard />
          </Layout>
        ) : <Navigate to="/login" />} 
      />
      <Route 
        path="/transfer" 
        element={isAuthenticated ? (
          <Layout>
            <Transfer />
          </Layout>
        ) : <Navigate to="/login" />} 
      />
      <Route 
        path="/history" 
        element={isAuthenticated ? (
          <Layout>
            <History />
          </Layout>
        ) : <Navigate to="/login" />} 
      />
      <Route 
        path="/ledger" 
        element={isAuthenticated ? (
          <Layout>
            <Ledger />
          </Layout>
        ) : <Navigate to="/login" />} 
      />
      <Route 
        path="/analytics" 
        element={isAuthenticated ? (
          <Layout>
            <Analytics />
          </Layout>
        ) : <Navigate to="/login" />} 
      />
      <Route 
        path="/" 
        element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} 
      />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <WalletProvider>
        <Router>
          <div className="App">
            <AppRoutes />
          </div>
        </Router>
      </WalletProvider>
    </AuthProvider>
  );
}

export default App;