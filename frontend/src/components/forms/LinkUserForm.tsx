import "../../styles/components/form.css"

import { GroupAdminResponse, groupUsers } from "../../api/admin/groups";
import ModalForm from "../../pages/ModalForm";
import { useEffect, useRef, useState } from "react";
import { getGroups as apiGetGroups } from "../../api/admin/groups" 
import { ActionFormProps } from "../../utils/Interfaces";
import { getUsers, UserAdminResponse } from "../../api/admin/users";

const LinkUserForm: React.FC<ActionFormProps> = ({onSuccessCall,isOpen, onClose}) =>{
    const formRef = useRef<HTMLFormElement>(null);

    const [searchResult, setSearchResult] = useState<UserAdminResponse[]>([]);
    const [searchText, setSearchText] = useState<string>('');

    const [selectedUsers, setSelectedUsers] = useState<UserAdminResponse[]>([]);
    const [groups, setGroups] = useState<GroupAdminResponse[]>([]);

    const groupUser = async() =>{
        if (!formRef.current) {
            return;
        }
        if (selectedUsers.length === 0){
            alert("Нужно выбрать пользователей для группировки!");
        }
        const form = formRef.current;
        if(form.checkValidity()){
            const formData = new FormData(form);
            try { 
                await groupUsers(Number(
                    formData.get('group')), 
                    selectedUsers.map(item => item.id));
                onSuccessCall();
                onClose();
            } catch (error) {
                alert(error);
                console.log("Error!", error);
            }
        }else{
            form.reportValidity();
        }
    }

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
    const handleAdd = (item: UserAdminResponse) => {
        if (!selectedUsers.some(i => i.id === item.id)) {
            setSelectedUsers([...selectedUsers, item]);
            setSearchText('');
            setSearchResult([]);
        }
    };
    const removeItem = (id: number) => {
        setSelectedUsers(selectedUsers.filter(item => item.id !== id));
    };

    useEffect(() => {
        const fetchResults = async () => {
            if (searchText.length < 2) return setSearchResult([]);
            try {
                const data = (await getUsers(
                    0,
                    10,
                    null,
                    {
                        username: searchText,
                        ungrouped: true
                    }
                )).content;
                const filtered = data.filter(r => !selectedUsers.some(s => s.id === r.id));
                setSearchResult(filtered);
            } catch (error) {
                alert(error);
                console.log("Error!", error);
            }
        };
    
        const debounce = setTimeout(fetchResults, 300);
        return () => clearTimeout(debounce);
    }, [searchText, selectedUsers]);

    return (
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
                    
                <input type="text" placeholder="Поиск..." 
                    value={searchText} onChange={(e) => setSearchText(e.target.value)}/>

                {searchResult.length > 0 &&(
                    <ul>
                        {searchResult.map(item =>(
                            <li key={item.id} onClick={() => handleAdd(item)}>
                                {item.username}
                            </li>
                        ))}
                    </ul>
                ) }
                <ul>
                    {selectedUsers.map(item =>(
                        <li key={item.id}>
                            {item.username}
                            <button onClick={() => removeItem(item.id)}>✕</button>
                        </li>
                    ))}
                </ul>

                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => groupUser()}>Сгруппировать</button>
                </div>
            </form>
        </ModalForm>
    )
}

export default LinkUserForm;