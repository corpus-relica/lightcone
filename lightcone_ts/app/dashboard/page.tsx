"use client";

import { useEffect, useState, useContext } from "react";
import { ProtectedRoute } from '../ProtectedRoute';
import { Button } from "@/components/ui/button";
import { AuthContext } from '../AuthContext';

const Dashboard = () => {

  const { logout} = useContext(AuthContext);

  return (
    <ProtectedRoute>
      <h1>Dashboard</h1>
      {/* Dashboard content */}

        <Button type="submit" onClick={logout}>
          logout
        </Button>
    </ProtectedRoute>
  );
};

export default Dashboard;
