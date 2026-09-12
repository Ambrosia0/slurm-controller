import AdminHome from './pages/AdminHome';
import UserHome from './pages/UserHome';
import Login from './pages/Login';
import { useAuth } from './utils/AuthContext';

function App() {
  const { role, isAuthenticated, logout, login } = useAuth();
  return isAuthenticated
    ? (role === 'ROLE_ADMIN' ? <AdminHome logout={logout} /> : <UserHome logout={logout} />)
    : <Login loginMethod={login}/>;
}

export default App;
