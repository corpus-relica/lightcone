import axios from 'axios';

console.log("MUTHER FUCKING TOKEN",localStorage.getItem('token'));

const authAxios = () => axios.create({
  baseURL: 'http://localhost:3003',
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
