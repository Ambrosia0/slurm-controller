import apiClient from "../../utils/axios";
import { Profile, SlurmJob, SlurmJobInfo, SlurmTres } from "../../utils/Interfaces";
import { SlurmClusterRec } from "../admin/clusters";

export type ClusterUserResponse = {
    id: number;
    hostname: string;
    displayedName: string;
}

export type BindedClusterResponse = {
    name: string;
    tres: SlurmTres[];
    nodes: string;
}

export type TaskFilter = {
    taskStatus?: string[] | null;
    startTime?: number | null;
    endTime?: number | null;
    clusterNames: string[] | null;
}

export type TaskPollFilter = {
    status?: string;
}

export type JobRequest = {
    args?: string[];
    script?: string;
    directory?: string;
    jobName?: string;
    batchFeatures?: string;
    flags?: string[];
    deadLine?: number;
    beginTime?: number;
    endTime?: number;
    cpusPerTres?: string;
    memPerTres?: string;
    tresPerJob?: string;
    tresPerTask?: string;
    tresPerNode?: string;
    cpusPerTask?: number;
    minimumCpus?: number;
    maximumCpus?: number;
    nodes?: string;     // String Node count range specification (e.g. 1-15:4)
    maxNodes?: number;
    minNodes?: number;
    numberOfTasks?: number;
    maxTaskLiveTime?: number;
    standardError?: string;
    standartInput?: string;
    standardOutput?: string;
}

export type TaskRequest = {
    script?: string;
    job?: JobRequest;
    jobs?: JobRequest[];
}

export const getUserInfo = async () => {
    const resp = await apiClient.get<string[]>('/api/user/info');
    return resp.data;
}

export const getAvailableClusters = async () => {
    const resp = await apiClient.get<ClusterUserResponse[]>('/api/cluster');
    return resp.data;
}

export const getAvailableBindedClusters = async (clusterId: number) => {
    const resp = await apiClient.get<SlurmClusterRec[]>(`/api/cluster/${clusterId}/bound`);
    return resp.data;
}

export const getTres = async (clusterId: number) => {
    const resp = await apiClient.get<SlurmTres[]>(`/api/cluster/${clusterId}/tres`);
    return resp.data;
}

export const getUserTasks = async (clusterId: number, taskFilter?: TaskFilter) => {
    const resp = await apiClient.get<SlurmJob[]>(`/api/cluster/${clusterId}/task`, {
        params: taskFilter
    });
    return resp.data;
}

export const pollTasks = async (clusterId: number, clusterName: string, taskPollFilter?: TaskPollFilter) => {
    const resp = await apiClient.get<SlurmJobInfo[]>(`/api/cluster/${clusterId}/bound/${clusterName}/task`, {
        params: taskPollFilter
    });
    return resp.data;
}

export const createTask = async (clusterId: number, clusterName: string, request: TaskRequest) => {
    return await apiClient.post(`/api/cluster/${clusterId}/bound/${clusterName}/task`, request);
}

export const cancelTask = async (clusterId: number, clusterName: string, taskId: number) => {
    return await apiClient.delete(`/api/cluster/${clusterId}/bound/${clusterName}/task/${taskId}`);
}

export const getUserProfile = async (clusterId: number, clusterName: string) => {
    const resp = await apiClient.get<Profile>(`/api/cluster/${clusterId}/bound/${clusterName}/profile`);
    return resp.data;
}

export const downloadUserProfile = async (clusterId: number, clusterName: string) => {
    const resp = await apiClient.get(`/api/cluster/${clusterId}/bound/${clusterName}/profile/download`, {
        responseType: 'blob'
    });
    const contentDisposition = resp.headers['content-disposition'];
    let fileName = `profile_${clusterId}_${clusterName}`;
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
