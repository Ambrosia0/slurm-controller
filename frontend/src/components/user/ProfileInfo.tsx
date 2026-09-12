import "../../styles/profileInfo.css"

import "../../styles/components/list.css"
import { useEffect, useState } from "react";
import { ClusterUserResponse, getUserProfile, downloadUserProfile } from "../../api/user/userApi";
import { formatSecondsToHMS } from "../../utils/functions";
import { SlurmClusterRec } from "../../api/admin/clusters";
import { ClusterProfileResponse } from "../../api/admin/profiles";


interface UserClusterProps{
    cluster: ClusterUserResponse;
    bindedCluster: SlurmClusterRec;
}

const ProfileInfo: React.FC<UserClusterProps> = ({cluster, bindedCluster}) =>{
    const [profileData, setProfileData] = useState<ClusterProfileResponse>();
    const [isBusy, setIsBusy] = useState<boolean>(false);

    const fetchProfileInfo = async() =>{
        if (isBusy) return;
        setIsBusy(true);
        try {
            const data = await getUserProfile(cluster.id, bindedCluster.name);
            setProfileData(data);
        } catch (error) {
            alert('Ошибка загрузки профиля');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

    const parseTres = (maxTres: string[] | null): Record<string, string> => {
        if (!maxTres || maxTres.length === 0) return {};
        const result: Record<string, string> = {};
        for (const tres of maxTres) {
            const parts = tres.split('=');
            if (parts.length === 2) {
                result[parts[0]] = parts[1];
            }
        }
        return result;
    };

    const downloadProfile = async () => {
        if (isBusy) return;
        setIsBusy(true);
        try {
            await downloadUserProfile(cluster.id, bindedCluster.name);
        } catch (error) {
            alert('Ошибка скачивания профиля');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

    const tres = profileData ? parseTres(profileData.maxTres) : {};
    const tresEntries = Object.entries(tres);

    useEffect(() => {
        fetchProfileInfo();
    }, []);

    return (
        <div id="statistics-node-container">
            {profileData ? <>
                <h2>Информация о профиле</h2>
                <ul className="list">
                    <li className="list-item">
                        <span>Имя профиля:</span>
                        <span>{profileData.user.username}</span>
                    </li>
                    <li className="list-item">
                        <span>Группа:</span>
                        <span>{profileData.user.group?.name ?? '-'}</span>
                    </li>
                    <li className="list-item">
                        <span>Создан:</span>
                        <span>{new Date(profileData.createdAt).toLocaleString()}</span>
                    </li>
                </ul>

                <h2>Ограничения</h2>
                <ul className="list">
                    <li className="list-item">
                        <span>Макс. задач в очереди:</span>
                        <span>{profileData.maxSubmit}</span>
                    </li>
                    <li className="list-item">
                        <span>Макс. выполняемых задач:</span>
                        <span>{profileData.maxJobs}</span>
                    </li>
                    <li className="list-item">
                        <span>TRES:</span>
                        <ul className="tres-list">
                            {tresEntries.length === 0 ? (
                                <li className="list-item">Нет ограничений</li>
                            ) : (
                                tresEntries.map(([key, value]) => (
                                    <li key={key} className="list-item">
                                        <span>{key}:</span>
                                        <span>{value}</span>
                                    </li>
                                ))
                            )}
                        </ul>
                    </li>
                    <li className="list-item">
                        <span>Время выполнения задачи:</span>
                        <span>{formatSecondsToHMS(profileData.maxTaskLiveTime)}</span>
                    </li>
                    <li className="list-item">
                        <span>Мягкий лимит (дисковое пространство):</span>
                        <span>{profileData.softLimit} MB</span>
                    </li>
                    <li className="list-item">
                        <span>Жёсткий лимит (дисковое пространство):</span>
                        <span>{profileData.hardLimit} MB</span>
                    </li>
                </ul>
            </> : isBusy ? <h2>Загрузка...</h2> : <h2>Профиль не найден</h2>}
            <button onClick={() => downloadProfile()}>Скачать профиль</button>
        </div>
    );
}

export default ProfileInfo;