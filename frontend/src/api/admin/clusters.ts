import apiClient from "../../utils/axios";
import { PageResponse, SlurmTres, Sort } from "../../utils/Interfaces";


export type ClusterAdminResponse = {
    id: number;
    hostname: string;
    username: string;
    displayedName: string;
    daemonPort: number;
    sshPort: number;
    taskScheduler: string;
}

export type SlurmClusterRec = {
    name: string;
    nodes: string;
    tres: SlurmTres[];
}

export type ClusterAdminRequest = {
    host: string;
    username: string;
    password?: string;
    displayedName: string;
    daemonPort?: number;
    sshPort?: number;
    schema: string;
    taskScheduler: string;
}

export type ClusterFilter = {
    hostname?: string;
    displayedName?: string;
    taskScheduler?: string;
}

export interface ClusterPageResponse extends PageResponse<ClusterAdminResponse> {}

export const getClusters = async (page: number, size: number, sort: Sort | null, filters?: ClusterFilter) => {
    const resp = await apiClient.get<ClusterPageResponse>('/api/admin/cluster', {
        params: {
            page: page,
            size: size,
            sort: sort? `${sort.sortField},${sort.sortDirection}`: undefined,
            ...filters
        }
    });
    return resp.data;
}

export const addCluster = async (clusterData: ClusterAdminRequest) => {
    const resp = await apiClient.post<ClusterAdminResponse>('/api/admin/cluster', clusterData);
    return resp.data;
}

export const deleteCluster = async (clusterId: number) => {
    const resp = await apiClient.delete(`/api/admin/cluster/${clusterId}`);
    return resp.status;
}

export const getSupportedSchedulers = async () => {
    const resp = await apiClient.get<string[]>('/api/admin/cluster/versions');
    return resp.data;
}

export const getBindedClusters = async (clusterId: number) => {
    const resp = await apiClient.get<SlurmClusterRec[]>(`/api/admin/cluster/${clusterId}/bound`);
    return resp.data;
}

export const deleteGroupProfiles = async (clusterId: number, groupId: number, bindedCluster: string) => {
    return await apiClient.delete(`/api/admin/cluster/${clusterId}/bound/${bindedCluster}/groups/${groupId}`);
}
