import "../../styles/components/form.css"

import { useEffect, useRef, useState } from "react";
import { createProfile, ClusterProfileAdminRequest } from "../../api/admin/profiles";
import RandExp from "randexp";
import ModalForm from "../../pages/ModalForm";
import { getUsers, UserAdminResponse } from "../../api/admin/users";
import { filterTres, ProfileActionFormProps} from "../../utils/Interfaces";

const CreateProfile: React.FC<ProfileActionFormProps> = ({clusterId, bindedCluster, isOpen, onSuccessCall, onClose}) =>{
    const formRef = useRef<HTMLFormElement>(null);

    const [searchResult, setSearchResult] = useState<UserAdminResponse[]>([]);
    const [searchText, setSearchText] = useState<string>('');

    const [selectedUsers, setSelectedUsers] = useState<UserAdminResponse[]>([]);
    const softLimitRef = useRef<HTMLInputElement>(null);

    const provideAccess = async () =>{
        if(selectedUsers.length === 0 || !formRef.current)
            return;
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

        const profiles: ClusterProfileAdminRequest[] = [];
        const usernameExp = new RandExp('^[a-zA-Z0-9._-]{8,16}$')
        selectedUsers.forEach(user => {
            profiles.push({
                userId: user.id,
                maxSubmit: Number(formData.get("maxSubmit")),
                maxTasks: Number(formData.get("maxJobs")),
                maxTres: maxTres,
                softLimit: Number(formData.get("softLimit")),
                hardLimit: Number(formData.get("hardLimit")),
                maxTaskLiveTime: Number(formData.get("maxTaskLiveTime"))
            })
        })
        try {
            await createProfile(clusterId, bindedCluster.name, profiles);
            onSuccessCall();
            onClose();   
        } catch (error) {
            alert(error);
            console.log("Error!", error);
        }
    }

    useEffect(() => {
        const fetchResults = async () => {
            if (searchText.length < 2) return setSearchResult([]);
            try {
                const data = (await getUsers(
                        0,
                        10,
                        null,
                        {
                            clusterId: clusterId,
                            bindedCluster: bindedCluster.name,
                            notInCluster: true
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


    const filteredTres = filterTres(bindedCluster.tres);

    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form className="form" ref={formRef} onSubmit={(e) => e.preventDefault()}>

                <input type="text" placeholder="Поиск..."
                    value={searchText} onChange={(e) => setSearchText(e.target.value)} />

                {searchResult.length > 0 && (
                    <ul>
                        {searchResult.map(item => (
                            <li key={item.id} onClick={() => handleAdd(item)}>
                                {item.username}
                            </li>
                        ))}
                    </ul>
                )}
                <ul>
                    {selectedUsers.map(item => (
                        <li key={item.id}>
                            {item.username}
                            <button onClick={() => removeItem(item.id)}>✕</button>
                        </li>
                    ))}
                </ul>
                <input type="number" name="maxSubmit" min={1} placeholder="Макс. кол-во задач в очереди..." required></input>
                <input type="number" name="maxJobs" min={1} placeholder="Макс. кол-во одновременно выполняемых задач..." required></input>
                {filteredTres.map(t => (
                    <input key={t.type} type="number" name={`maxTres_${t.type}`} min={0} placeholder={`Максимальное кол-во ${t.type}... (на кластере: ${bindedCluster.tres.find(val=>val.type === t.type)?.count??'?'})`} required></input>
                ))}
                <input type="number" ref={softLimitRef} name="softLimit" min={1} placeholder={'Мягкий лимит на дисковое пространство (можно превысить на время) (в МБ)'} 
                    onInvalid={(e) =>
                        (e.currentTarget as HTMLInputElement).setCustomValidity(
                            "Мягкий лимит должен быть меньше или равен жесткому"
                        )}
                    onInput={(e) => {
                        e.currentTarget.setCustomValidity("");
                    }}
                    ></input>
                <input type="number" name="hardLimit" min={1} placeholder={'Жесткий лимит на дисковое пространство (нельзя превысить) (в МБ)'}></input>
                <input type="number" name="maxTaskLiveTime" min={1} placeholder="Максимальное время выполнения задачи" required></input>
                <div>
                    <button onClick={onClose}>Назад</button>
                    <button onClick={() => provideAccess()}>Предоставить доступ</button>
                </div>
            </form>
        </ModalForm>
    );
}

export default CreateProfile;