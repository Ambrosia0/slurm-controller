import '../../styles/queue.css'
import '../../styles/components/pagination.css'
import '../../styles/components/table.css'
import '../../styles/components/tableContainer.css'
import "../../styles/components/panelTab.css"
import "../../styles/components/updateButton.css"

import { useEffect, useState } from "react";
import { cancelTask as apiCancelTask } from '../../api/user/userApi';
import MovingPanel from "../../utils/MovingPanel";
import { BindedClusterResponse, ClusterUserResponse, getUserTasks, pollTasks, TaskFilter } from '../../api/user/userApi'
import { SlurmJob, statuses, TaskStatus } from '../../utils/Interfaces'
import TaskCreationForm from '../forms/TaskCreationForm'
import { useTranslation } from 'react-i18next'
import { isStatusUnCancellable, mapPollJobToSlurmJob } from '../../utils/utils'

interface UserClusterProps{
    cluster: ClusterUserResponse;
    bindedCluster: BindedClusterResponse;
}

const Queue: React.FC<UserClusterProps> = ({cluster, bindedCluster}) =>{
    const [tasks, setTasks] = useState<SlurmJob[]>([]);
    const [isBusy, setIsBusy] = useState<boolean>(false);
    
    const [dynamicPanel, setDynamicPanel] = useState<boolean>(false);

    const [activeForm, setActiveForm] = useState<string>();
    const [isFormOpen, setIsFormOpen] = useState<boolean>(false);

    const [sortOption, setSortOption] = useState<TaskStatus | null>(null);

    const [startTime, setStartTime] = useState<number | null>(Math.floor(Date.now() / 1000) - 24 * 60 * 60);
    const [endTime, setEndTime] = useState<number | null>(null);

    const [taskFilter, setTaskFilter] = useState<TaskFilter>({
        clusterNames: [bindedCluster.name],
        startTime: Math.floor(Date.now() / 1000) - 24 * 60 * 60
    })

    const { t } = useTranslation();

    const fetchTasks = async() =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            const [staticTasks, pollJobs] = await Promise.allSettled([
                        getUserTasks(cluster.id, taskFilter),
                        pollTasks(cluster.id, bindedCluster.name)
                    ]);
            const baseTasks: SlurmJob[] = staticTasks.status === 'fulfilled' ? staticTasks.value : [];
            const pollTaskList: SlurmJob[] = pollJobs.status === 'fulfilled' ? pollJobs.value.map(mapPollJobToSlurmJob) : [];
            const combined = [...baseTasks, ...pollTaskList];
            const uniqueMap = new Map<number, SlurmJob>();
            for (const task of combined) {
                uniqueMap.set(task.id, task);
            }
            setTasks(Array.from(uniqueMap.values()));
        } catch (error) {
            alert(t('queue.error'));
            console.log('Error!', error);
        } finally{
            setIsBusy(false);
        }
    }
    const cancelTask = async(taskId: number) =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            await apiCancelTask(cluster.id, bindedCluster.name, taskId);
            setTasks(prev => prev.map(task => 
                task.id === taskId?{
                    ...task,
                    jobState: {
                        ...task.jobState,
                        current: [...task.jobState.current, "CANCELLED"]
                    }
                }:
                task
            ));
        } catch (error) {
            alert(t('queue.error'));
            console.log('Error!', error);
        } finally{
            setIsBusy(false);
        }
    }

    const toDateTimeLocal = (timestamp: number) => {
        const date = new Date(timestamp * 1000);
        const pad = (n: number) => String(n).padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    };

    const handleStartChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (!value) {
            setStartTime(null);
            return;
        }

        const unix = Math.floor(new Date(value).getTime() / 1000);
        if (endTime !== null && unix > endTime) {
            setEndTime(null);
        }
        setStartTime(unix);
    };

    const handleEndChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (!value) {
            setEndTime(null);
            return;
        }

        const unix = Math.floor(new Date(value).getTime() / 1000);
        if (startTime !== null && unix < startTime) {
            setStartTime(null);
        }
        setEndTime(unix);
    };


    useEffect(() => {
        fetchTasks();
    }, [sortOption, startTime, endTime]);



    return (
        <div id='queue-container'>
            <MovingPanel isOpen={dynamicPanel} onClose={() => setDynamicPanel(dynamicPanel ? false : true)}>
                <div className="dynamic-panel-tab">
                    <button onClick={() => { setActiveForm('taskCreation'); setIsFormOpen(true) }}>{t('queue.createTask')}</button>
                </div>

                <div className="dynamic-panel-tab-content">
                    {activeForm === 'taskCreation' && <TaskCreationForm isBusy={isBusy} setBusy={setIsBusy} clusterId={cluster.id} bindedCluster={bindedCluster} onSuccessCall={() => fetchTasks()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} />}
                </div>
            </MovingPanel>

            <div className="table-option-container">
                <div className="queue-tools">
                       <label className="select-label">
                        <select
                            value={sortOption ?? ""}
                            onChange={(e) => setSortOption((e.target.value === "" ? null : e.target.value) as TaskStatus | null)}
                            className="custom-select">
                            <option value="">{t('queue.all')}</option>
                            {statuses.map((stat, index) => (
                                <option key={index} value={stat}>{stat}</option>
                            ))}
                        </select>
                        <span className="select-arrow">▼</span>
                    </label>
                    <label className="select-label">
                        {t('queue.searchDates')}
                        <input type='datetime-local' value={startTime !== null ? toDateTimeLocal(startTime): ''} onChange={handleStartChange} />
                        <input type='datetime-local' value={endTime !== null ? toDateTimeLocal(endTime) : ''} onChange={handleEndChange} />
                        <button className='update-button' type='button' onClick={(e) => {fetchTasks()}}></button>
                    </label>
                </div>
                <div className='table-container'>
                    {isBusy? <h2>{t('queue.loading')}</h2> : (tasks.length !== 0)? <>
                        <table className='table'>
                            <thead>
                                <tr>
                                    <th>№</th>
                                    <th>{t('queue.taskId')}</th>
                                    <th>{t('queue.username')}</th>
                                    <th>{t('queue.status')}</th>
                                    <th>{t('queue.createdDate')}</th>
                                    <th>{t('queue.endDate')}</th>
                                    <th>{t('queue.directory')}</th>
                                    <th>{t('queue.action')}</th>
                                </tr>
                            </thead>
                            <tbody>
                                {tasks.map((task, index) => {
                                    const current = task.jobState?.current ?? [];
                                    const lastStatus = current.length > 0? current[current.length - 1]: null;
                                    return(
                                        <tr key={index}>
                                        <td>{index + 1}</td>
                                        <td>{task.id}</td>
                                        <td>{task.user}</td>
                                        <td>{lastStatus ?? "-"}</td>
                                        <td>{task.time?.submission ? new Date(task.time.submission * 1000).toLocaleString() : '-'}</td>
                                        <td>{task.time?.end ? new Date(task.time.end * 1000).toLocaleString() : '-'}</td>
                                        <td>{task.workingDirectory}</td>
                                        <td>
                                            {lastStatus && !isStatusUnCancellable(lastStatus) && <button onClick={() => cancelTask(task.id)}>{t('queue.cancel')}</button>}
                                        </td>
                                    </tr>
                                    )
                                })}
                            </tbody>
                        </table>
                        </> : <h2>{t('queue.noTasks')}</h2>
                    }
                </div>
            </div>
        </div>
    )
}
export default Queue;