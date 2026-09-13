import '../../styles/items.css'
import '../../styles/components/updateButton.css'

import React, { useState, useEffect } from 'react';
import {deleteCluster, getBindedClusters, getClusters} from '../../api/admin/clusters';
import { ClusterAdminResponse, SlurmClusterRec } from '../../api/admin/clusters';
import { PageMetadata } from '../../api/admin/Interfaces';
import { useTranslation } from 'react-i18next'


interface ItemsProps {
    selectedCluster: ClusterAdminResponse | null;
    setSelectedCluster: (cluster: ClusterAdminResponse | null) => void;
    selectedBindedCluster: SlurmClusterRec | null;
    setSelectedBindedCluster: (bindedCluster: SlurmClusterRec | null) => void;
}

const Items: React.FC<ItemsProps> = ({
    setSelectedBindedCluster, 
    selectedCluster, 
    setSelectedCluster
}) => {
    const { t } = useTranslation();
    const [clusters, setClusters] = useState<ClusterAdminResponse[]>([]);
    const [bindedClusters, setBindedClusters] = useState<SlurmClusterRec[] | null>(null);

    const [searchText, setSearchText] = useState<string>('');

    const [pageMetadata, setPageMeta] = useState<PageMetadata | null>({
        size: 0,
        totalElements: 0,
        totalPages: 0,
        number: 0
    });


    const toggleChecked = (cluster: ClusterAdminResponse) =>{
        document.querySelectorAll('label.items-custom-radio').forEach(label => {
            label.classList.remove('active');
        });
        document.querySelector(`label[for="cluster${cluster.id}"]`)?.classList.add('active');
        setSelectedCluster(cluster);
    }

    const handleDelete = async (cluster: ClusterAdminResponse) =>{
        try {
            const confirmed = window.confirm(t('items.deleteCluster', { name: selectedCluster?.displayedName }))
            if(confirmed){
                await deleteCluster(cluster.id);
                setSelectedCluster(null);
                const data = await getClusters(0, 10, {sortField: 'id', sortDirection: 'ASC'});
                setClusters(data.content);
                setPageMeta(data.pageable);
            }
        } catch (error) {
            alert(t('items.error'));
            console.log("Error!", error);
        }
    }

    const fetchClusters = async () =>{
        try {
            if(searchText.length == 0){
                const data = await getClusters(0, 10, {sortField: 'id', sortDirection: 'ASC'});
                setClusters(data.content);
                setPageMeta(data.pageable);
            } else{
                const data = await getClusters(0, 10, {sortField: 'id', sortDirection: 'ASC'}, {
                    displayedName: searchText
                });
                setClusters(data.content);
            }
        } catch (error) {
            alert(t('items.error'));
            console.log("Error!", error);
        }
    }

    const fetchBindedClusters = async (clusterId: number | null) =>{
        try {
            if(clusterId === null)
                return;
            const data = await getBindedClusters(clusterId);
            setBindedClusters(data);
         } catch (error) {
            alert(t('items.error'));
            console.log("Error!", error);
        }
    }

    const handleClusterSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter')
            setSearchText(e.currentTarget.value);
    }

    const handleClusterUpdate = () => {
        setSelectedBindedCluster(null);
        setSelectedCluster(null);
        setClusters([]);
        setBindedClusters(null);
        fetchClusters()
    }

    useEffect(() =>{
        fetchBindedClusters(selectedCluster?.id ?? null);
    }, [selectedCluster])

    useEffect(() => {
        fetchClusters();
    }, [searchText])

    useEffect(() => {
        const handler = (event: Event) => {
            const customEvent = event as CustomEvent<ClusterAdminResponse>;
            setClusters(prev => [...prev, customEvent.detail])
        };

        document.addEventListener('clusterCreated', handler)
        
        return () => {
            document.removeEventListener('clusterCreated', handler);
        }
    }, [])

    return (
        <div id='items-container' className='items-container'>
            <div id='items-tools-container' className='items-tools-container'>
                <input id='items-search-field' placeholder={t('items.searchPlaceholder')} type='search'   
                    onKeyDown={handleClusterSearch}
                ></input>
                <button className='update-button' title={t('items.update')} onClick={handleClusterUpdate}></button>
            </div>
            <div id='items-cluster-container'>
                {clusters != null && clusters.map((cluster, index) => (
                    <React.Fragment key={index}>
                        <label key={index} className='items-custom-radio' htmlFor={`cluster${cluster.id}`}>
                            <input type="radio" id={`cluster${index}`} name="clusters" value={cluster.id} onChange={() => toggleChecked(cluster)}></input>
                            <span className='name-hint'>{cluster.displayedName}</span>
                            <div className='hint-container'>
                                <span className="port-hint">{t('items.daemonPort')}: {cluster.daemonPort}</span>
                                <button className='items-action-button' onClick={(e) => { e.stopPropagation(); handleDelete(cluster) }}></button>
                            </div>
                        </label>
                        {selectedCluster === cluster && bindedClusters && bindedClusters.length > 0 && (
                            <div className='binded-clusters'>
                                {bindedClusters.map((binded, index) => (
                                    <label key={index} className='binded-radio'>
                                        <input
                                            type="radio"
                                            id={`binded-${binded.name}`}
                                            name='bindedClusters'
                                            value={binded.name}
                                            onChange={() => setSelectedBindedCluster(binded)}
                                        />
                                        <span className="binded-name">{binded.name}</span>
                                    </label>
                                ))}
                            </div>
                        )}
                    </React.Fragment>
                ))}
            </div>
        </div>
    )
};

export default Items;