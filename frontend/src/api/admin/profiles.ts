import apiClient from "../../utils/axios";
import { PageResponse, Sort } from "./Interfaces";
import { UserAdminResponse } from "./users";

export type ClusterProfileResponse = {
    clusterId: number;
    user: UserAdminResponse;
    bindedCluster: string;
    profileId: number;
    maxSubmit: number;
    maxJobs: number;
    maxTres: string[];
    softLimit: number;
    hardLimit: number;
    maxTaskLiveTime: number;
    createdAt: number;
}

export interface ProfilePageResponse extends PageResponse<ClusterProfileResponse> {}

export type ClusterProfileFilter = {
    groupId?: number;
    clusterId?: number;
    bindedCluster?: string;
    username?: string;
    page?: number;
    size?: number;
    sort?: string;
}

export type ClusterProfileAdminRequest = {
    userId: number;
    maxSubmit: number | null;
    maxTasks: number | null;
    maxTres: string[] | null;
    softLimit: number | null;
    hardLimit: number | null;
    maxTaskLiveTime: number | null;
}

export const getProfiles = async (page: number, size: number, sort: Sort | null, filters?: ClusterProfileFilter) => {
    const resp = await apiClient.get<ProfilePageResponse>(
        '/api/admin/cluster/profile',
        {
            params: {
                page: page,
                size: size,
                sort: sort? `${sort.sortField},${sort.sortDirection}`: undefined,
                ...filters
            }
        }
    )
    return resp.data;
};

export const createProfile = async (clusterId: number, bindedCluster: string, requests: ClusterProfileAdminRequest[]) => {
    return await apiClient.post(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/profile`, requests);
}

export const updateProfile = async (clusterId: number, bindedCluster: string, requests: ClusterProfileAdminRequest[]) => {
    return await apiClient.put(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/profile`, requests);
}

export const deleteProfile = async (clusterId: number, bindedCluster: string, userId: number) => {
    return await apiClient.delete(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/profile/${userId}`);
}

export const deleteProfiles = async (clusterId: number, bindedCluster: string, userIds: number[]) => {
    return await apiClient.delete(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/profile`, {
        data: userIds
    });
}

export const downloadProfile = async (clusterId: number, bindedCluster: string, userId: number) => {
    const resp = await apiClient.get(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/download`, {
        params: {
            userIds: userId
        },
        responseType: 'blob'
    });
    const contentDisposition = resp.headers['content-disposition'];
    let fileName = `profile_${clusterId}_${bindedCluster}_${userId}`;
    if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
        if (fileNameMatch?.[1]) {
            fileName = fileNameMatch[1];
        }
    }
    const url = window.URL.createObjectURL(new Blob([resp.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
}

export const downloadMultipleProfiles = async (clusterId: number, bindedCluster: string, userIds: number[]) => {
    const resp = await apiClient.get(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/download`, {
        params: {
            userIds
        },
        paramsSerializer: {
            indexes: null
        },
        responseType: 'blob'
    });
    const contentDisposition = resp.headers['content-disposition'];
    let fileName = `profiles_${clusterId}_${bindedCluster}`;
    if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
        if (fileNameMatch?.[1]) {
            fileName = fileNameMatch[1];
        }
    }
    const url = window.URL.createObjectURL(new Blob([resp.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
}

export const revokeAccessFromGroup = async (clusterId: number, groupId: number, bindedCluster: string) => {
    return await apiClient.delete(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/groups/${groupId}`);
}
