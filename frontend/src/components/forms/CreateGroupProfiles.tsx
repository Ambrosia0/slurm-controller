import "../../styles/components/form.css"

import ModalForm from "../../pages/ModalForm";
import { getGroups as apiGetGroups, GroupAdminResponse } from "../../api/admin/groups"
import { useRef, useState } from "react";
import { createProfile, ClusterProfileAdminRequest } from "../../api/admin/profiles";
import LoadingScreen from "../LoadingScreen";
import {  ProfileActionFormProps } from "../../utils/Interfaces";
import { getUsers } from "../../api/admin/users";
import { useTranslation } from 'react-i18next'
import ProfileForm from "./components/ProfileForm";
import { normalizeConstraint } from "../../utils/utils";

const CreateGroupProfiles: React.FC<ProfileActionFormProps> = ({
    isBusy, 
    setIsBusy, 
    bindedCluster, 
    clusterId, 
    isOpen, 
    onSuccessCall, 
    onClose
}) =>{
    
    const [groups, setGroups] = useState<GroupAdminResponse[]>([]);
    const [request, setRequest] = useState<Omit<ClusterProfileAdminRequest, 'userId'>>({
        hardLimit: 0,
        maxSubmit: 0,
        maxTaskLiveTime: 0,
        maxTasks: 0,
        maxTres: [],
        softLimit: 0
    });
    const formRef = useRef<HTMLFormElement>(null);
    const softLimitRef = useRef<HTMLInputElement>(null);
    const { t } = useTranslation();

    const provideAccess = async () =>{
        if(!formRef.current || isBusy)
            return;

        const form = formRef.current;
        if(!form.checkValidity()){
            form.reportValidity();
            return;
        }
        const formData = new FormData(form);

        try {
            setIsBusy && setIsBusy(true);
            const usersData = (await getUsers(0, 50, null, {
                groupId: Number(formData.get('group')),
                clusterId: clusterId,
                bindedCluster: bindedCluster.name,
                notInCluster: true
            })).content;

            const profiles: ClusterProfileAdminRequest[] = [];

            const normalized = normalizeConstraint(request);
            usersData.forEach(user =>{
                profiles.push({
                    ...normalized,
                    userId: user.id
                })
            });
            await createProfile(clusterId, bindedCluster.name, profiles);
            onSuccessCall();
            onClose();
        } catch (error) {
            alert(error);
            console.log("Error!", error);
        } finally{
            setIsBusy && setIsBusy(false);
        }
    }

    const getGroups = async () => {
        try {
            const data = await apiGetGroups(0, 50, null, {
                clusterId: clusterId,
                bindedCluster: bindedCluster.name,
                notInCluster: true
            });
            setGroups(data.content);
        } catch (error) {
            alert(error);
            console.log("Error!", error);
        }
    }

    const handleFocus = () =>{
        getGroups();
    }

    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form ref={formRef} className="form" onSubmit={(e) => e.preventDefault()}>
                <label>
                    {t('forms.groupLabel')}
                    <select name="group" onFocus={handleFocus} required>
                        {groups.length !== 0 && groups.map((group, index) => (
                            <option key={index} value={group.id}>{group.name}</option>
                        ))}
                    </select>
                </label>
                <ProfileForm 
                    bindedCluster={bindedCluster}
                    profile={request}
                    setProfile={setRequest}
                    softLimitRef={softLimitRef}
                />
                <div className="form-action">
                    <button onClick={onClose}>{t('common.back')}</button>
                    <button onClick={() => provideAccess()}>{t('forms.grantAccess')}</button>
                </div>
                {isBusy && <LoadingScreen />}
            </form>
        </ModalForm>
    );
}

export default CreateGroupProfiles;