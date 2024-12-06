"use client";

import { useEffect, useState, useContext } from "react";
import { ProtectedRoute } from '../ProtectedRoute';
import { Button } from "@/components/ui/button";
import { AuthContext } from '../AuthContext';
import { getEvents } from "../api";

const Dashboard = () => {

  const { logout} = useContext(AuthContext);

  useEffect(() => {
    console.log("GET EVENTS?")
    getEvents().then((data) => {
      console.log('EVENTS DATA')
      console.log(data);
    }).catch((error) => {
       if (error.response && error.response.status === 419) {
         // Token has expired, log the user out
         logout();
       } else {
         // Handle other errors
       }
    });
  }, []);

  return (
    <>
    <ProtectedRoute>
      <h1>Dashboard</h1>
      {/* Dashboard content */}

        <Button type="submit" onClick={logout}>
          logout
        </Button>
    </ProtectedRoute>
    </>
  );
};

export default Dashboard;
