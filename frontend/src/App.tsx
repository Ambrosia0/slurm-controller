import Login from './pages/Login';
import { useAuth } from './utils/AuthContext';
import './i18n'
import { lazy, Suspense } from 'react';

const AdminHome = lazy(() => import("./pages/AdminHome"));
const UserHome = lazy(() => import("./pages/UserHome"));

function App() {
    const { role, isAuthenticated, logout, login } = useAuth();
    if (!isAuthenticated)
        return <Login loginMethod={login}/>
    return(
        <Suspense fallback={<div>Loading...</div>}>
            {role && role === "ROLE_ADMIN"?
                <AdminHome logout={logout}/>:
                <UserHome logout={logout}/>
            }
        </Suspense>
    )
}

export default App;
