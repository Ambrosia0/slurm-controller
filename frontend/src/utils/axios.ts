import axios from "axios";
import { apiUrl } from "./config";

const apiClient = axios.create({
    baseURL: `${apiUrl}`,
    withCredentials: true
});

export const setInterceptors = (logout: () => void) =>{
    apiClient.interceptors.response.use(
        resp => resp,
        error =>{
            if(error.response?.status === 401){
                logout();
            }
            return Promise.reject(error);
        }
    );
}

export default apiClient;