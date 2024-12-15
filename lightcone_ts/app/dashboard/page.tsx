"use client";

import { useEffect, useState, useContext } from "react";
import { ProtectedRoute } from '../ProtectedRoute';
import { Button } from "@/components/ui/button";
import { AuthContext } from '../AuthContext';
import EventsList from "@/components/EventsList";
import Timeline from "@/components/Timeline";
import { getEvents } from "../api";

const Dashboard = () => {

  const [events, setEvents] = useState([]);
  const { logout} = useContext(AuthContext);

  useEffect(() => {
    getEvents().then((data) => {
      console.log(data.data);
      setEvents(data.data);
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
        <p>Welcome to the dashboard</p>
        <Timeline events={events}/>
        <EventsList events={events}/>
        <Button type="submit" onClick={logout}>
          logout
        </Button>
      </ProtectedRoute>
    </>
  );
};

export default Dashboard;
