import '../../styles/components/form.css'

import { useRef } from "react";
import { createGroup as apiCreateGroup } from "../../api/admin/groups";
import ModalForm from "../../pages/ModalForm";
import { ActionFormProps } from '../../utils/Interfaces';
import { useTranslation } from 'react-i18next'

const CreateGroupForm: React.FC<ActionFormProps> = ({isOpen, onClose, onSuccessCall}) =>{
    const formRef = useRef<HTMLFormElement>(null);
    const { t } = useTranslation();

    const createGroup = async () => {
        const form = formRef.current;
        if (!form) return;

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }
    
        try {
            const formData = new FormData(form);
            await apiCreateGroup(String(formData.get('name')));
            onSuccessCall();
            onClose();
        } catch (error) {
            alert(error);
            console.error("Error!", error);
        }
    };
    return(
        <ModalForm isOpen={isOpen} onClose={onClose}>
            <form ref={formRef} className="form" onSubmit={(e) => e.preventDefault()}>
                <input type='text' name='name' pattern='^[a-zA-Z0-9._\-]{3,32}$' placeholder={t('forms.name')}
                    onInvalid={(e) =>
                        (e.currentTarget as HTMLInputElement).setCustomValidity(
                            t('forms.nameValidation')
                        )} 
                    onInput={(e) => (e.currentTarget as HTMLInputElement).setCustomValidity('')}
                    required></input>
                <div className="form-action">
                    <button onClick={onClose}>{t('common.back')}</button>
                    <button onClick={() => createGroup()}>{t('common.create')}</button>
                </div>
            </form>
        </ModalForm>
    )
}
export default CreateGroupForm;