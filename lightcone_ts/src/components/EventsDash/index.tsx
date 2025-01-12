import { useEffect, useState, useContext } from "react";

import EventsList from "@/components/EventsList";
import EventForm from "@/components/EventForm";
import Timeline from "@/components/Timeline";
import { ScrollArea } from "@/components/ui/scroll-area"
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/resizable"

const EventsDash = ({events, selectedEvent, people, setSelectedEventUID}) =>{
  // const [selectedEvent, setSelectedEvent] = useState(null);

  // const selectEvent = (uid:number) => {
    // const event = events.find((e:any) => e.id === uid);
    // setSelectedEvent(event);
  // }

  const clearSelectedEvent = () => {
    setSelectedEventUID(null);
  }

  return (
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
                <Timeline events={events} setSelectedEventUID={setSelectedEventUID} />
              </div>
            </div>

            {/* Bottom Section */}
            <div className="flex-1 flex">
              {/* Events List */}
              <div className="w-1/4 border-r">
                <div className="p-4 font-semibold border-b">Events List</div>
                <EventsList events={events} onSelect={setSelectedEventUID} clearSelection={clearSelectedEvent}/>
              </div>

              {/* Form Section */}
              <div className="flex-1 p-4">
                <div className="bg-card h-full rounded p-4">
                  <EventForm initialData={selectedEvent}
                              availableParticipants={people}
                              onSubmit={(data:any)=>{console.log("SUBMIT OCCURRED -->", data)}}/>
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
                </div>
              </div>
            </ScrollArea>
          </div>
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
  )
}

export default EventsDash;
