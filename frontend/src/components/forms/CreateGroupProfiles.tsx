import "../../styles/components/form.css"

import ModalForm from "../../pages/ModalForm";
import { getGroups as apiGetGroups, GroupAdminResponse } from "../../api/admin/groups"
import { useRef, useState } from "react";
import { createProfile, ClusterProfileAdminRequest, getProfiles } from "../../api/admin/profiles";
import RandExp from "randexp";
import LoadingScreen from "../LoadingScreen";
import { filterTres, ProfileActionFormProps } from "../../utils/Interfaces";
import { UserAdminResponse } from "../../api/admin/users";

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
    const formRef = useRef<HTMLFormElement>(null);
    const softLimitRef = useRef<HTMLInputElement>(null);

    const provideAccess = async () =>{
        if(!formRef.current || isBusy)
            return
        const form = formRef.current;
        if(!form.checkValidity()){
            form.reportValidity();
            return;
        }
        const formData = new FormData(form);

        const softLimit = Number(formData.get("softLimit"));
        const hardLimit = Number(formData.get("hardLimit"));
        if(softLimit > hardLimit){
            softLimitRef.current?.reportValidity();
        }

        softLimitRef.current?.setCustomValidity("")

        const maxTres = [
            ...bindedCluster.tres.map(t => {
                const val = Number(formData.get(`maxTres_${t.type}`));
                if(!val || Number.isNaN(val))
                    return null;
                return `${t.type}=${val}`;
            })
                .filter((val): val is string => val !== null)
        ]

        try {
            setIsBusy && setIsBusy(true);
            const usersData = await getProfiles(0, 50, null, {
                groupId: Number(formData.get('group')),
                clusterId: clusterId,
                bindedCluster: bindedCluster.name
            });
            const users: UserAdminResponse[] = usersData.content.map(p => p.user);
            const profiles: ClusterProfileAdminRequest[] = [];
            users.forEach(user =>{
                profiles.push({
                    userId: user.id,
                    maxSubmit: Number(formData.get("maxSubmit")),
                    maxTasks: Number(formData.get("maxJobs")),
                    maxTres: maxTres,
                    softLimit: Number(formData.get("softLimit")),
                    hardLimit: Number(formData.get("hardLimit")),
                    maxTaskLiveTime: Number(formData.get("maxTaskLiveTime"))
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
                bindedCluster: bindedCluster.name
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

    const filteredTres = filterTres(bindedCluster.tres);
    
    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form ref={formRef} className="form" onSubmit={(e) => e.preventDefault()}>
                <label>
                    Группа:
                    <select name="group" onFocus={handleFocus} required>
                        {groups.length !== 0 && groups.map((group, index) => (
                            <option key={index} value={group.id}>{group.name}</option>
                        ))}
                    </select>
                </label>
                <input type="number" name="maxSubmit" min={1} placeholder="Макс. кол-во задач в очереди..." required></input>
                <input type="number" name="maxJobs" min={1} placeholder="Макс. кол-во одновременно выполняемых задач..." required></input>
                {filteredTres.map(t => (
                    <input key={t.type} type="number" name={`maxTres_${t.type}`} min={0} placeholder={`Максимальное кол-во ${t.type}... (на кластере: ${bindedCluster.tres.find(val=>val.name === t.type)?.count??'?'})`} required></input>
                ))}
                <input type="number" name="maxNodes" min={1} placeholder="Максимальное кол-во узлов..." required></input>
                <input type="number" ref={softLimitRef} name="softLimit" min={1} placeholder={'Мягкий диск. лимит (МБ)'} 
                    onInvalid={(e) =>
                        (e.currentTarget as HTMLInputElement).setCustomValidity(
                            "Мягкий лимит должен быть меньше или равен жесткому"
                        )}
                    onInput={(e) => {
                        e.currentTarget.setCustomValidity("");
                    }}
                ></input>
                <input type="number" name="hardLimit" min={1} placeholder={'Жесткий диск. лимит (МБ)'}></input>
                <input type="number" name="maxTaskLiveTime" min={1} placeholder="Максимальное время выполнения задачи" required></input>
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => provideAccess()}>Предоставить доступ</button>
                </div>
                {isBusy && <LoadingScreen />}
            </form>
        </ModalForm>
    );
}

export default CreateGroupProfiles;