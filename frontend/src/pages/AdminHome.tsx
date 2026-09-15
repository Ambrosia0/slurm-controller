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
import { useTranslation } from 'react-i18next'
import { LanguageChange } from '../components/LanguageChange';


const AdminHome: React.FC<LogoutProp> = ({ logout }) => {
    type ControlOption = "clusters" | "terminal" | "users";
    type DisplayOption = "statistics" | "queue" | "profiles" | "terminal" | null;

    const { t } = useTranslation();
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
            alert(t('cluster.notChoose'));
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
            {showClusterForm && 
                <ClusterForm 
                    onClose={() => setShowClusterForm(false)} 
                    isOpen={showClusterForm} 
                />
            }
            
            <div id="control-panel-container">
                <div id='control-panel-upper'>
                    <label className='custom-radio' htmlFor='clusters'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('clusters')} />
                        <span id="cluster-img"></span>
                        {t("menu.clusters")}
                    </label>
                    <label className='custom-radio' htmlFor='terminal'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('terminal')} />
                        <span id="terminal-img"></span>
                        {t("menu.serverTerminal")}
                    </label>
                    <label className='custom-radio' htmlFor='users'>
                        <input type="radio" name='controlOption' onChange={() => handleControlOptionChange('users')} />
                        <span id="users-img"></span>
                        {t("menu.users")}
                    </label>
                </div>
                <div id='control-panel-lower'>
                    <LanguageChange />
                    <button id='add-cluster-button' onClick={() => setShowClusterForm(true)}>
                        <svg
                            width="30"
                            height="30"
                            viewBox="0 0 24 24"
                            fill="none"
                            xmlns="http://www.w3.org/2000/svg"
                        >
                            <path
                                d="M15 12L12 12M12 12L9 12M12 12L12 9M12 12L12 15"
                                stroke="#FFFFFF"
                                strokeWidth="1.5"
                                strokeLinecap="round"
                            />
                            <path
                                d="M22 12C22 16.714 22 19.0711 20.5355 20.5355C19.0711 22 16.714 22 12 22C7.28595 22 4.92893 22 3.46447 20.5355C2 19.0711 2 16.714 2 12C2 7.28595 2 4.92893 3.46447 3.46447C4.92893 2 7.28595 2 12 2C16.714 2 19.0711 2 20.5355 3.46447C21.5093 4.43821 21.8356 5.80655 21.9449 8"
                                stroke="#FFFFFF"
                                strokeWidth="1.5"
                                strokeLinecap="round"
                            />
                        </svg>
                        <span className='button-text'>{t("menu.addCluster")}</span>
                    </button>
                    <button id='logout-button' onClick={logout}>
                        <svg
                            className="control-button-icon"
                            width="30"
                            height="30"
                            viewBox="0 0 24 24"
                            fill="none"
                            xmlns="http://www.w3.org/2000/svg"
                        >
                            <path
                                d="M15 12L6 12M6 12L8 14M6 12L8 10"
                                stroke="currentColor"
                                strokeWidth="1.5"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />

                            <path
                                d="M12 21.9827C10.4465 21.9359 9.51995 21.7626 8.87865 21.1213C8.11027 20.3529 8.01382 19.175 8.00171 17M16 21.9983C18.175 21.9862 19.3529 21.8897 20.1213 21.1213C21 20.2426 21 18.8284 21 16V14V10V8C21 5.17157 21 3.75736 20.1213 2.87868C19.2426 2 17.8284 2 15 2H14C11.1715 2 9.75733 2 8.87865 2.87868C8.11027 3.64706 8.01382 4.82497 8.00171 7"
                                stroke="currentColor"
                                strokeWidth="1.5"
                                strokeLinecap="round"
                            />

                            <path
                                d="M3 9.5V14.5C3 16.857 3 18.0355 3.73223 18.7678C4.46447 19.5 5.64298 19.5 8 19.5M3.73223 5.23223C4.46447 4.5 5.64298 4.5 8 4.5"
                                stroke="currentColor"
                                strokeWidth="1.5"
                                strokeLinecap="round"
                            />
                        </svg>
                        <span className='button-text'>{t("menu.exit")}</span>
                    </button>
                </div>
            </div>


            <div id="right-panel-container">
                {selectedControlOption === "clusters" && (
                    <>
                        <Items 
                            setSelectedCluster={handleClusterChange} 
                            selectedCluster={selectedCluster} 
                            setSelectedBindedCluster={setSelectedBindedCluster} 
                            selectedBindedCluster={selectedBindedCluster} 
                        /> 
                        {selectedCluster !== null && selectedBindedCluster !== null && (
                            <div id="control-tabs-container">
                                <div id="control-tabs-options">
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('statistics')} />
                                        <span id="statistics-img"></span>
                                        {t("menu.statistics")}
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('queue')} />
                                        <span id="queue-img"></span>
                                        {t("menu.queue")}
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('profiles')} />
                                        <span id="profiles-img"></span>
                                        {t("menu.profiles")}
                                    </label>
                                    <label className='tab-custom-radio'>
                                        <input type="radio" name='displayOption' onChange={() => handleDisplayOptionChange('terminal')} />
                                        <span id="clutser-terminal-img"></span>
                                        {t("menu.terminal")}
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