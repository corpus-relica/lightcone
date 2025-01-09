import React, {useEffect, useState} from "react";

import { BrowserRouter, Routes, Route } from 'react-router'
import LandingPage from './pages/Landing'
import LoginPage from './pages/Login'
import DashboardPage from './pages/Dashboard'
import './App.css'

import { AuthProvider } from './AuthContext';


const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  return (
    <BrowserRouter>
      <AuthProvider isAuthenticated={isAuthenticated}>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App;
