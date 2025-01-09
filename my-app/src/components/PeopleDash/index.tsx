import React, {useState} from 'react';


import PeopleList from "@/components/PeopleList";
import PersonForm from "@/components/PersonForm";
import { ScrollArea } from "@/components/ui/scroll-area"
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/resizable"

const PeopleDash = ({ people }) => {
  const [selectedPerson, setSelectedPerson] = useState(null);

  const onSubmit = (data:any) => {
    console.log("submit", data);
  }

  const selectPerson = (uid:number) => {
    const person = people.find((p:any) => p.id === uid);
    setSelectedPerson(person);
  }

  const clearSelectedPerson = () => {
    setSelectedPerson(null);
  }

  return (
    <div className="h-screen flex flex-col">
      {/* Header */}
      <header className="border-b h-14 flex items-center px-4 shrink-0">
        <h1 className="font-bold text-xl">People Management</h1>
      </header>

      {/* Main content */}
      <div className="flex-1 flex">
        {/* Sidebar */}
        <aside className="border-r w-64 p-4 shrink-0">
          <h2 className="font-bold text-lg mb-4">People</h2>
          <ScrollArea className="h-[calc(100%-57px)]">
            <PeopleList people={people} onSelect={selectPerson} clearSelection={clearSelectedPerson}/>
          </ScrollArea>
        </aside>

        {/* Main content */}
        <main className="flex-1 p-4">
          <ResizablePanelGroup direction='vertical'>
            <ResizablePanel>
              <PersonForm onSubmit={onSubmit} initialData={selectedPerson}/>
            </ResizablePanel>
          </ResizablePanelGroup>
        </main>
      </div>
    </div>
  );
}

export default PeopleDash;
