import axios from 'axios';

console.log("MUTHER FUCKING TOKEN",localStorage.getItem('token'));

const authAxios = () => axios.create({
  baseURL: process.env.NEXT_PUBLIC_LIGHTCONE_SERVER_URL,
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`,
  },
});

export const getEvents = () => authAxios().get('/api/events');

// // Example usage
// authAxios.get('/api/events')
//   .then(response => {
//     // Handle the response
//   })
//   .catch(error => {
//     // Handle the error
//   });
