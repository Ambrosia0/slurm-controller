import React, { useRef, useState } from 'react';
import '../styles/login.css'
import '../styles/users.css'
import { useTranslation } from 'react-i18next'

type LoginProps = {
    loginMethod: (username: string, password: string) => Promise<void>;
};

const Login: React.FC<LoginProps> = ({loginMethod}) => {
    const { t } = useTranslation();
    const formRef = useRef<HTMLFormElement>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isError, setIsError] = useState<boolean>(false);

    const handleLogin = async (event: React.SubmitEvent<HTMLFormElement>) => {
        event.preventDefault();
        try {
            const formData = new FormData(event.currentTarget);
            const username = formData.get('username')?.toString().trim();
            const password = formData.get('password')?.toString();
            if(!username || !password)
                return;
            setIsError(false);
            setIsLoading(true);
            await loginMethod(username, password);
        } catch (error) {
            setIsError(true);
            console.error('Login error', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div id='login-container'>
            <form 
                ref={formRef} 
                id='form'
                onSubmit={handleLogin}
            >
                <h1 className='entry-title'>{t('login.title')}</h1>
                <input 
                    className={isError? 'login-input error-input': 'login-input'} 
                    title={t('login.username')} 
                    name='username' 
                    type="text"
                    autoComplete='username' 
                    placeholder={t('login.usernamePlaceholder')} 
                    required 
                />
                <input 
                    className={isError? 'login-input error-input': 'login-input'} 
                    title={t('login.password')} 
                    name='password' 
                    type="password" 
                    autoComplete='current-password'
                    placeholder={t('login.passwordPlaceholder')} 
                    required 
                />
                {isError &&
                    <div>
                        <div className='login-error' role='alert'>
                            {t('login.invalidCredentials')}
                        </div>
                    </div>
                }
                <div id='button-container'>
                    <button id='login-button' 
                        type='submit'
                        disabled={isLoading}
                    >
                        {isLoading? <span className='loading-progress'/>: t('login.button')}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Login;