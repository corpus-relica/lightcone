'use client';
import { createContext, useEffect, useState } from 'react';


export const AuthContext = createContext({ isAuthenticated: false, login: () => {}, logout: () => {}});

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);

  const login = (token) => {
    console.log("LOGIN");
    // Send login request to Clojure backend
    // If successful, update authentication state
    localStorage.setItem('token', token);
    setIsAuthenticated(true);
  };

  const logout = () => {
    console.log("LOGOUT");
    // Send logout request to Clojure backend
    // Update authentication state
    localStorage.removeItem('token');
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
