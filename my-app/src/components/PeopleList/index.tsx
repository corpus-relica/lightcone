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

const PeopleList = (props) => {
  const { people, onSelect, clearSelection } = props;

  const renderPeople = () => {
    return people.map((person) => {
      return (
        <div key={person.id} onClick={()=>{onSelect(person.id)}}>
          <h3>{person.id}</h3>
          <p>{person.name}</p>
        </div>
      );
    });
  };

  return (
    <ScrollArea className="h-[calc(100%-57px)]">
      <div className="p-4 space-y-2">
        <Button type="submit" className="w-full" onClick={()=>{clearSelection()}}>
          Add Person
        </Button>
        {renderPeople()}
      </div>
    </ScrollArea>
  );
};

export default PeopleList;
