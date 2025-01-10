import { createContext, useEffect, useState } from 'react';

export const AuthContext = createContext({ isAuthenticated: false, setIsAuthenticated:()=>{},login: () => {}, logout: () => {}});

export const AuthProvider = (props) => {
  const { children } = props;
  const [isAuthenticated, setIsAuthenticated] = useState(props.isAuthenticated);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);

  const login = (token) => {
    localStorage.setItem('token', token);
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, setIsAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
