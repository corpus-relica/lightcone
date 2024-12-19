"use client";

import { useEffect, useState, useContext } from "react";
import { ProtectedRoute } from '../ProtectedRoute';
import { Button } from "@/components/ui/button";
import { AuthContext } from '../AuthContext';
import EventsList from "@/components/EventsList";
import EventForm from "@/components/EventForm";
import Timeline from "@/components/Timeline";
import { getEvents } from "../api";

import { ScrollArea } from "@/components/ui/scroll-area"
import { Separator } from "@/components/ui/separator"
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/resizable"

const Dashboard = () => {

  const [selectedEvent, setSelectedEvent] = useState(null); // [event, setEvent
  const [foobarbaz, setFoobarbaz] = useState(null); // [event, setEvent
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


  useEffect(() => {
    const uid = selectedEvent && selectedEvent[0];
    const event = uid && events.find((e:any) => e.uid === uid);

    console.log("Selected Event: ", event);
    setFoobarbaz(event);
  }, [selectedEvent]);

  return (
    <ProtectedRoute>
      <div className="h-screen flex flex-col">
        {/* Header */}
        <header className="border-b h-14 flex items-center px-4 shrink-0">
          <h1 className="font-bold text-xl">Event Management</h1>
        </header>

        {/* Main Content */}
        <ResizablePanelGroup direction="horizontal" className="flex-1">
          {/* Main Content Area */}
          <ResizablePanel defaultSize={80}>
            <div className="h-full flex flex-col">
              {/* Timeline Section */}
              <div className="h-1/3 border-b">  {/* removed p-4 */}
                <div className="h-full w-full">  {/* removed bg-muted, rounded, flex classes */}
                  <Timeline events={events} setSelectedEvent={setSelectedEvent} />
                </div>
              </div>

              {/* Bottom Section */}
              <div className="flex-1 flex">
                {/* Events List */}
                <div className="w-1/4 border-r">
                  <div className="p-4 font-semibold border-b">Events List</div>
                  <EventsList events={events}/>
                </div>

                {/* Form Section */}
                <div className="flex-1 p-4">
                  <div className="bg-card h-full rounded p-4">
                    <EventForm initialData={foobarbaz} onSubmit={(data:any)=>{console.log("SUBMIT OCCURRED -->", data)}}/>
                  </div>
                </div>
              </div>
            </div>
          </ResizablePanel>

          <ResizableHandle />

          {/* Chat Panel - Full Height */}
          <ResizablePanel defaultSize={20} minSize={15} maxSize={30}>
            <div className="h-full flex flex-col">
              <div className="p-4 font-semibold border-b">Chat Interface</div>
              <ScrollArea className="flex-1">
                <div className="p-4">
                  <div className="space-y-4">
                    <div className="bg-muted p-2 rounded">Chat messages will go here</div>

                    <Button type="submit" onClick={logout}>
                      logout
                    </Button>

                  </div>
                </div>
              </ScrollArea>
            </div>
          </ResizablePanel>
        </ResizablePanelGroup>
      </div>
    </ProtectedRoute>
  )
};

export default Dashboard;
