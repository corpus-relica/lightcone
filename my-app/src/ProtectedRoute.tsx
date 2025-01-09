import { useContext, useEffect } from 'react';
import { AuthContext } from './AuthContext';
// import { useRouter } from 'next/navigation';
import {Link, useNavigate } from "react-router"


export const ProtectedRoute = ({ children }) => {

  const { isAuthenticated } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate("/login")
    }
  }, [isAuthenticated]);


  return children;
};
