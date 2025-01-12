import axios from 'axios';

const authAxios = () => axios.create({
  baseURL: import.meta.env.VITE_PUBLIC_LIGHTCONE_SERVER_URL,
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`,
  },
});

////////////////////////////////////////////////////////////////////// EVENTS //

export const getEvents = async () => {
  const res = await authAxios().get('/api/events');
  return res.data;
}

export const createEvent = async (event: any) => {
  const res = await authAxios().post('/api/event', {event});
  return res.data;
}

export const updateEvent = async (event: any) => {
  const res = await authAxios().put(`/api/events/${event.id}`, event);
  return res.data;
}

export const deleteEvent = async (id: number) => {
  const res = await authAxios().delete(`/api/events/${id}`);
  return res.data;
}

export const setEventTitle =async (id: number, title: string) => {
  const res = await authAxios().put(`/api/events/${id}/title`, { title });
  return res.data;
}

export const setEventNote = async (id: number, note: string) => {
  const res = await authAxios().put(`/api/events/${id}/note`, { note });
  return res.data;
}

export const setEventParticipants = async (id: number, participants: number[]) => {
  const res = await authAxios().put(`/api/events/${id}/participants`, { participants });
  return res.data;
}

/////////////////////////////////////////////////////////////////////// PEOPLE //

export const getPeople = async () => {
  const res = await authAxios().get('/api/persons')
  return res.data.persons
};

export const createPerson = async (person: any) => {
  const res = await authAxios().post('/api/person', person);
  const newPerson = {id: res.data.person.uid,
                     name: res.data.person.name}
  return newPerson;
};

export const updatePerson = async (person: any) => {
  const res = await authAxios().put(`/api/person/${person.id}`, person);
  return res.data;
};

export const destroyPerson = async (id: number) => {
  const res = await authAxios().delete(`/api/person/${id}`);
  return res.data;
};
// // Example usage
// authAxios.get('/api/events')
//   .then(response => {
//     // Handle the response
//   })
//   .catch(error => {
//     // Handle the error
//   });
