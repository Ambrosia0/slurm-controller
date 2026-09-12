import { useMemo, useState } from "react";
import { TresLimit } from "../TaskCreationForm";
import { JobRequest } from "../../../api/user/userApi";
import { SlurmClusterRec } from "../../../api/admin/clusters";
import { ClusterProfileResponse } from "../../../api/admin/profiles";

type JobFormProps = {
    profile?: ClusterProfileResponse;
    bindedCluster: SlurmClusterRec;
    job: JobRequest;
    index: number;
    tresLimits: TresLimit[];
    isBusy: boolean;
    changeValue: (job: JobRequest, index: number) => void;
}

export const JobForm: React.FC<JobFormProps> = ({
    profile,
    bindedCluster,
    isBusy,
    tresLimits,
    job,
    index,
    changeValue
}) => {
    const [args, setArgs] = useState<string[]>([]);
    const [inputVal, setInputVal] = useState("");

    
    const findTres = (type: string): TresLimit =>{
        return tresLimits.find(val => val.type === type) ?? {count: 0, type: "unknown"};
    }

    const cpuTres = useMemo(
        () => findTres("cpu"),
        [tresLimits]
    );

    const memTres = useMemo(
        () => findTres("mem"),
        [tresLimits]
    );

    const otherTres = useMemo(
        () =>
            tresLimits.filter(
                t => t.type !== "cpu" && t.type !== "mem"
            ),
        [tresLimits]
    );


    const handleChange = () =>{

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

        tresMap.set(type, value);

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

    const currentTres: TresLimit[] = job.tresPerJob?.split(",")
        .map((val, _) => {
            const tresValue = val.split("=");
            return({
                type: tresValue[0],
                count: Number(tresValue[1])
            })
        }) ?? [];

    return(
        <>
            <input
                type="text"
                placeholder="Аргумент..."
                value={inputVal}
                onChange={e => setInputVal(e.target.value)}
                onKeyDown={handleKeyDown}
                disabled={isBusy}
            />

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

            <input
                type="text"
                name="directory"
                placeholder="Рабочая директория..."
                required
                disabled={isBusy}
            />

            <input
                type="number"
                name="maxCpu"
                min={1}
                value={currentTres.find(val => val.type === "cpu")?.count}
                onChange={(e) => handleTresChange("cpu", Number(e.target.value))}
                max={cpuTres?.count}
                placeholder={`CPU на job (макс: ${findTres("cpu").count})`}
                required
                disabled={isBusy}
            />

            <input
                type="number"
                name="maxMem"
                min={1}
                value={currentTres.find(val => val.type === "mem")?.count}
                onChange={(e) => handleTresChange("mem", Number(e.target.value))}
                max={memTres?.count}
                placeholder={`RAM на job, MiB (макс: ${findTres("mem").count})`}
                required
                disabled={isBusy}
            />

            {otherTres.map(tres => (
                <input
                    key={tres.type}
                    type="number"
                    name={`maxTres_${tres.type}`}
                    value={currentTres.find(val => val.type === tres.type)?.count}
                    onChange={(e) => handleTresChange(tres.type, Number(e.target.value))}
                    min={0}
                    max={tres.count}
                    placeholder={`${tres.type} на job (макс: ${findTres(tres.type).count})`}
                    disabled={isBusy}
                />
            ))}

            <input
                type="text"
                name="nodes"
                value={job.nodes}
                placeholder={`Диапазон nodes, например 1-15:4 (доступно: ${bindedCluster.nodes || "?"})`}
                disabled={isBusy}
            />

            <input
                type="number"
                name="tasks"
                value={job.numberOfTasks}
                min={1}
                placeholder="Число задач"
                required
                disabled={isBusy}
            />

            <input
                type="number"
                name="cpusPerTask"
                value={job.cpusPerTask}
                min={0}
                placeholder="CPU на задачу (опционально)"
                disabled={isBusy}
            />

            <input
                type="number"
                name="maxTaskLiveTime"
                min={128}
                value={job.maxTaskLiveTime}
                placeholder="Максимальное время выполнения"
                required
                disabled={isBusy}
            />
        </>
    )
}