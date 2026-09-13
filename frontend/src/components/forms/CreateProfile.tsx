import "../../styles/components/form.css"

import { useEffect, useRef, useState } from "react";
import { createProfile, ClusterProfileAdminRequest } from "../../api/admin/profiles";
import RandExp from "randexp";
import ModalForm from "../../pages/ModalForm";
import { getUsers, UserAdminResponse } from "../../api/admin/users";
import {ProfileActionFormProps} from "../../utils/Interfaces";
import { useTranslation } from 'react-i18next'
import ProfileForm from "./components/ProfileForm";
import { normalizeConstraint } from "../../utils/utils";

const CreateProfile: React.FC<ProfileActionFormProps> = ({clusterId, bindedCluster, isOpen, onSuccessCall, onClose}) =>{
    const formRef = useRef<HTMLFormElement>(null);
    const { t } = useTranslation();

    const [searchResult, setSearchResult] = useState<UserAdminResponse[]>([]);
    const [searchText, setSearchText] = useState<string>('');

    const [selectedUsers, setSelectedUsers] = useState<UserAdminResponse[]>([]);
    const softLimitRef = useRef<HTMLInputElement>(null);

    const [request, setRequest] = useState<Omit<ClusterProfileAdminRequest, 'userId'>>({
        hardLimit: null,
        maxSubmit: null,
        maxTaskLiveTime: null,
        maxTasks: null,
        maxTres: [],
        softLimit: null
    });

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
            return;
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

        const normalized = normalizeConstraint(request);
        const profiles: ClusterProfileAdminRequest[] = [];
        selectedUsers.forEach(user => {
            profiles.push({
                ...normalized,
                userId: user.id
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

    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form className="form" ref={formRef} onSubmit={(e) => e.preventDefault()}>

                <input type="text" placeholder={t('forms.search')}
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
            </form>
        </ModalForm>
    );
}

export default CreateProfile;