import { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { JobRequest } from "../../../api/user/userApi";
import { SlurmClusterRec } from "../../../api/admin/clusters";
import { ClusterProfileResponse } from "../../../api/admin/profiles";
import { formatTime, TresLimit } from "../../../utils/utils";

type JobFormProps = {
    bindedCluster: SlurmClusterRec;
    profile?: ClusterProfileResponse;
    job: JobRequest;
    index: number;
    tresLimits: TresLimit[];
    isBusy: boolean;
    changeValue: (job: JobRequest, index: number) => void;
}

export const JobForm: React.FC<JobFormProps> = ({
    bindedCluster,
    isBusy,
    tresLimits,
    profile,
    job,
    index,
    changeValue
}) => {
    const { t } = useTranslation();
    const [args, setArgs] = useState<string[]>([]);
    const [inputVal, setInputVal] = useState("");

    
    const findTres = (type: string): TresLimit =>{
        return tresLimits.find(val => val.type === type) ?? {count: 0, type: "unknown"};
    }

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key !== "Enter") {
            return;
        }

        e.preventDefault();

        const value = inputVal.trim();

        if (!value) {
            return;
        }

        setInputVal("");
        handleArgAdd(value);
    };

    const handleTresChange = (type: string, value: number) => {
        const tresMap = new Map<string, number>();

        job.tresPerJob?.split(",").filter(Boolean).forEach(item => {
            const [tresType, tresCount] = item.split("=");
            if(tresType){
                tresMap.set(tresType, Number(tresCount));
            }
        })

        if(value !== 0)
            tresMap.set(type, value);
        else
            tresMap.delete(type);

        const tresPerJob = Array.from(tresMap.entries())
            .map(([tresType, count]) => `${tresType}=${count}`)
            .join(",");
        changeValue(
            {
                ...job,
                tresPerJob
            },
            index
        )
    }

    const handleArgAdd = (arg: string) => {
        changeValue(
            {
                ...job,
                args: [...(job.args ?? []), arg]
            },
            index
        )
    }

    const handleArgRemove = (index: number) => {
        changeValue(
            {
                ...job,
                args: job.args?.filter((_, idx) => idx !== index)
            },
            index
        )
    }

    const handleTaskLiveChange = (value: number) => {
        if(profile && value > profile.maxTaskLiveTime)
            return;
        changeValue(
            {
                ...job,
                maxTaskLiveTime: value
            },
            index
        )
    }

    const currentTres: TresLimit[] = job.tresPerJob?.split(",")
        .map((val, _) => {
            const tresValue = val.split("=");
            return({
                type: tresValue[0],
                count: Number(tresValue[1])
            })
        }) ?? [];

    const stringToNumber = (number: string) => {
        const val = Number(number);
        return number === ""? undefined: val;
    }

    return(
        <>
            <div>
                <span>{t('jobForm.arguments')}</span>
                <input
                    type="text"
                    placeholder={t('jobForm.argumentPlaceholder')}
                    value={inputVal}
                    onChange={e => setInputVal(e.target.value)}
                    onKeyDown={handleKeyDown}
                    disabled={isBusy}
                />
                <div className="arg-list">
                    {job.args?.map((val, index) => {
                        return(
                            <div 
                                key={index}
                                className="arg-container"
                                onClick={(e) => {
                                    e.preventDefault(); 
                                    handleArgRemove(index);
                                }}
                            >
                                <span>{val}</span>
                                <span className="arg-container-close">✕</span>
                            </div>
                        )
                    })}
                </div>
            </div>

            {args.length > 0 && (
                <ul>
                    {args.map((arg, index) => (
                        <li key={`${arg}-${index}`}>
                            {arg}

                            <button
                                type="button"
                                onClick={() => handleArgRemove(index)}
                                disabled={isBusy}
                            >
                                ✕
                            </button>
                        </li>
                    ))}
                </ul>
            )}

            <div>
                <span>{t('jobForm.workingDirectory')}</span>
                <input
                    type="text"
                    name="directory"
                    placeholder={t('jobForm.workingDirectoryPlaceholder')}
                    value={job.directory}
                    onChange={(e) => changeValue({
                        ...job,
                        directory: e.target.value
                    }, index)}
                    disabled={isBusy}
                />
            </div>
            {tresLimits.map(tres => (
                <div>
                    <span>{t('jobForm.tresPerJob', { type: tres.type, max: findTres(tres.type).count })}</span>
                    <input
                        key={tres.type}
                        type="number"
                        name={`maxTres_${tres.type}`}
                        value={currentTres.find(val => val.type === tres.type)?.count}
                        onChange={(e) => handleTresChange(tres.type, Number(e.target.value))}
                        min={0}
                        max={tres.count}
                        placeholder={t('jobForm.tresPlaceholder', { type: tres.type })}
                        disabled={isBusy}
                    />
                </div>
            ))}

            <div>
                <span>{bindedCluster.nodes}</span>
                <input
                    type="text"
                    name="nodes"
                    value={job.nodes}
                    onChange={(e) => changeValue({...job, nodes: e.target.value}, index)}
                    placeholder={t('jobForm.nodesRange', { available: bindedCluster.nodes || '?' })}
                    disabled={isBusy}
                />
            </div>

            <div>
                <span>{t('jobForm.numberOfTasks')}</span>
                <input
                    type="number"
                    name="tasks"
                    value={job.numberOfTasks}
                    onChange={(e) => changeValue({
                        ...job, 
                        numberOfTasks: stringToNumber(e.target.value)
                    }, index)}
                    placeholder={t('jobForm.numberOfTasksPlaceholder')}
                    required
                    disabled={isBusy}
                />
            </div>

            <div>
                <span>{t('jobForm.cpuPerTask')}</span>
                <input
                    type="number"
                    name="cpusPerTask"
                    onChange={(e) => changeValue({...job, cpusPerTask: Number(e.target.value)}, index)}
                    value={job.cpusPerTask}
                    min={0}
                    placeholder={t('jobForm.cpuPerTaskPlaceholder')}
                    disabled={isBusy}
                />
            </div>
            
             <div>
                <span>{profile ? t('jobForm.maxExecutionTimeWithLimit', { limit: profile.maxTaskLiveTime }) : t('jobForm.maxExecutionTime')}</span>
                <input
                    type="number"
                    name="maxTaskLiveTime"
                    onChange={(e) => handleTaskLiveChange(Number(e.target.value))}
                    min={0}
                    value={job.maxTaskLiveTime}
                    placeholder={t('jobForm.maxExecutionTimePlaceholder')}
                    required
                    disabled={isBusy}
                />
                <span>{
                    formatTime(
                        job.maxTaskLiveTime ?? 0,
                        t('jobForm.hours'),
                        t('jobForm.minutes'),
                        t('jobForm.seconds')
                    )}
                </span>
            </div>

            <div>
                <span></span>
            </div>
        </>
    )
}