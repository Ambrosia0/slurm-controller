import "../../styles/components/form.css"

import { GroupAdminResponse, updateGroup } from "../../api/admin/groups";
import ModalForm from "../../pages/ModalForm";
import { useRef } from "react";

interface GroupRenameProps{
    isOpen: boolean,
    onClose: () => void;
    group: GroupAdminResponse;
}

const GroupRenameForm: React.FC<GroupRenameProps> = ({isOpen, onClose, group}) =>{
    const formRef = useRef<HTMLFormElement>(null);

    const renameGroup = async() =>{
        if (!formRef.current) {
            return;
        }
        const form = formRef.current;
        if(form.checkValidity()){
            const formData = new FormData(form);
            try {
                const name = formData.get('groupName');
                if (typeof name !== 'string') {
                    throw new Error('Invalid form data: name is required.');
                }
                  
                const data = await updateGroup(
                    group.id,
                    name
                );
                group.name = data.name;
                onClose();
            } catch (error) {
                alert(error);
                console.log("Error!", error);
            }
        }else{
            form.reportValidity();
        }
    }

    return (
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form className="form" ref={formRef} onSubmit={(e) => e.preventDefault()}>
                <input name="groupName" type="text"  pattern='^[a-zA-Z0-9._\-]{3,32}$' placeholder="Введите название..." 
                onInvalid={(e) =>
                    (e.currentTarget as HTMLInputElement).setCustomValidity(
                        "Название группы должно содержать минимум 3-32 символа, допустимые спецсимволы: . _ -."
                    )}
                onInput={(e) => {
                    e.currentTarget.setCustomValidity("");
                }} required></input>
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => renameGroup()}>Переименовать</button>
                </div>
            </form>
        </ModalForm>
    )
}

export default GroupRenameForm;