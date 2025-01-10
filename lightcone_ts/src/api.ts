import axios from 'axios';

const authAxios = () => axios.create({
  baseURL: import.meta.env.VITE_PUBLIC_LIGHTCONE_SERVER_URL,
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`,
  },
});

export const getEvents = async () => {
  const res = await authAxios().get('/api/events');
  return res.data;
}

export const getPeople = async () => {
  const res = await authAxios().get('/api/persons')
  return res.data.persons
};

// // Example usage
// authAxios.get('/api/events')
//   .then(response => {
//     // Handle the response
//   })
//   .catch(error => {
//     // Handle the error
//   });
