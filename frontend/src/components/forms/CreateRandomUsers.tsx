import "../../styles/components/form.css"

import { useRef, useState } from "react";
import ModalForm from "../../pages/ModalForm";
import RandExp from "randexp";
import { importUsersFromJson } from "../../api/admin/users";
import { ActionFormProps } from "../../utils/Interfaces";

interface UserCreationProps{
    username: string,
    password: string,
    group: GroupProps|null
}

interface GroupProps{
    name: string
}

const CreateRandomUsers: React.FC<ActionFormProps> = ({onClose, onSuccessCall, isOpen}) =>{
    const [isGrouped, setIsGrouped] = useState<boolean>(false);
    const formRef = useRef<HTMLFormElement>(null);

    const generateUsers = async () =>{
        const form = formRef.current;
        if(!form)
            return;
        if(!form.checkValidity()){
            form.reportValidity();
            return;
        }
        const formData = new FormData(form);
        const generatedUsers: UserCreationProps[] = [];
        const prefix = String(formData.get("prefix"));
        const group: GroupProps|null = String(formData.get("group"))? {name: String(formData.get("group"))}: null
        console.log(group);
        const randUsername = new RandExp("^[a-zA-Z0-9._\-]{8,24}$");
        const randPassword = new RandExp("^[A-Za-z0-9!@#$&*]{12,20}$")
        while(generatedUsers.length !== Number(formData.get('numberOfUsers'))){
            generatedUsers.push({
                username: prefix + randUsername.gen(),
                password: randPassword.gen(),
                group: isGrouped? group: null
            })
        }
        console.log(generatedUsers);
        const jsonBlob = new Blob([JSON.stringify(generatedUsers)], {type: "application/json"});
        const formDataToSend = new FormData();
        formDataToSend.append("file", jsonBlob, "data.json");
        await importUsersFromJson(formDataToSend);
        onSuccessCall();
        onClose();
    }

    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form ref={formRef} className="form" onSubmit={(e) => e.preventDefault()}>
                <input type="text" name="prefix" pattern='[a-zA-Z0-9._\-]{3,32}$' 
                 placeholder="Префикс (опционально)..." 
                    onInvalid={(e) =>
                        (e.currentTarget as HTMLInputElement).setCustomValidity(
                            "Допустимые спецсимволы: . _ -."
                        )}
                    onInput={(e) => {
                        e.currentTarget.setCustomValidity("");
                    }} required></input>
                <input type="number" min={1} max={1000} name="numberOfUsers" placeholder="Количество пользователей..." required></input>
                <label>
                <input type="checkbox" checked={isGrouped} onChange={(e) => setIsGrouped(e.target.checked)}></input>
                    Сгруппировать?
                </label>
                {isGrouped === true && <input type="text" name="group"  placeholder="Название группы..."></input>}
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={()=>generateUsers()}>Создать</button>
                </div>
            </form>
        </ModalForm>
    )
}

export default CreateRandomUsers;