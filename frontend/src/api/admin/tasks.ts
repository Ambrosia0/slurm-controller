import apiClient from "../../utils/axios";
import { SlurmJob, SlurmJobInfo } from "../../utils/Interfaces";
import { TaskRequest } from "../user/userApi";

export type TaskFilter = {
    taskStatus?: string[] | null;
    startTime: number | null;
    endTime?: number | null;
    usernames?: string[] | null;
    groupId?: number | null;
    clusterNames?: string[] | null;
}

export type TaskPollFilter = {
    status?: string;
}

export const getTasks = async (clusterId: number, taskFilter?: TaskFilter) => {
    const resp = await apiClient.get<SlurmJob[]>(`/api/admin/cluster/${clusterId}`, {
        params: taskFilter
    });
    return resp.data;
}

export const pollTasks = async (clusterId: number, clusterName: string, taskPollFilter?: TaskPollFilter) => {
    const resp = await apiClient.get<SlurmJobInfo[]>(`/api/admin/cluster/${clusterId}/bound/${clusterName}/task`, {
        params: taskPollFilter
    });
    return resp.data;
}

export const createTask = async (clusterId: number, clusterName: string, request: TaskRequest) => {
    return await apiClient.post(`/api/admin/cluster/${clusterId}/bound/${clusterName}/task`, request);
}

export const cancelTask = async (clusterId: number, clusterName: string, taskId: number) => {
    return await apiClient.delete(`/api/admin/cluster/${clusterId}/bound/${clusterName}/task/${taskId}`);
}
