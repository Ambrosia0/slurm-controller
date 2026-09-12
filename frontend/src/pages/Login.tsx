import React, { useRef } from 'react';
import '../styles/login.css'

type LoginProps = {
    loginMethod: (username: string, password: string) => Promise<void>;
};

const Login: React.FC<LoginProps> = ({loginMethod}) => {
    const formRef = useRef<HTMLFormElement>(null);

    const handleLogin = async () => {
        if (!formRef.current) {
            return;
        }
        const form = formRef.current;
        try {
            if(form.checkValidity()){
                const formData = new FormData(form);
                loginMethod(
                    formData.get('username') as string, 
                    formData.get('password') as string);
            }else{
                form.reportValidity();
            }
        } catch (error) {
            alert('Неверный логин или пароль!');
            console.error('Login error', error);
        }
    };

    return (
        <div id='login-container'>
            <form ref={formRef} id='form' onSubmit={(e) => e.preventDefault()}>
                <h1 className='entry-title'>Login</h1>
                <input className='login-input' title='Username' name='username' type="text" placeholder='Username...' required />
                <input className='login-input' title='Password' name='password' type="password" placeholder='Password...' required />
                <div id='button-container'>
                    <button id='login-button' onClick={() => handleLogin()}>Войти</button>
                </div>
            </form>
        </div>
    );
};

export default Login;