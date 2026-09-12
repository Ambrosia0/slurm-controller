import { createContext, useCallback, useContext, useState, useEffect } from "react";
import apiClient, { setInterceptors } from "./axios";


type AuthContextType = {
    role: string | null;
    isAuthenticated: boolean;
    logout: () => Promise<void>;
    login: (username: string, password: string) => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({children}: {children: React.ReactNode}) => {
    const [role, setRole] = useState<string | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [token, setToken] = useState<string | null>();

    const renderLogin = useCallback(() => {
        setIsAuthenticated(false);
        setRole(null);
    },[]);

    const logout = useCallback(async () => {
        try {
            const resp = await apiClient.post('/api/logout');
        } catch {};
        renderLogin();
    }, [renderLogin]);

    const login = useCallback(async (username: string, password: string) =>{
        try {
            const resp = await apiClient.post<string>('/api/login', {username, password});
            if(resp.status === 200){
                setRole(resp.data);
                setIsAuthenticated(true);
            }
        } catch (error) {
            renderLogin();
            
        }
    },[renderLogin]);

    const getRole = useCallback(async () => {
        try {
            const resp = await apiClient.get('/api/info');
            setRole(resp.data.toString());
            setIsAuthenticated(true);
        } catch (error) {
            renderLogin();
        }
    }, [renderLogin])

    useEffect(() => {
        setInterceptors(renderLogin);
        getRole();
    }, [getRole, renderLogin]);

    // useEffect(() =>{
    //     console.log('Role updated:', role);
    //     console.log('IsAuth?', isAuthenticated );
    // },[role, isAuthenticated])

    return(
        <AuthContext.Provider value={{role, isAuthenticated, logout, login}}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if(!ctx) throw new Error("useAuth must be used inside AuthProvider");
    return ctx;
}