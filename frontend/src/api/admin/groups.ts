import apiClient from "../../utils/axios";
import { PageResponse, Sort } from "./Interfaces";

export type GroupAdminResponse = {
    id: number;
    name: string;
    createdAt: number;
}

export type GroupAdminRequest = {
    name: string;
}

export type GroupUsersRequest = {
    userIds: number[];
}

export interface GroupPageResponse extends PageResponse<GroupAdminResponse> {}

export type GroupFilter = {
    name?: string;
    clusterId?: number;
    bindedCluster?: string;
    notInCluster?: boolean;
}


export const getGroups = async (page: number, size: number, sort: Sort | null, filters?: GroupFilter): Promise<GroupPageResponse> => {
    const resp = await apiClient.get<GroupPageResponse>('/api/group', {
        params: {
            page: page,
            size: size,
            sort: sort? `${sort.sortField},${sort.sortDirection}`: undefined,
            ...filters
        }
    });
    return resp.data;
};

export const exportGroupToJSON = async (group: GroupAdminResponse) => {
    const resp = await apiClient.get(`/api/group/${group.id}/export`, {
        responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([resp.data]));
    const link = document.createElement('a');
    const disposition = resp.headers['content-disposition'];
    const match = disposition?.match(/filename="?([^"]+)"?/);
    const fileName = match ? match[1] : `${group.name}.json`;
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();

    window.URL.revokeObjectURL(url);
    document.body.removeChild(link);
}

export const updateGroup = async (groupId: number, name: string) => {
    const resp = await apiClient.patch<GroupAdminResponse>(`/api/group/${groupId}`, {
        name
    });
    return resp.data;
}

export const deleteGroup = async (groupId: number, deleteBinded: boolean = false) => {
    return await apiClient.delete(`/api/group/${groupId}`, {
        params: {
            deleteBinded
        }
    });
}

export const createGroup = async (groupName: string) => {
    return await apiClient.post('/api/group', {
        name: groupName
    });
}

export const groupUsers = async (groupId: number, userIds: number[]) => {
    return await apiClient.post(`/api/group/${groupId}/users`, {
        userIds
    });
}

export const ungroupUsers = async (userIds: number[]) => {
    return await apiClient.delete('/api/group/users', {
        data: userIds
    });
}