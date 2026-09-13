import '../../styles/queue.css'
import '../../styles/components/pagination.css'
import '../../styles/components/table.css'
import '../../styles/components/tableContainer.css'
import "../../styles/components/panelTab.css"
import "../../styles/components/updateButton.css"


import { useEffect, useState } from "react";
import { getTasks, cancelTask as apiCancelTask, TaskFilter, pollTasks } from '../../api/admin/tasks';
import MovingPanel from "../../utils/MovingPanel";
import { SlurmJob, statuses } from '../../utils/Interfaces'
import { getGroups, GroupAdminResponse } from '../../api/admin/groups'
import { getUsers, UserAdminResponse } from '../../api/admin/users'
import { SlurmClusterRec } from '../../api/admin/clusters'
import TaskCreationForm from '../forms/TaskCreationForm'
import { useTranslation } from 'react-i18next'
import { isStatusUnCancellable, mapPollJobToSlurmJob } from '../../utils/utils'

type QueueProps = {
    clusterId: number;
    bindedCluster: SlurmClusterRec;
}

const Queue: React.FC<QueueProps> = ({ ...props }) => {
    const { t } = useTranslation();
    const [tasks, setTasks] = useState<SlurmJob[]>([]);
    const [isBusy, setIsBusy] = useState<boolean>(false);

    const [dynamicPanel, setDynamicPanel] = useState<boolean>(false);
    const [activeForm, setActiveForm] = useState<string>();
    const [isFormOpen, setIsFormOpen] = useState<boolean>(false);

    const [searchText, setSearchText] = useState<string>('');
    const [searchOption, setSearchOption] = useState<'group' | 'user'>('group');
    const [searchResult, setSearchResult] = useState<UserAdminResponse[] | GroupAdminResponse[]>([]);

    const [taskFilter, setTaskFilter] = useState<TaskFilter>({
        clusterNames: [props.bindedCluster.name],
        startTime: Math.floor(Date.now() / 1000) - 24 * 60 * 60
    });


    const fetchTasks = async () => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            const [staticTasks, pollJobs] = await Promise.allSettled([
                getTasks(props.clusterId, taskFilter),
                pollTasks(props.clusterId, props.bindedCluster.name)
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
        } finally {
            setIsBusy(false);
        }
    }

    const cancelTask = async (taskId: number) => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            await apiCancelTask(props.clusterId, props.bindedCluster.name, taskId);
            setTasks(prev => prev.map(task => 
                task.id === taskId?{
                    ...task,
                    jobState: {
                        ...task.jobState,
                        current: [...task.jobState.current, "CANCELLED"]
                    }
                }:
                task
            ))
        } catch (error) {
            alert(t('queue.error'));
            console.log('Error!', error);
        } finally {
            setIsBusy(false);
        }
    }

    const fetchSearchResult = async (searchString: string) =>{
        try {
            const data = searchOption === 'group'?
                await getGroups(0, 10, null, 
                    {
                        clusterId: 
                        props.clusterId, 
                        bindedCluster: props.bindedCluster.name, 
                        name: searchString,
                        notInCluster: false
                    }
                ):
                await getUsers(0, 10, null, 
                    {
                        username: searchString,
                        clusterId: props.clusterId, 
                        bindedCluster: props.bindedCluster.name,
                        notInCluster: false
                    }
                );
            setSearchResult(data.content);
        } catch (error) {
            alert(t('queue.error'));
            console.log('Error!', error);
        }
    }

    const rerenderCurrentPage = () => {
        fetchTasks();
        return;
    }

    const handleStartChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (!value) {
            setTaskFilter(prev => ({
                ...prev,
                startTime: null
            }))
            return;
        }

        const unix = Math.floor(new Date(value).getTime() / 1000);
        if (taskFilter.endTime && unix > taskFilter.endTime) {
            setTaskFilter(prev => ({
                ...prev,
                endTime: null
            }));
        }
        setTaskFilter(prev => ({
                ...prev,
                startTime: unix
        }));
    };

    const handleEndChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (!value) {
            setTaskFilter(prev => ({
                ...prev,
                endTime: null
            }));
            return;
        }

        const unix = Math.floor(new Date(value).getTime() / 1000);
        if (taskFilter.startTime && unix < taskFilter.startTime) {
            setTaskFilter(prev => ({
                ...prev,
                startTime: null
            }));;
        }
        setTaskFilter(prev => ({
                ...prev,
                endTime: unix
        }));
    };
    const toDateTimeLocal = (timestamp: number) => {
        const date = new Date(timestamp * 1000);
        const pad = (n: number) => String(n).padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    };

    const handleSearchOptionChange = (option: 'group' | 'user') => {
        setSearchOption(option);
        setSearchText('');
    }

    const handleSearchTextChange = (searchText: string) =>{
        setTaskFilter(prev => ({
            ...prev,
            groupId: null,
            usernames: null
        }));
        setSearchText(searchText);
    }

    const handleTaskSortChange = (status: string[] | null) => {
        setTaskFilter(prev => ({
            ...prev,
            taskStatus: status
        }))
    }

    const handleRelatedResourceChange = (resource: UserAdminResponse | GroupAdminResponse) =>{
        if("username" in resource){
            setSearchText(resource.username)
            setTaskFilter(prev => ({
                ...prev,
                usernames: [resource.username]
            }));
        }
        if("name" in resource){
            setSearchText(resource.name);
            setTaskFilter(prev => ({
                ...prev,
                groupId: resource.id
            }));
        }
        setSearchResult([]);
    }

    useEffect(() => {
        const timeout = setTimeout(() => {
            if (searchText.length >= 3) {
                fetchSearchResult(searchText);
            } else {
                setSearchResult([]);
            }
        }, 500);
        return () => clearTimeout(timeout);
    }, [searchText]);


    useEffect(() => {
        fetchTasks();
    }, [taskFilter])


    const renderName = (item: UserAdminResponse | GroupAdminResponse) =>{
        if("name" in item){
            return item.name;
        }else
            return item.username;
    }


    return (
        <div id='queue-container'>
            <MovingPanel isOpen={dynamicPanel} onClose={() => setDynamicPanel(dynamicPanel? false: true) }>
                <div className="dynamic-panel-tab">
                    <button onClick={() => {
                        setIsFormOpen(true); 
                        setActiveForm('taskCreation')
                        }}
                    >
                        {t("queue.createTask")}
                    </button>
                </div>

                <div className="dynamic-panel-tab-content">
                    {activeForm === 'taskCreation' && 
                        <TaskCreationForm 
                            isBusy={isBusy} 
                            setBusy={setIsBusy} 
                            clusterId={props.clusterId} 
                            bindedCluster={props.bindedCluster} 
                            onSuccessCall={() => rerenderCurrentPage()} 
                            isOpen={isFormOpen} 
                            onClose={() => setIsFormOpen(false)} 
                        />}
                </div>
            </MovingPanel>

            <div className="table-option-container">
                <div className="queue-tools">
                    <div className="search-container">
                        <input
                            placeholder='Поиск...'
                            type='search'
                            value = {searchText}
                            onChange={(e) => handleSearchTextChange(e.target.value)}
                            className="search-input"
                        />
                        <svg className="search-icon" viewBox="0 0 24 24">
                            <path d="M15.5 14h-.79l-.28-.27a6.5 6.5 0 0 0 1.48-5.34c-.47-2.78-2.79-5-5.59-5.34a6.505 6.505 0 0 0-7.27 7.27c.34 2.8 2.56 5.12 5.34 5.59a6.5 6.5 0 0 0 5.34-1.48l.27.28v.79l4.25 4.25c.41.41 1.08.41 1.49 0 .41-.41.41-1.08 0-1.49L15.5 14zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
                        </svg>
                        <div></div>
                        {searchResult.length > 0 && (
                            <ul className='search-result-list'>
                                {searchResult.map((item,index) =>(
                                    <li key={index} 
                                        className='search-result-item'
                                        onClick={() => handleRelatedResourceChange(item)}>
                                        {renderName(item)}
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                    <label className="select-label">
                        {t('queue.searchOptions')}
                        <select
                            onChange={(e) => handleSearchOptionChange(e.target.value as 'group' | 'user')}
                            className="custom-select">
                            <option value="" disabled>{t('queue.searchByPlaceholder')}</option>
                            <option value="group">{t('queue.byGroup')}</option>
                            <option value="user">{t('queue.byUser')}</option>
                        </select>
                        <span className="select-arrow">▼</span>

                         <select
                            onChange={(e) => handleTaskSortChange([e.target.value])}
                            className="custom-select">
                            <option value="" selected>{t('queue.all')}</option>
                            {statuses.map((stat, index) => {
                                return <option key={index} value={stat}>{stat}</option>
                            })}
                        </select>
                        <span className="select-arrow">▼</span>
                    </label>
                     <label className="select-label">
                        {t('queue.searchDates')}
                        <input type='datetime-local' value={taskFilter.startTime? toDateTimeLocal(taskFilter.startTime): ''} onChange={handleStartChange} />
                        <input type='datetime-local' value={taskFilter.endTime? toDateTimeLocal(taskFilter.endTime) : ''} onChange={handleEndChange} />
                        <button className='update-button' type='button' onClick={fetchTasks}></button>
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
                                {tasks.map((task, index) => (
                                    <tr key={index}>
                                        <td>{index + 1}</td>
                                        <td>{task.id}</td>
                                        <td>{task.user}</td>
                                        <td>{task.jobState.current[task.jobState.current.length - 1]}</td>
                                        <td>{task.time?.submission ? new Date(task.time.submission * 1000).toLocaleString() : '-'}</td>
                                        <td>{task.time?.end ? new Date(task.time.end * 1000).toLocaleString() : '-'}</td>
                                        <td>{task.workingDirectory}</td>
                                        <td>
                                             {!isStatusUnCancellable(task.jobState.current[task.jobState.current.length - 1]) && <button onClick={() => cancelTask(task.id)}>{t('queue.cancel')}</button>}
                                        </td>
                                    </tr>
                                ))}
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