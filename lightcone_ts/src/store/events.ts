// store/participants.ts
import { create } from 'zustand'
import {getEvents, createEvent} from '../api.js'

type EventsStore = {
  events: any[];
  loading: boolean;
  fetchEvents: () => Promise<void>;
  createEvent: (eventData: any) => Promise<void>;
  updateEvent: (eventData: any) => Promise<void>;
  destroyEvent: (id: number) => Promise<void>;
}

export const useEventsStore = create<EventsStore>((set, get) => ({
  events: [],
  loading: true,
  fetchEvents: async () => {
    const response = await getEvents();
    console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    console.log('Events:', response)
    set({ events: response, loading: false });
  },
  createEvent: async (eventData) => {
    console.log("LAST STOP BEFORE API", eventData)

    const newEvent = await createEvent(eventData);
    console.log("FIRST STOP AFTER API", newEvent)

    set({ events: [...get().events, newEvent] });
  },
  updateEvent: async (eventData) => {
    // const updatedEvent = await updateEvent(eventData);
    // set({ events: get().events.map((event) => event.id === updatedEvent.id ? updatedEvent : event) });
  },
  destroyEvent: async (id) => {
    // await destroyEvent(id);
    // set({ events: get().events.filter((event) => event.id !== id) });
  }
}));
