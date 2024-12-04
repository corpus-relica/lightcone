'use client';
import { createContext, useState } from 'react';

export const AuthContext = createContext({ isAuthenticated: false, login: () => {}, logout: () => {} });

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Function to update authentication state
  const login = () => {
    // Send login request to Clojure backend
    // If successful, update authentication state
    setIsAuthenticated(true);
  };

  const logout = () => {
    // Send logout request to Clojure backend
    // Update authentication state
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
