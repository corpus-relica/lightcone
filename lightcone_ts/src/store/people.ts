// store/participants.ts
import { create } from 'zustand'
import {getPeople, createPerson, updatePerson, destroyPerson} from '../api.js'

type PeopleStore = {
  people: any[];
  loading: boolean;
  fetchPeople: () => Promise<void>;
  createPerson: (personData: any) => Promise<void>;
  updatePerson: (personData: any) => Promise<void>;
  destroyPerson: (id: number) => Promise<void>;
}

export const usePeopleStore = create<PeopleStore>((set, get) => ({
  people: [],
  loading: true,
  fetchPeople: async () => {
    const response = await getPeople();
    set({ people: response, loading: false });
  },
  createPerson: async (personData) => {
    const newPerson = await createPerson(personData);
    set({ people: [...get().people, newPerson] });
  },
  updatePerson: async (personData) => {
    const updatedPerson = await updatePerson(personData);
    set({ people: get().people.map((person) => person.id === updatedPerson.id ? updatedPerson : person) });
  },
  destroyPerson: async (id) => {
    await destroyPerson(id);
    set({ people: get().people.filter((person) => person.id !== id) });
  }
}));
