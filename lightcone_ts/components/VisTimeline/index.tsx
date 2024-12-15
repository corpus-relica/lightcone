import React, { useEffect, useRef } from 'react';
import { Timeline, DataSet } from 'vis-timeline/standalone';
import 'vis-timeline/styles/vis-timeline-graph2d.css';

const VisTimeline = ({ items: initialItems, groups: initialGroups, options = {} }) => {
  const containerRef = useRef(null);
  const timelineRef = useRef(null);

  useEffect(() => {
    // Convert items to DataSet if they aren't already
    console.log("OCNVERTING ITEMS TO DATASET")
    console.log(initialItems);
    const items = initialItems instanceof DataSet
      ? initialItems
      : new DataSet(initialItems);
    console.log(items);

    // Convert groups to DataSet if they aren't already
    const groups = initialGroups instanceof DataSet
      ? initialGroups
      : new DataSet(initialGroups);

    // Default options that can be overridden by props
    const defaultOptions = {
      height: '400px',
      editable: true,
      zoomable: true,
      stack: true
    };

    // Create timeline
    if (containerRef.current) {
      timelineRef.current = new Timeline(
        containerRef.current,
        items,
        // groups,
        {
          ...defaultOptions,
          ...options
        }
      );

      // Example event listeners
      timelineRef.current.on('select', (properties) => {
        console.log('Selected items:', properties.items);
      });

      timelineRef.current.on('rangechanged', (properties) => {
        console.log('Range changed:', properties);
      });
    }

    // Cleanup
    return () => {
      if (timelineRef.current) {
        timelineRef.current.destroy();
      }
    };
  }, []); // Empty dependency array since we want this to run once

  return (
    <div ref={containerRef} className="w-full h-full" />
  );
};

export default VisTimeline;
