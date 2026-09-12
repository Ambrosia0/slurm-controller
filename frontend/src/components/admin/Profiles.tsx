import "../../styles/profiles.css";
import "../../styles/components/dropTable.css"
import "../../styles/components/table.css";
import "../../styles/components/pagination.css"
import "../../styles/components/tableContainer.css"
import "../../styles/components/panelTab.css"

import { useEffect, useState } from "react";
import MovingPanel from "../../utils/MovingPanel";
import {
    getProfiles,
    deleteProfile as apiDeleteProfile,
    downloadProfile as apiDownloadProfile,
    downloadMultipleProfiles,
    ClusterProfileResponse,
} from "../../api/admin/profiles";
import CreateProfile from "../forms/CreateProfile";
import RevokeGroupProfile from "../forms/RevokeGroupProfile";
import CreateGroupProfiles from "../forms/CreateGroupProfiles";
import { formatSecondsToHMS } from "../../utils/functions";
import { PageMetadata, Sort } from "../../api/admin/Interfaces";
import { getGroups, GroupAdminResponse } from "../../api/admin/groups";
import { SlurmClusterRec } from "../../api/admin/clusters";
import { NON_LIMITABLE_TRES } from "../../utils/Interfaces";

interface ProfileTableProp {
    group: GroupAdminResponse;
    profiles: ClusterProfileResponse[];
}

type ProfileProps = {
    clusterId: number;
    bindedCluster: SlurmClusterRec;
}

const Profiles: React.FC<ProfileProps> = ({ bindedCluster, clusterId }) => {
    type DisplayMode = "groups" | "profiles"

    const [pageProfiles, setPageProfiles] = useState<ClusterProfileResponse[]>([]);
    const [pageMetadata, setPageMetadata] = useState<PageMetadata>({
        size: 0,
        number: 0,
        totalElements: 0,
        totalPages: 0
    });
    const [tableGroup, setTableGroups] = useState<ProfileTableProp[]>([]);
    const [openGroups, setOpenGroups] = useState<number[]>([]);
    const [dynamicPanel, setDynamicPanel] = useState<boolean>(false);

    const [displayOption, setDisplayOption] = useState<DisplayMode>();

    const [sortField, setSortField] = useState<string | null>(null);
    const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('ASC');
    
    const [isBusy, setIsBusy] = useState<boolean>(false);

    const [activeForm, setActiveForm] = useState<string>();
    const [isFormOpen, setIsFormOpen] = useState<boolean>(false);

    useEffect(() => {
        switch (displayOption) {
            case "groups":
                setSortField(null);
                setSortDirection('ASC');
                setPageProfiles([]);
                fetchGroups();
                break;
            case "profiles":
                setSortField(null);
                setSortDirection('ASC');
                setTableGroups([]);
                setOpenGroups([]);
                fetchProfiles();
                break;
        }
    }, [displayOption]);

    const renderDisplayOption = (option: DisplayMode) => {
        document.querySelector(`label[for=${displayOption}]`)?.classList.remove('active');
        document.querySelector(`label[for=${option}]`)?.classList.add('active');
        setDisplayOption(option);
    };

    const fetchGroups = async () => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            const data = (await getGroups(0, 50, null, {
                bindedCluster: bindedCluster.name,
                clusterId: clusterId
            })).content;
            const prepared = data.map((group: GroupAdminResponse) => ({
                group,
                profiles: []
            }))
            setTableGroups(prepared);
        } catch (error) {
            console.log("Error!", error);
            alert('Ошибка!');
        } finally {
            setIsBusy(false);
        }
    }

    const fetchProfiles = async (pageNumber = 0, pageSize = 20) => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            const sort: Sort | null = sortField ? {
                sortField: sortField,
                sortDirection: sortDirection
            } : null;
            const data = await getProfiles(
                pageNumber, 
                pageSize,
                sort,
                {
                    clusterId: clusterId,
                    bindedCluster: bindedCluster.name
                }
            );
            setPageProfiles(data.content);
            setPageMetadata(data.pageable);
        } catch (error) {
            console.log("Error!", error);
            alert('Ошибка!');
        } finally {
            setIsBusy(false);
        }
    }

    const loadGroupProfiles = async (groupId: number) => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            const data = await getProfiles(
                0,
                1000,
                null,
                {
                    groupId: groupId,
                    clusterId: clusterId,
                    bindedCluster: bindedCluster.name
                }
            );
            setTableGroups(prev => prev.map(item =>
                item.group.id === groupId ? { ...item, profiles: data.content } : item
            ));
        } catch (error) {
            alert('Ошибка!');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

    const deleteProfile = async (profile: ClusterProfileResponse) => {
        const confirmed = window.confirm(`Удалить профиль ${profile.user.username}?`)
        if (confirmed) {
            if (isBusy)
                return;
            setIsBusy(true);
            try {
                await apiDeleteProfile(clusterId, bindedCluster.name, profile.user.id);
                if (displayOption === "groups" && profile.user.group?.id) {
                    const data = await getProfiles(
                        0,
                        1000,
                        null,
                        {
                            groupId: profile.user.group.id,
                            clusterId: clusterId,
                            bindedCluster: bindedCluster.name
                        }
                    );
                    setTableGroups(prev => prev.map(item =>
                        item.group.id === profile.user.group?.id ? { ...item, profiles: data.content } : item
                    ));
                } else {
                    const sort: Sort | null = sortField ? {
                        sortField: sortField,
                        sortDirection: sortDirection
                    } : null;
                    const data = await getProfiles(
                        pageMetadata.number,
                        pageMetadata.size,
                        sort,
                        {
                            clusterId: clusterId,
                            bindedCluster: bindedCluster.name
                        }
                    );
                    setPageProfiles(data.content);
                    setPageMetadata(data.pageable);
                }
            } catch (error) {
                alert('Ошибка!');
                console.log("Error!", error);
            } finally {
                setIsBusy(false);
            }
        }
    }

   const downloadProfile = async (profile: ClusterProfileResponse) => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            await apiDownloadProfile(clusterId, bindedCluster.name, profile.user.id);
        } catch (error) {
            alert('Ошибка!');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

    const downloadGroup = async (group: GroupAdminResponse) => {
        if (isBusy)
            return;
        setIsBusy(true);
        try {
            const data = await getProfiles(
                0,
                1000,
                null,
                {
                    groupId: group.id,
                    clusterId: clusterId,
                    bindedCluster: bindedCluster.name
                }
            );
            const userIds = data.content.map(p => p.user.id);
            await downloadMultipleProfiles(clusterId, bindedCluster.name, userIds);
        } catch (error) {
            alert('Ошибка!');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

    const toggleGroup = (groupId: number) => {
        if (openGroups.includes(groupId)) {
            setTableGroups(prev => prev.map(item =>
                item.group.id === groupId ? { ...item, profiles: [] } : item
            ));
            setOpenGroups(prev => prev.filter(id => id !== groupId));
        } else {
            loadGroupProfiles(groupId);
            setOpenGroups(prev => [...prev, groupId]);
        }
    };

    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        const newSize = parseInt(event.target.value, 10);
        fetchProfiles(0, newSize);
    };

    const handlePageChange = (pageNumber: number) => {
        fetchProfiles(pageNumber);
    };

    const renderPageNumbers = () => {
        const pages = [];
        const { number: currentPage, totalPages } = pageMetadata;

        const createPageButton = (page: number) => renderPageButton(page);

        const addEllipsis = (key: string) =>
            pages.push(<span key={key} style={{ padding: '0 5px' }}>...</span>);

        pages.push(createPageButton(0));

        if (currentPage > 2) {
            pages.push(createPageButton(1));
        }

        if (currentPage > 3) {
            addEllipsis('left-dots');
        }

        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
            if (i > 1 && i < totalPages - 1) {
                pages.push(createPageButton(i));
            }
        }

        if (currentPage < totalPages - 4) {
            addEllipsis('right-dots');
        }

        if (currentPage < totalPages - 2) {
            pages.push(createPageButton(totalPages - 2));
        }

        if (totalPages > 1) {
            pages.push(createPageButton(totalPages - 1));
        }

        return pages;
    };

    const renderPageButton = (pageIndex: number) => (
        <button
            key={pageIndex}
            onClick={() => handlePageChange(pageIndex)}
            style={{
                fontWeight: pageMetadata.number === pageIndex ? 'bold' : 'normal',
                margin: '0 4px',
            }}>
            {pageIndex + 1}
        </button>
    );

    const handleSort = (field: string) => {
        if (sortField === field) {
            setSortDirection(sortDirection === 'ASC' ? 'DESC' : 'ASC');
        } else {
            setSortField(field);
            setSortDirection('ASC');
        }
    };

    const rerenderCurrentPage = () => {
        if (displayOption === "groups") {
            fetchGroups();
        } else if (displayOption === "profiles") {
            fetchProfiles(pageMetadata.number);
        }
        return;
    }
    useEffect(() => {
        if (displayOption === "profiles") {
            rerenderCurrentPage();
        }
    }, [sortField, sortDirection]);


    const parseTres = (maxTres: string[] | null): Record<string, string> => {
        if (!maxTres || maxTres.length === 0) return {};
        const result: Record<string, string> = {};
        for (const tres of maxTres) {
            const parts = tres.split('=');
            if (parts.length === 2 && !NON_LIMITABLE_TRES.has(parts[0])) {
                result[parts[0]] = parts[1];
            }
        }
        return result;
    };

    const renderProfile = (index: number, profile: ClusterProfileResponse) => {
        const tres = parseTres(profile.maxTres);
        const tresEntries = Object.entries(tres);

        return (
            <tr key={profile.user.id}>
                <td>{index + 1}</td>
                <td>{profile.user.id}</td>
                <td>{profile.user.username}</td>
                <td>{profile.user.group?.name}</td>
                <td>{new Date(profile.createdAt).toLocaleString()}</td>
                <td>{profile.maxSubmit}</td>
                <td>{profile.maxJobs}</td>
                <td>
                    {tresEntries.length === 0 ? '-' : (
                        <table className="tres-subtable">
                            <tbody>
                                {tresEntries.map(([key, value]) => (
                                    <tr key={key}>
                                        <td>{key}</td>
                                        <td>{value}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </td>
                <td>{profile.softLimit + 'MB'}</td>
                <td>{profile.hardLimit + 'MB'}</td>
                <td>{formatSecondsToHMS(profile.maxTaskLiveTime)}</td>
                <td>
                    <button onClick={() => deleteProfile(profile)}>Удалить</button>
                    <button onClick={() => downloadProfile(profile)}>Загрузить</button>
                </td>
            </tr>
        );
    };

    return (
        <div id="profiles-container">
            <MovingPanel isOpen={dynamicPanel} onClose={() => setDynamicPanel(dynamicPanel ? false : true)}>
                <div className="dynamic-panel-tab">

                    <button onClick={() => { setActiveForm('createProfile'); setIsFormOpen(true) }}>Предоставление доступа пользователям</button>
                    <button onClick={() => { setActiveForm('provideGroupAccess'); setIsFormOpen(true) }}>Предоставление доступа группе пользователей</button>
                    <button onClick={() => { setActiveForm('revokeAccessFromGroup'); setIsFormOpen(true) }}>Аннулирование доступа группе</button>
                </div>
                <div className="dynamic-panel-tab-content">
                    {activeForm === 'revokeAccessFromGroup' && <RevokeGroupProfile isBusy={isBusy} setIsBusy={setIsBusy} bindedCluster={bindedCluster} clusterId={clusterId} onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} />}
                    {activeForm === 'provideGroupAccess' && <CreateGroupProfiles isBusy={isBusy} setIsBusy={setIsBusy} bindedCluster={bindedCluster} clusterId={clusterId} onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} />}
                    {activeForm === 'createProfile' && <CreateProfile isBusy={isBusy} setIsBusy={setIsBusy} bindedCluster={bindedCluster} clusterId={clusterId} onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} />}
                </div>
            </MovingPanel>
            <div id="profiles-container-display-mode">
                <label htmlFor="groups">
                    <input type="radio" name="displayMode" onChange={() => renderDisplayOption("groups")} />
                    Группы
                </label>
                <label htmlFor="profiles">
                    <input type="radio" name="displayMode" onChange={() => renderDisplayOption("profiles")} />
                    Профили
                </label>
            </div>
            <div id="profiles-option-render-container">
                {displayOption === "groups" && tableGroup.map(item => (
                    <div className="drop-table" key={item.group.id}>
                        <div className="drop-table-groups" onClick={() => toggleGroup(item.group.id)}>
                            {item.group.name} {openGroups.includes(item.group.id) ? '▲' : '▼'}
                            <div>
                                <button onClick={() => downloadGroup(item.group)}>Загрузить</button>
                            </div>
                        </div>
                        {openGroups.includes(item.group.id) && (
                            <table className="table">
                                <thead>
                                    <tr>
                                        {/* <th onClick={() => handleSort('id.user.id', item.group.id)}>Id пользователя
                                            {sortField === 'id.user.id' && (sortDirection === 'asc' ? '↑' : '↓')}</th>
                                        <th onClick={() => handleSort('id.user.username', item.group.id)}>Имя пользователя
                                            {sortField === 'id.user.username' && (sortDirection === 'asc' ? '↑' : '↓')}</th>
                                        <th onClick={() => handleSort('username', item.group.id)}>Имя профиля
                                            {sortField === 'username' && (sortDirection === 'asc' ? '↑' : '↓')}</th>
                                        <th onClick={() => handleSort('password', item.group.id)}>Пароль
                                            {sortField === 'password' && (sortDirection === 'asc' ? '↑' : '↓')}</th>
                                        <th onClick={() => handleSort('createdAt', item.group.id)}>Время создания
                                            {sortField === 'createdAt' && (sortDirection === 'asc' ? '↑' : '↓')}</th> */}
                                        <th>№</th>
                                        <th>Id</th>
                                        <th>Пользователь</th>
                                        <th>Время создания</th>
                                        <th>Макс. в очереди</th>
                                        <th>Макс. выполн.</th>
                                        <th>TRES</th>
                                        <th>Soft</th>
                                        <th>Hard</th>
                                        <th>TTL</th>
                                        <th>Действ.</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {item.profiles.length === 0 ?
                                        (<tr><td>Профили не созданы...</td></tr>) :
                                        item.profiles.map((profile, index) => (
                                            renderProfile(index, profile)
                                        ))
                                    }
                                </tbody>
                            </table>    
                        )}
                    </div>
                ))}
                {displayOption === "profiles" && <>
                    <div className="table-option-container">
                        <div className="table-container">
                            {pageProfiles.length === 0 ? (<h2>Профили не созданы...</h2>) : <>
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th>№</th>
                                            <th onClick={() => handleSort('id.user.id')}>Id
                                                {sortField === 'id.user.id' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('id.user.username')}>Пользователь
                                                {sortField === 'id.user.username' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('id.user.group.name')}>Группа
                                                {sortField === 'id.user.group.name' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('createdAt')}>Создан
                                                {sortField === 'createdAt' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th>Макс. в очереди</th>
                                            <th>Макс. выполн.</th>
                                            <th>TRES</th>
                                            <th>Soft</th>
                                            <th>Hard</th>
                                            <th>TTL</th>
                                            <th>Действ.</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {pageProfiles.map((profile, index) => (
                                            renderProfile(index, profile)
                                        ))}
                                    </tbody>
                                </table>
                            </>}
                        </div>
                        <div className="pagination">
                            <button className="prev-button" onClick={() => handlePageChange(pageMetadata.number - 1)} disabled={pageMetadata.number === 0}>
                                Назад
                            </button>

                            {renderPageNumbers()}

                            <button className="next-button" onClick={() => handlePageChange(pageMetadata.number + 1)} disabled={pageMetadata.number === pageMetadata.totalPages}>
                                Вперед
                            </button>

                            <select title="Количество отображаемых профилей" value={pageMetadata.size} onChange={handlePageSizeChange}>
                                <option value={20}>20</option>
                                <option value={30}>30</option>
                                <option value={50}>50</option>
                                <option value={100}>100</option>
                            </select>
                        </div>
                    </div>
                </>}
            </div>
        </div>
    )
}
export default Profiles;