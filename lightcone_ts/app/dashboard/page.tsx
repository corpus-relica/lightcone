"use client";

import { useEffect, useState, useContext } from "react";

import { ProtectedRoute } from '../ProtectedRoute';
import { AuthContext } from '../AuthContext';
import { getEvents, getPeople } from "../api";
import EventsDash from "@/components/EventsDash";
import PeopleDash from "@/components/PeopleDash";

import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button";
const Dashboard = () => {

  const [selectedEvent, setSelectedEvent] = useState(null); // [event, setEvent
  const [foobarbaz, setFoobarbaz] = useState(null); // [event, setEvent
  const [events, setEvents] = useState([]);
  const [people, setPeople] = useState([]);
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

    getPeople().then((data) => {
      console.log(data.data);
      setPeople(data.data.persons);
    }).catch((error) => {
       if (error.response && error.response.status === 419) {
         // Token has expired, log the user out
         logout();
       } else {
         // Handle other errors
       }
    });
  }, []);


  useEffect(() => {
    const uid = selectedEvent && selectedEvent[0];
    const event = uid && events.find((e:any) => e.uid === uid);

    console.log("Selected Event: ", event);
    setFoobarbaz(event);
  }, [selectedEvent]);

  return (
    <ProtectedRoute>
      <Button type="submit" onClick={logout}>
        logout
      </Button>
      <Tabs defaultValue="events" className="w-full">
        <TabsList>
          <TabsTrigger value="events">Events</TabsTrigger>
          <TabsTrigger value="people">People</TabsTrigger>
        </TabsList>
        <TabsContent value="events">
          <EventsDash events={events}
                      foobarbaz={foobarbaz}
                      people={people}
                      setSelectedEvent={setSelectedEvent}/>
        </TabsContent>
        <TabsContent value="people" className="w-full">
          <PeopleDash people={people} />
        </TabsContent>
      </Tabs>
    </ProtectedRoute>
  )
};

export default Dashboard;
