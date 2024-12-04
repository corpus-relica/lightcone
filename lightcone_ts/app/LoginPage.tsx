import { useContext } from 'react';
import { AuthContext } from './AuthContext';

export const LoginPage = () => {
  const { login } = useContext(AuthContext);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Get username and password from form
    // Call the login function from AuthContext
    login();
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Login form fields */}
      <button type="submit">Login</button>
    </form>
  );
};
