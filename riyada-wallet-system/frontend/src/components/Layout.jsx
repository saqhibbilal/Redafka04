import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Layout = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  const handleLogout = () => {
    logout();
  };

  const toggleSidebar = () => {
    setSidebarCollapsed(!sidebarCollapsed);
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  const navigation = [
    { name: 'Dashboard', path: '/dashboard', icon: '🏠' },
    { name: 'Transfer', path: '/transfer', icon: '💸' },
    { name: 'History', path: '/history', icon: '📊' },
    { name: 'Ledger', path: '/ledger', icon: '📋' },
    { name: 'Analytics', path: '/analytics', icon: '📈' },
  ];

  return (
    <div className="min-h-screen bg-background">
      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 bg-card border-r border-border z-50 transition-all duration-300 ${
        sidebarCollapsed ? 'w-16' : 'w-64'
      }`}>
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="p-6 border-b border-border">
            <div className="flex items-center justify-between">
              {!sidebarCollapsed && (
                <div>
                  <h1 className="text-xl font-bold text-card-foreground">Riyada Wallet</h1>
                  <p className="text-sm text-muted-foreground">Digital Wallet System</p>
                </div>
              )}
              <button
                onClick={toggleSidebar}
                className="p-2 rounded-md hover:bg-muted transition-colors"
              >
                <span className="text-lg">{sidebarCollapsed ? '→' : '←'}</span>
              </button>
            </div>
          </div>

          {/* User Info */}
          <div className="p-4 border-b border-border">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-primary-foreground font-medium">
                  {user?.name?.charAt(0) || 'U'}
                </span>
              </div>
              {!sidebarCollapsed && (
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-card-foreground truncate">
                    {user?.name || 'User'}
                  </p>
                  <p className="text-xs text-muted-foreground truncate">
                    {user?.email || 'user@example.com'}
                  </p>
                </div>
              )}
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 p-4">
            <ul className="space-y-2">
              {navigation.map((item) => (
                <li key={item.name}>
                  <button
                    onClick={() => navigate(item.path)}
                    className={`w-full flex items-center px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      sidebarCollapsed ? 'justify-center' : 'space-x-3'
                    } ${
                      isActive(item.path)
                        ? 'bg-primary text-primary-foreground'
                        : 'text-muted-foreground hover:text-card-foreground hover:bg-muted'
                    }`}
                    title={sidebarCollapsed ? item.name : ''}
                  >
                    <span className="text-lg">{item.icon}</span>
                    {!sidebarCollapsed && <span>{item.name}</span>}
                  </button>
                </li>
              ))}
            </ul>
          </nav>

          {/* Logout */}
          <div className="p-4 border-t border-border">
            <button
              onClick={handleLogout}
              className={`w-full flex items-center px-3 py-2 rounded-md text-sm font-medium text-muted-foreground hover:text-card-foreground hover:bg-muted transition-colors ${
                sidebarCollapsed ? 'justify-center' : 'space-x-3'
              }`}
              title={sidebarCollapsed ? 'Logout' : ''}
            >
              <span className="text-lg">🚪</span>
              {!sidebarCollapsed && <span>Logout</span>}
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className={`transition-all duration-300 ${sidebarCollapsed ? 'pl-16' : 'pl-64'}`}>
        <div className="p-6">
          {children}
        </div>
      </div>
    </div>
  );
};

export default Layout;
