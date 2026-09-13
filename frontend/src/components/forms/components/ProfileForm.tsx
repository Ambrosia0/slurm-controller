import React, { Ref } from "react";
import { ClusterProfileAdminRequest } from "../../../api/admin/profiles"
import { SlurmClusterRec } from "../../../api/admin/clusters";
import { useTranslation } from 'react-i18next'
import { filterTres, formatTime, TresLimit } from "../../../utils/utils";

export type ProfileFormProps = {
    profile: Omit<ClusterProfileAdminRequest, 'userId'>;
    setProfile: (profile: Omit<ClusterProfileAdminRequest, 'userId'>) => void;
    bindedCluster: SlurmClusterRec;
    softLimitRef: React.Ref<HTMLInputElement>;
}

const ProfileForm: React.FC<ProfileFormProps> = ({
    profile,
    setProfile,
    bindedCluster,
    softLimitRef
}) =>{
    const filteredTres = filterTres(bindedCluster.tres);
    const { t } = useTranslation();

    const handleTresChange = (type: string, value: number) =>{
        const tresMap = new Map<string, number>();
        profile.maxTres?.forEach(tres => {
            const [tresType, count] = tres.split('=');
            if(tresType){
                tresMap.set(tresType, Number(count));
            }
        })

        if(value !== 0)
            tresMap.set(type, value);
        else
            tresMap.delete(type);
        const tresPerJob = Array.from(tresMap.entries())
            .map(([tresType, count]) => `${tresType}=${count}`);

        setProfile({...profile, maxTres: tresPerJob});
    }

    const currentTres: TresLimit[] = profile.maxTres?.map(val => {
        const splitted = val.split("=");
        return({
            type: splitted[0],
            count: Number(splitted[1])
        })
    }) ?? [];

    return(
        <>
            <div>
                <span>{`${t('forms.maxSubmit')} (${t("forms.unlimited", { 'count': 0 })})`}</span>
                <input 
                    type="number" 
                    name="maxSubmit" 
                    min={0} 
                    value={Number(profile.maxSubmit)}
                    placeholder={t('forms.maxSubmit')} 
                    onChange={(e) => setProfile({...profile, maxSubmit: Number(e.target.value)})}
                    required 
                />
            </div>
            <div>
                <span>{`${t('forms.maxJobs')} (${t("forms.unlimited", { 'count': 0 })})`}</span>
                <input 
                    type="number" 
                    name="maxJobs" 
                    min={0} 
                    value={Number(profile.maxTasks)}
                    placeholder={t('forms.maxJobs')} 
                    onChange={(e) => setProfile({...profile, maxTasks: Number(e.target.value)})}
                    required
                />
            </div>
            {filteredTres.map(tresType => {
                const maxTres = bindedCluster.tres.find(val=> val.type === tresType.type)?.count ?? "?";
                const currentValue = currentTres.find(val => val.type === tresType.type)?.count;
                return(
                    <div>
                    <span>{`${t('jobForm.tresPerJob', { type: tresType.type, max: maxTres })} (${t("forms.unlimited", { 'count': 0 })})`}</span>
                    <input 
                        key={tresType.type} 
                        type="number" 
                        name={`maxTres_${tresType.type}`} 
                        min={0} 
                        placeholder={
                            t('forms.maxTres', {
                                type: tresType.type, 
                                count: maxTres
                            }
                        )} 
                        value={currentValue ?? 0}
                        onChange={(e) => handleTresChange(tresType.type, Number(e.target.value))}
                        required
                    />
                </div>
                )
            })}
            <div>
                <span>{`${t('forms.softLimit')} (${t("forms.unlimited", { 'count': 0 })})`}</span>
                <input 
                    type="number" 
                    ref={softLimitRef} 
                    name="softLimit" 
                    value={profile.softLimit ?? 0}
                    min={0} 
                    placeholder={t('forms.softLimit')}
                    onChange={(e) => setProfile({...profile, softLimit: Number(e.target.value)})}
                />
            </div>
            <div>
                <span>{`${t('forms.hardLimit')} (${t("forms.unlimited", { 'count': 0 })})`}</span>
                <input 
                    type="number" 
                    name="hardLimit" 
                    value={profile.hardLimit ?? 0}
                    min={0} 
                    placeholder={t('forms.hardLimit')}
                    onChange={(e) => setProfile({...profile, hardLimit: Number(e.target.value)})}
                />
            </div>
            <input 
                type="number" 
                name="maxTaskLiveTime" 
                onChange={(e) => setProfile({...profile, maxTaskLiveTime: Number(e.target.value)})} 
                value={profile.maxTaskLiveTime ?? 0}
                min={0} 
                placeholder={t('forms.maxTaskLiveTime')} 
                required
            />
            <div>
                <span>{`${t('jobForm.maxExecutionTime')} (${t("forms.unlimited", { 'count': 0 })})`}</span>
                <span>{
                    formatTime(
                        profile.maxTaskLiveTime ?? 0,
                        t('jobForm.hours'),
                        t('jobForm.minutes'),
                        t('jobForm.seconds')
                    )}
                </span>
            </div>
        </>
    )
}

export default ProfileForm;