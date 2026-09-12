import "../../styles/components/addForm.css"

import { useEffect, useRef, useState } from "react";
import apiClient from "../../utils/axios";
import ModalForm from "../../pages/ModalForm";
import { FormProps } from "../../utils/Interfaces";

const ClusterForm: React.FC<FormProps> = ({isOpen, onClose}) =>{
    const formRef = useRef<HTMLFormElement>(null);

    const [schedulers, setSchedulers] = useState<string[]>([]);
    
    const addCluster = async () => {
        if (!formRef.current) {
            return;
        }
        const form = formRef.current;
        if(form.checkValidity()){
            const formData = new FormData(form);
            try {
                await apiClient.post('/api/admin/cluster', 
                    {
                        host: formData.get('host'), 
                        username: formData.get('username'),
                        password: formData.get('password'),
                        displayedName: formData.get('displayedName'),
                        daemonPort: formData.get('daemonPort')?
                                Number(formData.get('daemonPort')):
                                null,
                        schema: formData.get('schema'),
                        sshPort: formData.get('sshPort')?
                            Number(formData.get('sshPort')):
                            null,
                        taskScheduler: formData.get('taskScheduler')})
                onClose();
            } catch (error: any) {
                const message =
                    error?.response?.data?.body?.detail ||
                    error?.response?.data?.error ||
                    error?.message ||
                    'Неизвестная ошибка';
                alert('Ошибка при добавлении кластера: ' + message);
            }
        }else{
            form.reportValidity();
        }
    }

    const getSupportedSchedulers = async(): Promise<void> => {
        try{
            const resp = await apiClient.get<string[]>('/api/admin/cluster/versions');
            setSchedulers(Object.values(resp.data));
        } catch (error){
            alert('Ошибка!');
        }
    }

    const handleFocus = () =>{
        if(schedulers.length ===0){
            getSupportedSchedulers();
        }
    }

    useEffect(() =>{
        return() => {
            setSchedulers([]);
        }
    },[])


    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form ref={formRef} className="add-form" onSubmit={(e) => e.preventDefault()}>
            <select name="schema">
                <option value="HTTP">HTTP</option>
                <option value="HTTPS">HTTPS</option>
            </select>

            <input
                type="text"
                name="host"
                placeholder="Hostname"
                required
            />

            <input
                type="text"
                name="username"
                placeholder="Username"
                required
            />

            <input
                type="password"
                name="password"
                placeholder="Пароль (опционально, если по ключам)"
            />

            <input
                type="text"
                name="displayedName"
                placeholder="Отображаемое имя"
                required
            />

            <input
                type="number"
                name="sshPort"
                placeholder="SSH-порт (22 по умолчанию)"
                min="1"
                max="65535"
            />

            <input
                type="number"
                name="daemonPort"
                placeholder="Порт slurmrestd (6820 по умолчанию)"
                min="1"
                max="65535"
            />

            <label>
                Планировщик:
                <select name="taskScheduler" onClick={handleFocus} required>
                    {schedulers.map((name) => (
                        <option key={name} value={name}>
                            {name}
                        </option>
                    ))}
                </select>
            </label>
                <div>
                    <button type="button" onClick={() => addCluster()}>Добавить</button>
                    <button type="button" onClick={onClose}>Назад</button>
                </div>
            </form>
        </ModalForm>
    )
}
export default ClusterForm;