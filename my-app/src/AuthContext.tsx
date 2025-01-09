import { createContext, useEffect, useState } from 'react';

export const AuthContext = createContext({ isAuthenticated: false, setIsAuthenticated:()=>{},login: () => {}, logout: () => {}});

export const AuthProvider = (props) => {
  console.log("AUTH PROVIDER");
  console.log(props.isAuthenticated);

  const { children } = props;

  const [isAuthenticated, setIsAuthenticated] = useState(props.isAuthenticated);

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
    <AuthContext.Provider value={{ isAuthenticated, setIsAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
