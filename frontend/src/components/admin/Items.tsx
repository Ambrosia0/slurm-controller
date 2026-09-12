import '../../styles/items.css'
import '../../styles/components/updateButton.css'

import React, { useState, useEffect } from 'react';
import {deleteCluster, getBindedClusters, getClusters} from '../../api/admin/clusters';
import { ClusterAdminResponse, SlurmClusterRec } from '../../api/admin/clusters';
import { PageMetadata } from '../../api/admin/Interfaces';


interface ItemsProps {
    selectedCluster: ClusterAdminResponse | null;
    setSelectedCluster: (cluster: ClusterAdminResponse | null) => void;
    selectedBindedCluster: SlurmClusterRec | null;
    setSelectedBindedCluster: (bindedCluster: SlurmClusterRec | null) => void;
}

const Items: React.FC<ItemsProps> = ({selectedBindedCluster, setSelectedBindedCluster, selectedCluster, setSelectedCluster}) => {
    const [clusters, setClusters] = useState<ClusterAdminResponse[]|null>();
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
            const confirmed = window.confirm(`Удалить кластер ${selectedCluster?.displayedName}?`)
            if(confirmed){
                await deleteCluster(cluster.id);
                setSelectedCluster(null);
                const data = await getClusters(0, 10, {sortField: 'id', sortDirection: 'ASC'});
                setClusters(data.content);
                setPageMeta(data.pageable);
            }
        } catch (error) {
            alert('Ошибка!');
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
            alert('Ошибка!');
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
            alert('Ошибка!');
            console.log("Error!", error);
        }
    }

    useEffect(() =>{
        fetchBindedClusters(selectedCluster?.id ?? null);
    }, [selectedCluster])

    useEffect(() => {
        fetchClusters();
    }, [searchText])

    return (
        <div id='items-container'>
            <div id='items-tools-container'>
                <input id='items-search-field' placeholder='Search...' type='search'   
                    onKeyDown={(e) => {if (e.key === 'Enter') {setSearchText(e.currentTarget.value);}}}></input>
                <button className='update-button' title='Обновить' onClick={() => {
                    setSelectedBindedCluster(null);
                    setSelectedCluster(null);
                    setClusters(null);
                    setBindedClusters(null);
                    fetchClusters()}}></button>
            </div>
            <div id='items-cluster-container'>
                {clusters != null && clusters.map((cluster, index) => (
                    <React.Fragment key={index}>
                        <label key={index} className='items-custom-radio' htmlFor={`cluster${cluster.id}`}>
                            <input type="radio" id={`cluster${index}`} name="clusters" value={cluster.id} onChange={() => toggleChecked(cluster)}></input>
                            <span className='name-hint'>{cluster.displayedName}</span>
                            <div className='hint-container'>
                                <span className="port-hint">Daemon Port: {cluster.daemonPort}</span>
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