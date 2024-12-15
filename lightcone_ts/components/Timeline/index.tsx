import React,{useEffect, useState} from 'react';
import VisTimeline from '@/components/VisTimeline';

const Timeline = (props) => {
  const [eventItems, setEventItems] = useState([]);
  const { events } = props;


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
    height: '500px',
    editable: true
  };

  console.log("EVENT ITEMS",eventItems)

  return (<div>
            {eventItems.length > 0 ? <VisTimeline items={ eventItems } groups={ groups } options={ options } /> : null}
          </div>);
};

export default Timeline;
