import '../../styles/components/form.css'
import { useRef, useState } from "react"
import ModalForm from "../../pages/ModalForm"
import {deleteGroup as apiDeleteGroup, getGroups as apiGetGroups, GroupAdminResponse} from "../../api/admin/groups" 
import { ActionFormProps } from '../../utils/Interfaces'



const DeleteGroupForm: React.FC<ActionFormProps> = ({isOpen, onClose, onSuccessCall}) =>{
    const formRef = useRef<HTMLFormElement>(null);
    const [isChecked, setIsChecked] = useState<boolean>(false);
    const [groups, setGroups] = useState<GroupAdminResponse[]>([]);

    const deleteGroup = async () => {
        const form = formRef.current;
        if (!form) return;
    
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }
    
        const formData = new FormData(form);
        const groupId = parseInt(formData.get('group') as string, 10);
    
        if (isNaN(groupId)) {
            alert('Некорректный ID группы');
            return;
        }
    
        try {
            await apiDeleteGroup(groupId, isChecked);
            onSuccessCall();
            onClose();
        } catch (error) {
            alert(error);
            console.error("Error!", error);
        }
    };

    const getGroups = async() =>{
        try {
            const data = (await apiGetGroups(
                0,
                40,
                null
            )).content;
            setGroups(data);
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
                    Группа:
                    <select name="group" onFocus={handleFocus} required>
                        {groups.length !== 0 && groups.map((group, index) => (
                            <option key={index} value={group.id}>{group.name}</option>
                        ))}
                    </select>
                </label>
                <label>
                    <input type="checkbox" checked={isChecked} onChange={(e) => setIsChecked(e.target.checked)}></input>
                    Удалить связанных пользователей?
                </label>
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => deleteGroup()}>Удалить</button>
                </div>
            </form>
        </ModalForm>
    )
}

export default DeleteGroupForm;