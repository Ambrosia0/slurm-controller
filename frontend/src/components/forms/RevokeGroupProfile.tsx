import "../../styles/components/form.css"

import { useRef, useState } from "react";
import ModalForm from "../../pages/ModalForm";
import { revokeAccessFromGroup } from "../../api/admin/profiles";
import LoadingScreen from "../LoadingScreen";
import { getGroups as apiGetGroups, GroupAdminResponse } from "../../api/admin/groups";
import { ProfileActionFormProps } from "../../utils/Interfaces";


const RevokeGroupProfile: React.FC<ProfileActionFormProps> = ({
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

    const revokeAccess = async () =>{
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
            await revokeAccessFromGroup(clusterId, Number(formData.get('group')), bindedCluster.name)
            onSuccessCall()
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

    const handleFocus = () => {
        getGroups();
    }
    
    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form className="form" ref={formRef} onSubmit={(e) => e.preventDefault()}>
                <label>
                    Группа:
                    <select name="group" onFocus={handleFocus} required>
                        {groups.length !== 0 && groups.map((group, index) => (
                            <option key={index} value={group.id}>{group.name}</option>
                        ))}
                    </select>
                </label>
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => revokeAccess()}>Отозвать доступ</button>
                </div>
                {isBusy && <LoadingScreen />}
            </form>
        </ModalForm>
    );
}

export default RevokeGroupProfile;