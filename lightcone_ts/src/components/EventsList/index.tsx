import React from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

import { Button } from "@/components/ui/button";

import { ScrollArea } from "@/components/ui/scroll-area"

const EventsList = (props) => {
  const { events, onSelect, clearSelection } = props;

  const renderEvents = () => {
    return events.map((event) => {
      return (
        <div key={event.uid} onClick={()=>{console.log('SELECT FOO', event); onSelect(event.uid)}}>
          <h3>{event.title}</h3>
          <p>{event.time}</p>
        </div>
      );
    });
  };

  return (
    <ScrollArea className="h-[calc(100%-57px)]">
      <div className="p-4 space-y-2">
        <Button type="submit" className="w-full" onClick={()=>{clearSelection()}}>
          Add Event
        </Button>
        {renderEvents()}
      </div>
    </ScrollArea>
  );
};

export default EventsList;

// {
//   "uid": 1732651912,
//   "title": "this is another test monkey face ",
//   "event-type": "event",
//   "time": "2024-11-28T07:59:05.799Z",
//   "participants": [
//     1000000084,
//     1000000074,
//     1000000067,
//     1000000081
//   ],
//   "note": "suckit for real fdfdas industry"
// }
