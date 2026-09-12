import "../../styles/components/form.css"

import ModalForm from "../../pages/ModalForm";
import { useEffect, useRef, useState } from "react";
import LoadingScreen from "../LoadingScreen";
import { createTask } from "../../api/admin/tasks";
import { SlurmClusterRec } from "../../api/admin/clusters";

import { getUserProfile, JobRequest, TaskRequest } from "../../api/user/userApi";
import { useAuth } from "../../utils/AuthContext";
import { JobForm } from "./components/JobForm";
import { ClusterProfileResponse } from "../../api/admin/profiles";

interface TaskFormProps {
    isOpen: boolean;
    onClose: () => void;
    onSuccessCall: () => void;
    clusterId: number;
    bindedCluster: SlurmClusterRec;
    isBusy: boolean;
    setBusy: (flag: boolean) => void;
}

export type TresLimit = {
    type: string;
    count: number;
}

const TaskCreationForm: React.FC<TaskFormProps> = ({
    isBusy,
    setBusy,
    clusterId,
    bindedCluster,
    onClose,
    onSuccessCall,
    isOpen,
}) => {
    const [script, setScript] = useState("");
    const [jobs, setJobs] = useState<JobRequest[]>([]);
    const [currentProfile, setCurrentProfile] = useState<ClusterProfileResponse>();
    const [openJobIndex, setOpenJobIndex] = useState<number | null>(null);

    const [tresLimits, setTresLimits] = useState<TresLimit[]>(
        bindedCluster.tres.map((value, _) => ({
            count: value.count ?? 0,
            type: value.type
        }))
    );

    const { role } = useAuth();

    const formRef = useRef<HTMLFormElement>(null);
    

    const submitTask = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!formRef.current) {
            return;
        }

        const form = formRef.current;

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const request: TaskRequest = {
            script: script,
            jobs: jobs,
        };

        try {
            setBusy(true);

            const start = Date.now();

            await createTask(
                clusterId,
                bindedCluster.name,
                request
            );

            const elapsed = Date.now() - start;
            const minWait = 3000;

            if (elapsed < minWait) {
                await new Promise(resolve =>
                    setTimeout(resolve, minWait - elapsed)
                );
            }

            onSuccessCall();
            onClose();
        } catch (error) {
            alert(error);
            console.error("Error creating task:", error);
        } finally {
            setBusy(false);
        }
    };

    const getProfileInfo = async () =>{
            try {
                const data = await getUserProfile(clusterId, bindedCluster.name);
                setCurrentProfile(data);
                setTresLimits(
                    data.maxTres.map(val => {
                        const arr = val.split("=");
                        return({
                            type: arr[0],
                            count: Number(arr[1])
                        })
                    })
                )
            } catch (error: any) {
                const message =
                    error?.response?.data?.body?.detail ||
                    error?.response?.data?.error ||
                    error?.message ||
                    'Неизвестная ошибка';
                alert('Ошибка при добавлении профиля: ' + message);
            }
        }

    const addJob = (job: JobRequest) =>{
        setJobs(prev => {
            const newJobs = [...prev, job];
            setOpenJobIndex(newJobs.length - 1);
            return newJobs;
        });
    }

    const removeJob = (indexToRemove: number) =>{
        setJobs(prev => 
            prev.filter((_, index) => index !== indexToRemove)
        );

        setOpenJobIndex(prev => {
            if(prev == null)
                return null;
            if(prev === indexToRemove)
                return null;
            if(prev > indexToRemove)
                return prev - 1;
            return prev;
        })
    }

    const handleJobChange = (job: JobRequest, index: number) =>{
        setJobs(prev => (
                prev.map((val, idx) => idx === index? job: val)
            )
        );
    }

    const toggleJob = (index: number) => {
        setOpenJobIndex(prev => 
            prev === index? null: index
        )
    };
    
    useEffect(() =>{
        if(role === "ROLE_USER")
            getProfileInfo();
    },[])

    return (
        <ModalForm 
            isOpen={isOpen} 
            onClose={onClose}
        >
            <form
                ref={formRef}
                className="form"
                onSubmit={submitTask}
            >
                <textarea
                    name="script"
                    value={script}
                    onChange={e => setScript(e.target.value)}
                    placeholder="Текст скрипта..."
                    required
                    disabled={isBusy}
                />
                <div className="jobs">
                {
                    jobs.map((job, index) => {
                        return(
                            <div
                                key={index}
                                className="job-accordion"
                            >
                                <div
                                    className="job-accordion_header"
                                    onClick={() => toggleJob(index)}
                                >
                                    <span>
                                        Job #{index+1}
                                    </span>
                                    <div>
                                        <button
                                            type="button"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                removeJob(index);
                                            }}
                                            disabled={isBusy}
                                        >
                                            ✕
                                        </button>
                                        <span
                                            className="job-accodion_icon"
                                        >
                                            {openJobIndex === index?
                                                "^":
                                                "⌄"
                                            }
                                        </span>
                                    </div>
                                </div>

                                {openJobIndex === index && (
                                    <div className="job-accordion_body">
                                        <JobForm
                                            key={index}
                                            bindedCluster={bindedCluster}
                                            isBusy={isBusy}
                                            job={job}
                                            index={index}
                                            changeValue={handleJobChange}
                                            tresLimits={tresLimits}
                                            profile={currentProfile}
                                        />
                                    </div>
                                )}
                            </div>
                        )
                    })
                }
                </div>

                <div className="task-form_actions">
                    <button
                        type="button"
                        onClick={onClose}
                        disabled={isBusy}
                    >
                        Назад
                    </button>

                    <button
                        type="button"
                        onClick={() => 
                            addJob({
                                args: [],
                                directory: "",
                                tresPerJob: "",
                                maxNodes: 1,
                                nodes: "",
                                numberOfTasks: 1,
                                cpusPerTask: 0,
                                maxTaskLiveTime: 128
                            })
                        }
                        disabled={isBusy}
                    >
                        Добавить задачу
                    </button>

                    <button
                        type="submit"
                        disabled={isBusy || jobs.length === 0}
                    >
                        Создать
                    </button>
                </div>

                {isBusy && <LoadingScreen />}
            </form>
        </ModalForm>
    );
};

export default TaskCreationForm;

