import "../../styles/profileInfo.css"

import "../../styles/components/list.css"
import { useEffect, useState } from "react";
import { ClusterUserResponse, getUserProfile, downloadUserProfile } from "../../api/user/userApi";
import { formatSecondsToHMS } from "../../utils/functions";
import { SlurmClusterRec } from "../../api/admin/clusters";
import { ClusterProfileResponse } from "../../api/admin/profiles";
import { useTranslation } from 'react-i18next'


interface UserClusterProps{
    cluster: ClusterUserResponse;
    bindedCluster: SlurmClusterRec;
}

const ProfileInfo: React.FC<UserClusterProps> = ({cluster, bindedCluster}) =>{
    const { t } = useTranslation();
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
            alert('Ошибка загрузки профиля');
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
                <h2>{t('profileInfo.profileInfo')}</h2>
                <ul className="list">
                    <li className="list-item">
                        <span>{t('profileInfo.profileName')}</span>
                        <span>{profileData.user.username}</span>
                    </li>
                    <li className="list-item">
                        <span>{t('profileInfo.group')}</span>
                        <span>{profileData.user.group?.name ?? '-'}</span>
                    </li>
                    <li className="list-item">
                        <span>{t('profileInfo.created')}</span>
                        <span>{new Date(profileData.createdAt).toLocaleString()}</span>
                    </li>
                </ul>

                <h2>{t('profileInfo.limitations')}</h2>
                <ul className="list">
                    <li className="list-item">
                        <span>{t('profileInfo.maxQueue')}</span>
                        <span>{profileData.maxSubmit}</span>
                    </li>
                    <li className="list-item">
                        <span>{t('profileInfo.maxRunning')}</span>
                        <span>{profileData.maxJobs}</span>
                    </li>
                    <li className="list-item">
                        <span>{t('profileInfo.tres')}</span>
                        <ul className="tres-list">
                            {tresEntries.length === 0 ? (
                                <li className="list-item">{t('profileInfo.noLimitations')}</li>
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
                        <span>{t('profileInfo.taskTime')}</span>
                        <span>{formatSecondsToHMS(profileData.maxTaskLiveTime)}</span>
                    </li>
                    <li className="list-item">
                        <span>{t('profileInfo.softLimit')}</span>
                        <span>{profileData.softLimit} MB</span>
                    </li>
                    <li className="list-item">
                        <span>{t('profileInfo.hardLimit')}</span>
                        <span>{profileData.hardLimit} MB</span>
                    </li>
                </ul>
            </> : isBusy ? <h2>{t('profileInfo.loading')}</h2> : <h2>{t('profileInfo.notFound')}</h2>}
            <button onClick={() => downloadProfile()}>{t('profileInfo.download')}</button>
        </div>
    );
}

export default ProfileInfo;