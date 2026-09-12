import apiClient from "../../utils/axios";
import { GroupAdminResponse } from "./groups";
import { PageResponse, Sort } from "./Interfaces";

export type UserAdminResponse = {
    id: number;
    username: string;
    password: string;
    group: GroupAdminResponse | null;
    createdAt: number;
}

export type UserAdminRequest = {
    username: string;
    password: string;
    groupId?: number | null;
}

export interface UserPageResponse extends PageResponse<UserAdminResponse> {}

export type UserFilter = {
    username?: string;
    clusterId?: number;
    bindedCluster?: string;
    groupId?: number;
    notInCluster?: boolean;
    ungrouped?: boolean;
}

export const getUsers = async (page: number, size: number, sort: Sort | null, filters?: UserFilter) => {
    const resp = await apiClient.get<UserPageResponse>('/api/user', {
        params: {
            page: page,
            size: size,
            sort: sort? `${sort.sortField},${sort.sortDirection}`: undefined,
            ...filters
        }
    });
    return resp.data;
}

export const updateUser = async (userId: number, request: UserAdminRequest) => {
    return await apiClient.patch(`/api/user/${userId}`, request);
}

export const importUsersFromJson = async (formData: FormData) => {
    const resp = await apiClient.post('/api/user/import', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
        .then(() => alert('Импорт завершён'))
        .catch(() => alert('Ошибка при импорте'));

}

export const deleteUser = async (userId: number) => {
    return await apiClient.delete(`/api/user/${userId}`);
}


export const exportUsersToJson = async () => {
    const resp = await apiClient.get('/api/user/export', {
        responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([resp.data]));
    const link = document.createElement('a');
    const disposition = resp.headers['content-disposition'];
    const match = disposition?.match(/filename="?([^"]+)"?/);
    const fileName = match ? match[1] : 'users.json';
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();

    window.URL.revokeObjectURL(url);
    document.body.removeChild(link);
}

export const createUser = async (request: UserAdminRequest) => {
    return await apiClient.post('/api/user', request);
}