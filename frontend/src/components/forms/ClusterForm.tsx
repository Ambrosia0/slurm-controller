import "../../styles/components/addForm.css"

import { useEffect, useRef, useState } from "react";
import apiClient from "../../utils/axios";
import ModalForm from "../../pages/ModalForm";
import { FormProps } from "../../utils/Interfaces";
import { ClusterAdminRequest, ClusterAdminResponse } from "../../api/admin/clusters";
import { addCluster as apiAddCluster } from "../../api/admin/clusters"
import { useTranslation } from 'react-i18next'

const ClusterForm: React.FC<FormProps> = ({
    isOpen, 
    onClose
}) =>{
    const formRef = useRef<HTMLFormElement>(null);
    const { t } = useTranslation();

    const [schedulers, setSchedulers] = useState<string[]>([]);
    
    const addCluster = async () => {
        if (!formRef.current) {
            return;
        }
        const form = formRef.current;
        if(form.checkValidity()){
            const formData = new FormData(form);
            const clusterData: ClusterAdminRequest = {
                host: String(formData.get('host')), 
                username: String(formData.get('username')),
                password: String(formData.get('password')),
                displayedName: String(formData.get('displayedName')),
                daemonPort: formData.get('daemonPort')?
                        Number(formData.get('daemonPort')):
                        undefined,
                schema: String(formData.get('schema')),
                sshPort: formData.get('sshPort')?
                    Number(formData.get('sshPort')):
                    undefined,
                taskScheduler: String(formData.get('taskScheduler'))
            }
            try {
                const resp = (await apiAddCluster(clusterData));
                document.dispatchEvent(
                    new CustomEvent<ClusterAdminResponse>('clusterCreated', {
                        detail: resp
                    })
                );
                onClose();
            } catch (error: any) {
                const message =
                    error?.response?.data?.body?.detail ||
                    error?.response?.data?.error ||
                    error?.message ||
                    'Неизвестная ошибка';
                alert(t('clusterForm.addClusterError') + message);
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
            alert(t('clusterForm.error'));
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
                <option value="HTTP">{t('clusterForm.schema')}</option>
                <option value="HTTPS">{t('clusterForm.schema')}</option>
            </select>

            <input
                type="text"
                name="host"
                placeholder={t('clusterForm.host')}
                required
            />

            <input
                type="text"
                name="username"
                placeholder={t('clusterForm.username')}
                required
            />

            <input
                type="password"
                name="password"
                placeholder={t('clusterForm.password')}
            />

            <input
                type="text"
                name="displayedName"
                placeholder={t('clusterForm.displayedName')}
                required
            />

            <input
                type="number"
                name="sshPort"
                placeholder={t('clusterForm.sshPort')}
                min="1"
                max="65535"
            />

            <input
                type="number"
                name="daemonPort"
                placeholder={t('clusterForm.daemonPort')}
                min="1"
                max="65535"
            />

            <label>
                {t('clusterForm.scheduler')}:
                <select name="taskScheduler" onClick={handleFocus} required>
                    {schedulers.map((name) => (
                        <option key={name} value={name}>
                            {name}
                        </option>
                    ))}
                </select>
            </label>
                <div className="form-action">
                    <button type="button" onClick={() => addCluster()}>{t('clusterForm.add')}</button>
                    <button type="button" onClick={onClose}>{t('clusterForm.back')}</button>
                </div>
            </form>
        </ModalForm>
    )
}
export default ClusterForm;