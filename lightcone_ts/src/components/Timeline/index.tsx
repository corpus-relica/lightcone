import React,{useEffect, useState} from 'react';
import VisTimeline from '@/components/VisTimeline';

const Timeline = (props) => {
  const { events, setSelectedEventUID} = props;
  const [eventItems, setEventItems] = useState([]);

  useEffect(() => {
    const ei = events.map((event) => {
      return {
        id: event.uid,
        content: event.title,
        start: event.time,
        type: 'point'
      };
    })
    setEventItems(ei)
  }, [events]);

  const groups = [
    { id: 1, content: 'Group 1' },
    { id: 2, content: 'Group 2' }
  ];

  const options = {
    width: '100%',    // Add this
    height: '100%',   // Change this from fixed 500px
    editable: true
  };

  return (
    <div className="h-full w-full">  {/* Add this wrapper */}
      {eventItems.length > 0 ? (
        <VisTimeline
          items={eventItems}
          groups={groups}
          options={options}
          setSelectedEventUID={setSelectedEventUID}
        />
      ) : null}
    </div>
  );
};

export default Timeline;
