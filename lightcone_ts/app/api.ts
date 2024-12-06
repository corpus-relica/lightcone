import axios from 'axios';

const authAxios = () => axios.create({
  baseURL: 'http://localhost:3002',
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
