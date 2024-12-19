import React, { useState,useEffect } from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { X, Users, Calendar } from "lucide-react";

const EventForm = ({ onSubmit, initialData = null }) => {
  const [title, setTitle] = useState(initialData?.title || '');
  const [date, setDate] = useState(initialData?.time || '');
  const [note, setNote] = useState(initialData?.note || '');
  const [participants, setParticipants] = useState(initialData?.participants || []);
  const [isParticipantMenuOpen, setIsParticipantMenuOpen] = useState(false);

  useEffect(() => {
    console.log("INITIAL DATA -->");
    console.log(initialData);

    if (initialData) {
      setTitle(initialData.title);
      setDate(initialData.time);
      setNote(initialData.note);
      setParticipants(initialData.participants);
    }
  }, [initialData]);

  // Mock participant data - replace with your actual data
  const availableParticipants = [
    { id: 1000000084, name: "Alice Johnson" },
    { id: 1000000696, name: "Bob Smith" },
    { id: 1000000070, name: "Carol White" },
    { id: 1000000081, name: "David Brown" },
  ];

  const removeParticipant = (idToRemove) => {
    setParticipants(participants.filter(id => id !== idToRemove));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = {
      title,
      time: date,
      note,
      participants,
      event_type: "event"
    };
    onSubmit(formData);
  };

  return (
    <div className="bg-card rounded-lg shadow-sm p-6">
      <form onSubmit={handleSubmit} className="space-y-8">
        <div className="space-y-4">
          <h2 className="text-lg font-semibold">
            {initialData ? 'Edit Event' : 'Create New Event'}
          </h2>
          <Separator />
        </div>

        <div className="space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Event Title
            </label>
            <Input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Enter event title"
              className="w-full"
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Date and Time
            </label>
            <div className="flex gap-2">
              <Input
                type="datetime-local"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="flex-1"
              />
              <Button variant="outline" size="icon" type="button">
                <Calendar className="h-4 w-4" />
              </Button>
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Participants
            </label>
            <Popover open={isParticipantMenuOpen} onOpenChange={setIsParticipantMenuOpen}>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className="w-full justify-between"
                  type="button"
                >
                  <span>Select participants</span>
                  <Users className="h-4 w-4" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-[200px] p-2">
                <div className="space-y-1">
                  {availableParticipants
                    .filter(p => !participants.includes(p.id))
                    .map(participant => (
                      <Button
                        key={participant.id}
                        variant="ghost"
                        className="w-full justify-start text-sm"
                        onClick={() => {
                          setParticipants([...participants, participant.id]);
                          setIsParticipantMenuOpen(false);
                        }}
                      >
                        {participant.name}
                      </Button>
                    ))}
                </div>
              </PopoverContent>
            </Popover>
            <div className="flex flex-wrap gap-2 mt-3">
              {participants.map(id => {
                const participant = availableParticipants.find(p => p.id === id);
                return (
                  <Badge
                    key={id}
                    variant="secondary"
                    className="flex items-center gap-1 px-3 py-1"
                  >
                    {participant?.name}
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-4 w-4 p-0 hover:bg-transparent"
                      onClick={() => removeParticipant(id)}
                      type="button"
                    >
                      <X className="h-3 w-3" />
                    </Button>
                  </Badge>
                );
              })}
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Notes
            </label>
            <Textarea
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="Add event notes..."
              className="min-h-[120px] resize-none"
            />
          </div>
        </div>

        <div className="pt-4">
          <Button type="submit" className="w-full">
            {initialData ? 'Update Event' : 'Create Event'}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default EventForm;
