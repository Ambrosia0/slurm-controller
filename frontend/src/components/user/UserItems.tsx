import React, { useEffect, useState } from "react";
import { ClusterUserResponse, getAvailableClusters, getAvailableBindedClusters } from "../../api/user/userApi";
import { BindedCluster } from "../../utils/Interfaces";
import { SlurmClusterRec } from "../../api/admin/clusters";

interface ItemsProps{
    selectedCluster: ClusterUserResponse|null;
    setSelectedCluster: (cluster: ClusterUserResponse|null) => void;

    selectedBindedCluster: SlurmClusterRec | null;
    setSelectedBindedCluster: (bindedCluser: SlurmClusterRec|null) => void;
}

const UserItems: React.FC<ItemsProps> = ({selectedBindedCluster, setSelectedBindedCluster, selectedCluster, setSelectedCluster}) =>{
    const [clusters, setClusters] = useState<ClusterUserResponse[] | null>();
    const [bindedClusters, setBindedClusters] = useState<SlurmClusterRec[] | null>(null);
    const [searchText, setSearchText] = useState<string>('');

    const fetchClusters = async () => {
        try {
            const data = await getAvailableClusters();
            setClusters(data);
        } catch (error) {
            alert('Ошибка!');
            console.log("Error!", error);
        }
    }

    const fetchBindedClusters = async (clusterId?: number) => {
        try {
            if(!clusterId)
                return;
            const data = await getAvailableBindedClusters(clusterId);
            setBindedClusters(data);
        } catch (error) {
            alert('Ошибка!');
            console.log("Error!", error);
        }
    }
    
    useEffect(() => {
        fetchClusters();
    }, [])

    useEffect(() => {
        fetchBindedClusters(selectedCluster?.id);
    }, [selectedCluster])

    const toggleChecked = (cluster: ClusterUserResponse) =>{
        document.querySelectorAll('label.items-custom-radio').forEach(label => {
            label.classList.remove('active');
        });
        document.querySelector(`label[for="cluster${cluster.id}"]`)?.classList.add('active');
        setSelectedCluster(cluster);
    }

    return (
        <div id='items-container'>
            <div id='items-tools-container'>
                <input id='items-search-field' placeholder='Search...' type='search'
                    onKeyDown={(e) => { if (e.key === 'Enter') { setSearchText(e.currentTarget.value); } }}></input>
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
                        </label>
                        {selectedCluster === cluster && bindedClusters && bindedClusters.length > 0 && (
                            <div className='binded-clusters'>
                                {bindedClusters.map((binded, index) => (
                                    <label key={index} className='binded-radio'>
                                        <input
                                            type="radio"
                                            id={`binded-${binded}`}
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
}
export default UserItems;