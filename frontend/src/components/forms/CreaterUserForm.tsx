import '../../styles/components/form.css';

import ModalForm from '../../pages/ModalForm';
import { useRef, useState } from 'react';
import {createUser as apiCreateUser} from '../../api/admin/users'
import { getGroups as apiGetGroups, GroupAdminResponse } from '../../api/admin/groups';
import { ActionFormProps } from '../../utils/Interfaces';



const CreateUserForm: React.FC<ActionFormProps> = ({
    onSuccessCall, 
    isOpen, 
    onClose
}) =>{
    const formRef = useRef<HTMLFormElement>(null);
    const [groups, setGroups] = useState<GroupAdminResponse[]>([]);

    const createUser = async() =>{
        if (!formRef.current) {
            return;
        }
        const form = formRef.current;
        if(form.checkValidity()){
            const formData = new FormData(form);
            try {
                await apiCreateUser({
                    username: String(formData.get('username')),
                    password: String(formData.get('password')),
                    groupId: Number(formData.get('group')) || null
                });
                onSuccessCall();
                onClose();
            } catch (error) {
                alert(error);
                console.log("Error!", error)
            }
        }else{
            form.reportValidity();
        }
    }

    const getGroups = async() =>{
        try {
            const data = (await apiGetGroups(
                0,
                20,
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
            <form ref={formRef} className='form' onSubmit={(e) => {e.preventDefault()}}>
                <input type='text' name='username' pattern='^[a-zA-Z0-9._\-]{8,16}$' placeholder='Имя пользователя...'
                    onInvalid={(e) =>
                        (e.currentTarget as HTMLInputElement).setCustomValidity(
                            "Имя пользователя должно содержать минимум 3-32 символа, допустимые спецсимволы: . _ -."
                        )}
                    onInput={(e) => {
                        e.currentTarget.setCustomValidity("");
                    }} required></input>
                <input type='password' name='password' pattern="^[A-Za-z0-9!@#$&*]{12,255}$"
                     placeholder='Пароль...'
                     onInvalid={(e) =>
                        (e.currentTarget as HTMLInputElement).setCustomValidity(
                          "[A-Za-z0-9!@#$&*]{12,255}"
                        )}
                    onInput={(e) => {
                        e.currentTarget.setCustomValidity("");
                        }}                        
                    required></input>
                <label>
                    Группа:
                    <select name="group" onFocus={handleFocus}>
                        <option value="">Нет</option>
                        {groups.map((group, index) => (
                            <option key={index} value={group.id}>{group.name}</option>
                        ))}
                    </select>
                </label>
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => createUser()}>Создать</button>
                </div>
            </form>
        </ModalForm>
    )
}

export default CreateUserForm;