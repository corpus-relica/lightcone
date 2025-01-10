// store/participants.ts
import { create } from 'zustand'
import {getEvents} from '../api.js'

type EventsStore = {
  events: any[];
  loading: boolean;
  fetchEvents: () => Promise<void>;
}

export const useEventsStore = create<EventsStore>((set) => ({
  events: [],
  loading: true,
  fetchEvents: async () => {
    const response = await getEvents();
    console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    console.log('Events:', response)
    set({ events: response, loading: false });
  }
}));
