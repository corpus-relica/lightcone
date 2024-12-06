import React from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

const EventsList = (props) => {
  const { events } = props;

  const renderEvents = () => {
    return events.map((event) => {
      return (
        <div key={event.uid}>
          <h3>{event.title}</h3>
          <p>{event.time}</p>
        </div>
      );
    });
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-2xl">Events List</CardTitle>
        <CardDescription>
          A list of events
        </CardDescription>
      </CardHeader>
      <CardContent>
        {renderEvents()}
      </CardContent>
    </Card>
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
