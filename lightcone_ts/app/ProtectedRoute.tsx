"use client"

import { useContext, useEffect } from 'react';
import { AuthContext } from './AuthContext';
import { useRouter } from 'next/navigation';

export const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useContext(AuthContext);
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated]);

  // if (!isAuthenticated) {
  //   router.push('/login');
  //   return null;
  // }

  return children;
};
