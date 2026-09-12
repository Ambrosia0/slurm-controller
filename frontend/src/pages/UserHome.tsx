import '../styles/adminHome.css';

import { useEffect, useState } from 'react';
import { BindedClusterResponse, ClusterUserResponse } from '../api/user/userApi';
import UserItems from '../components/user/UserItems';
import ProfileTerminal from '../components/user/ProfileTerminal'
import Queue from '../components/user/Queue';
import ProfileInfo from '../components/user/ProfileInfo';
import { LogoutProp } from '../utils/Interfaces';

const UserHome: React.FC<LogoutProp> = ({logout}) =>{
    type ControlOption = "clusters" | "info";
    type DisplayOption = "queue" | "terminal" | "info" | null;

        const [selectedControlOption, setSelectedControlOption] = useState<ControlOption>();
        const [selectedCluster, setSelectedCluster] = useState<ClusterUserResponse | null>(null);
        const [selectedBindedCluster, setSelectedBindedCluster] = useState<BindedClusterResponse | null>(null);
        const [selectedDisplayOption, setSelectedDisplayOption] = useState<DisplayOption>(null);

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


    const handleClusterChange = (cluster: ClusterUserResponse|null) =>{
        setSelectedDisplayOption(null);
        setSelectedCluster(cluster)
    }



    const renderDisplayOption = () => {
        if (!selectedCluster || !selectedBindedCluster) {
            alert('Не выбран кластер!');
            return;
        }
        switch (selectedDisplayOption) {
            case 'queue':
                return <Queue cluster={selectedCluster} bindedCluster={selectedBindedCluster} />
            case 'terminal':
                return <ProfileTerminal cluster={selectedCluster} bindedCluster={selectedBindedCluster} />
            case 'info':
                return <ProfileInfo cluster={selectedCluster} bindedCluster={selectedBindedCluster} />
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


    return(
        <div id="home-container">
            <div id="control-panel-container">
                <div id='control-panel-upper'>
                    <label className='custom-radio' htmlFor='clusters'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('clusters')} />
                        <span id="cluster-img"></span>
                        Кластеры
                    </label>
                </div>
                <div id='control-panel-lower'>
                    <button id='logout-button' onClick={logout}>
                        <span className='button-text'>Выход</span>
                    </button>
                </div>
            </div>
            <div id="right-panel-container">
                {selectedControlOption === "clusters" && (
                    <>
                        <UserItems setSelectedCluster={handleClusterChange} selectedCluster={selectedCluster} 
                            setSelectedBindedCluster={setSelectedBindedCluster} selectedBindedCluster={selectedBindedCluster} /> 
                        {selectedCluster !== null && selectedBindedCluster !== null && (
                            <div id="control-tabs-container">
                                <div id="control-tabs-options">
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('queue')} />
                                        <span id="queue-img"></span>
                                        Очередь
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('terminal')} />
                                        <span id="clutser-terminal-img"></span>
                                        Терминал
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('info')} />
                                        <span id="clutser-terminal-img"></span>
                                        Информация о профиле
                                    </label>
                                </div>
                                <div id="control-tabs-option-render-container">
                                    {renderDisplayOption()}
                                </div>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

export default UserHome;