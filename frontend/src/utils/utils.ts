import { SlurmJob, SlurmJobInfo, SlurmTres, UnCancellableTaskStatus } from "./Interfaces";

export function isStatusUnCancellable(value: string): value is UnCancellableTaskStatus {
    return value === "FAILED" || value === "COMPLETED" || value === "CANCELLED" || value === "TIMEOUT" || value === "PREEMPTED";
}

export const NON_LIMITABLE_TRES = new Set([
    "energy",
    "billing",
    "pages",
    "fs",
])

export type TresLimit = {
    type: string;
    count: number;
}

export const formatTime = (seconds: number, h: string, m: string, s: string) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return [
        hours > 0 ? `${hours} ${h}` : "",
        minutes > 0 ? `${minutes} ${m}` : "",
        secs > 0 ? `${secs} ${s}` : ""
    ].filter(Boolean).join(" ") || `0 ${s}`;
}

export const filterTres = (tres: SlurmTres[]) =>{
    return tres.filter(val => !NON_LIMITABLE_TRES.has(val.type));
}

export const mapPollJobToSlurmJob = (info: SlurmJobInfo): SlurmJob => ({
    id: info.jobId ?? 0,
    name: info.jobId?.toString() ?? 'unknown',
    cluster: info.cluster ?? '',
    jobState: {
        current: info.jobState ?? [],
        reason: info.stateReason ?? ''
    },
    workingDirectory: info.currentWorkingDirectory ?? '',
    user: info.username ?? '',
    userId: 0,
    time: {
        submission: info.submitTime?.number,
        start: undefined,
        end: info.endTime?.number,
        eligible: info.eligibleTime?.number,
        suspended: info.suspendTime?.number,
    },
    failedNode: info.failedNode,
});

export function normalizeConstraint<T>(value: T): T{
    if(typeof value === 'string'){
        return (value.trim() === ""? undefined: value) as T;
    }
    if(Array.isArray(value)){
        return (value.length === 0? undefined: value.map(normalizeConstraint)) as T;
    }
    if(Number.isInteger(value)){
        return (value === 0? undefined: value) as T;
    }
    if(value !== null && typeof value === 'object'){
        return Object.fromEntries(
            Object.entries(value).map(([key, value]) => [
                key,
                normalizeConstraint(value)
            ]),
        ) as T;
    }
    return value;
}
