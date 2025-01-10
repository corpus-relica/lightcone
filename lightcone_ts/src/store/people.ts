// store/participants.ts
import { create } from 'zustand'
import {getPeople} from '../api.js'

type PeopleStore = {
  people: any[];
  loading: boolean;
  fetchPeople: () => Promise<void>;
}

export const usePeopleStore = create<PeopleStore>((set) => ({
  people: [],
  loading: true,
  fetchPeople: async () => {
    const response = await getPeople();
    set({ people: response, loading: false });
  }
}));
