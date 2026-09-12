import { ClusterProfileResponse } from "../api/admin/profiles";
import { GroupAdminResponse } from "../api/admin/groups";
import { ClusterAdminResponse } from "../api/admin/clusters";
import { SlurmClusterRec } from "../api/admin/clusters";
import { UserAdminResponse } from "../api/admin/users";

export * from "../api/admin/Interfaces";

// Task types (old names kept for compatibility)
export type Task = {
    id: number;
    name: string;
    time: Time;
    state: JobState;
    user: string;
    userId: number;
    association: Assoc;
    workingDirectory: string;
}

export type Assoc = {
    user: string;
    id: number;
}

export type JobState = {
    current: string;
    reason: string;
}

export type Time = {
    elapsed?: number;
    end?: number;
    submission?: number;
    eligible?: number;
    suspended?: number;
    start?: number;
}

export type Cluster = ClusterAdminResponse;
export type BindedCluster = SlurmClusterRec;

export type Profile = ClusterProfileResponse;

export type Group = GroupAdminResponse;

export type User = UserAdminResponse;

// User BindCluster types
export type SlurmTres = {
    type: string;
    name?: string;
    id?: number;
    count?: number;
}

export type SlurmUint32NoVal = {
    set?: boolean;
    infinite?: boolean;
    number?: number;
}

export type SlurmUint64NoVal = {
    set?: boolean;
    infinite?: boolean;
    number?: number;
}

export type SlurmJobTime = {
    elapsed?: number;
    end?: number;
    submission?: number;
    eligible?: number;
    suspended?: number;
    start?: number;
}

export type SlurmJobReq = {
    cpus?: number;
    memoryPerCpu?: SlurmUint64NoVal;
    memoryPerNode?: SlurmUint64NoVal;
}

export type SlurmAssocShort = {
    account?: string;
    cluster?: string;
    partition?: string;
    user?: string;
    id: number;
}

export type SlurmJobState = {
    current: string[];
    reason: string;
}

export type SlurmProcessExitCodeVerboseSignal = {
    id?: SlurmUint32NoVal;
    name?: string;
}

export type SlurmProcessExitCodeVerbose = {
    status?: string[];
    returnCode?: SlurmUint32NoVal;
    signal?: SlurmProcessExitCodeVerboseSignal;
}

export type SlurmJobTres = {
    allocated?: SlurmTres[];
    requested?: SlurmTres[];
}

// TaskStatus type
export type TaskStatus = "BOOT_FAIL" | "CANCELLED" | "COMPLETED" | "DEADLINE" | "FAILED" | "NODE_FAIL" | "OUT_OF_MEMORY" | "PENDING" | "PREEMPTED" | "RUNNING" | "SUSPENDED" | "TIMEOUT";

export type UnCancellableTaskStatus = "FAILED" | "COMPLETED" | "CANCELLED" | "TIMEOUT" | "PREEMPTED";

export const statuses: TaskStatus[] = ["BOOT_FAIL", "CANCELLED", "COMPLETED", "DEADLINE", "FAILED", "NODE_FAIL", "OUT_OF_MEMORY", "PENDING", "PREEMPTED", "RUNNING", "SUSPENDED", "TIMEOUT"];

export function isStatusUnCancellable(value: string): value is UnCancellableTaskStatus {
    return value === "FAILED" || value === "COMPLETED" || value === "CANCELLED" || value === "TIMEOUT" || value === "PREEMPTED";
}

export const NON_LIMITABLE_TRES = new Set([
    "energy",
    "billing",
    "pages",
    "fs",
])

export const filterTres = (tres: SlurmTres[]) =>{
    return tres.filter(val => !NON_LIMITABLE_TRES.has(val.type));
}

export interface Statistics {
    jobsSubmitted: number;
    jobsStarted: number;
    jobsCompleted: number;
    jobsCancelled: number;
    jobsFailed: number;
    jobsRunning: number;
    timestamp: string;
    nodes: NodeStatistics[];
}

interface NodeStatistics {
    hostname: string;
    name: string;
    bootTime: uint64;
    state: string[];
    sockets: number;
    cpus: number;
    cores: number;
    threads: number;
    allocatedCpus: number;
    allocatedIdleCpus: number;
    freeMemory: uint64;
    allocatedMemory: number;
    gres: string;
    gresDrained: string;
    gresUsed: string;
    lastBusy: uint64;
    cpuLoad: number;
    tresUsed: string;
    tres: string;
    reason: string;
}

interface uint64 {
    number: number
}

export interface LogoutProp {
    logout: () => void;
}

export interface FormProps {
    isOpen: boolean;
    onClose: () => void;
}

export interface ActionFormProps {
    isOpen: boolean;
    onClose: () => void;
    onSuccessCall: () => void;
}

export interface ProfileActionFormProps {
    isOpen: boolean;
    onSuccessCall: () => void;
    onClose: () => void;
    clusterId: number;
    bindedCluster: SlurmClusterRec;
    isBusy: boolean;
    setIsBusy: (flag: boolean) => void;
}

export type SlurmJob  = {
    id: number;
    name: string;
    cluster: string;
    time?: SlurmJobTime;
    required?: SlurmJobReq;
    jobState: SlurmJobState;
    exitCode?: SlurmProcessExitCodeVerbose;
    failedNode?: string;
    user: string;
    userId: number;
    nodes?: string;
    association?: SlurmAssocShort;
    tres?: SlurmJobTres;
    usedGres?: string;
    workingDirectory: string;
};


export type SlurmJobInfo  = {
    jobId?: number;
    jobState?: string[];
    account?: string;
    cluster?: string;
    comment?: string;
    failedNode?: string;
    gresDetail?: string[];
    cpus?: SlurmUint32NoVal;
    tasks?: SlurmUint32NoVal;
    nodeCount?: SlurmUint32NoVal;
    username?: string;
    currentWorkingDirectory?: string;
    endTime?: SlurmUint64NoVal;
    eligibleTime?: SlurmUint64NoVal;
    submitTime?: SlurmUint64NoVal;
    suspendTime?: SlurmUint64NoVal;
    timeLimit?: SlurmUint32NoVal;
    tresPerJob?: string;
    tresPerNode?: string;
    tresPerTask?: string;
    tresReqStr?: string;
    tresAllocStr?: string;
    stateReason?: string;
};