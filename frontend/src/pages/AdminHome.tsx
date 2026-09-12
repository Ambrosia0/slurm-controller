import '../styles/adminHome.css';

import React, { useEffect, useState } from 'react';
import ClusterForm from "../components/forms/ClusterForm";
import Profiles from '../components/admin/Profiles';
import Items from "../components/admin/Items";
import Queue from "../components/admin/Queue";
import Users from "../components/admin/Users";
import ClusterTerminal from '../components/admin/ClusterTerminal';
import ServerTerminal from '../components/admin/ServerTerminal';
import { ClusterAdminResponse, SlurmClusterRec } from '../api/admin/clusters';
import { LogoutProp } from '../utils/Interfaces';
import StatisticsDisplay from '../components/admin/Statistics';


const AdminHome: React.FC<LogoutProp> = ({ logout }) => {
    type ControlOption = "clusters" | "terminal" | "users";
    type DisplayOption = "statistics" | "queue" | "profiles" | "terminal" | null;

    const [selectedControlOption, setSelectedControlOption] = useState<ControlOption>();
    const [selectedCluster, setSelectedCluster] = useState<ClusterAdminResponse | null>(null);
    const [selectedBindedCluster, setSelectedBindedCluster] = useState<SlurmClusterRec | null>(null);
    const [selectedDisplayOption, setSelectedDisplayOption] = useState<DisplayOption>(null);

    const [showClusterForm, setShowClusterForm] = useState<boolean>(false);

    const handleControlOptionChange = (option: ControlOption) => {
        document.querySelector(`label[for=${selectedControlOption}]`)?.classList.remove('active');
        document.querySelector(`label[for=${option}]`)?.classList.add('active');
        setSelectedCluster(null);
        setSelectedDisplayOption(null);
        setSelectedControlOption(option);
    };

    const handleDisplayOptionChange = (option: DisplayOption) => {
        setSelectedDisplayOption(option);
    };

    const handleClusterChange = (cluster: ClusterAdminResponse|null) =>{
        setSelectedDisplayOption(null);
        setSelectedCluster(cluster)
    }


    const renderDisplayOption = () => {
        if (!selectedCluster || !selectedBindedCluster) {
            alert('Не выбран кластер!');
            return;
        }
        switch (selectedDisplayOption) {
            case 'statistics':
                return <StatisticsDisplay clusterId={selectedCluster.id} bindedCluster={selectedBindedCluster.name} />
            case 'queue':
                return <Queue bindedCluster={selectedBindedCluster} clusterId={selectedCluster.id} />
            case 'profiles':
                return <Profiles clusterId={selectedCluster.id} bindedCluster={selectedBindedCluster}/>
            case 'terminal':
                return <ClusterTerminal clusterId={selectedCluster.id} bindedCluster={selectedBindedCluster.name}/>
        }
    }

    useEffect(() => {
        if (selectedControlOption == null) 
            return;
        document.querySelectorAll('#control-panel-upper').forEach((label) =>
            label.classList.remove('active')
        );
        const activeLabel = document.querySelector(`label[for=${selectedControlOption}]`);
        activeLabel?.classList.add('active');
    }, [selectedControlOption]);


    return (
        <div id="home-container">
            {showClusterForm && <ClusterForm onClose={() => setShowClusterForm(false)} isOpen={showClusterForm} />}
            
            <div id="control-panel-container">
                <div id='control-panel-upper'>
                    <label className='custom-radio' htmlFor='clusters'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('clusters')} />
                        <span id="cluster-img"></span>
                        Кластеры
                    </label>
                    <label className='custom-radio' htmlFor='terminal'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('terminal')} />
                        <span id="terminal-img"></span>
                        Терминал сервера
                    </label>
                    <label className='custom-radio' htmlFor='users'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('users')} />
                        <span id="users-img"></span>
                        Пользователи
                    </label>
                </div>
                <div id='control-panel-lower'>
                    <button id='add-cluster-button' onClick={() => setShowClusterForm(true)}>
                        <span className='button-text'>Добавить кластер</span>
                    </button>
                    <button id='logout-button' onClick={logout}>
                        <span className='button-text'>Выход</span>
                    </button>
                </div>
            </div>


            <div id="right-panel-container">
                {selectedControlOption === "clusters" && (
                    <>
                        <Items setSelectedCluster={handleClusterChange} selectedCluster={selectedCluster} 
                            setSelectedBindedCluster={setSelectedBindedCluster} selectedBindedCluster={selectedBindedCluster} /> 
                        {selectedCluster !== null && selectedBindedCluster !== null && (
                            <div id="control-tabs-container">
                                <div id="control-tabs-options">
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('statistics')} />
                                        <span id="statistics-img"></span>
                                        Статистика
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('queue')} />
                                        <span id="queue-img"></span>
                                        Очередь
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('profiles')} />
                                        <span id="profiles-img"></span>
                                        Профили
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('terminal')} />
                                        <span id="clutser-terminal-img"></span>
                                        Терминал
                                    </label>
                                </div>
                                <div id="control-tabs-option-render-container">
                                    {renderDisplayOption()}
                                </div>
                            </div>
                        )}
                    </>
                )}
                {selectedControlOption === 'terminal' && <ServerTerminal />}
                {selectedControlOption === 'users' && <Users />}

            </div>
        </div>
    );
};

export default AdminHome;