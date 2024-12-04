import { ProtectedRoute } from '../ProtectedRoute';

const Dashboard = () => {
  return (
    <ProtectedRoute>
      <h1>Dashboard</h1>
      {/* Dashboard content */}
    </ProtectedRoute>
  );
};

export default Dashboard;
