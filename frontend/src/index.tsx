import ReactDOM from 'react-dom/client';
import App from './App';
import { AuthProvider } from './utils/AuthContext';
import "@xterm/xterm/css/xterm.css";
import React from 'react';


ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </React.StrictMode>
);