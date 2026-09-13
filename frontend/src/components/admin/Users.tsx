import "../../styles/users.css";
import "../../styles/components/dropTable.css"
import "../../styles/components/table.css";
import "../../styles/components/pagination.css"
import "../../styles/components/tableContainer.css"
import "../../styles/components/panelTab.css"

import { useEffect, useState } from "react";
import MovingPanel from "../../utils/MovingPanel";
import CreateUserForm from "../forms/CreaterUserForm";
import { exportGroupToJSON, getGroups } from "../../api/admin/groups";
import { exportUsersToJson, importUsersFromJson, deleteUser as apiDeleteUser, getUsers, UserAdminResponse, UserPageResponse } from "../../api/admin/users";
import GroupRenameForm from "../forms/GroupRenameForm";
import DeleteGroupForm from "../forms/DeleteGroupForm";
import CreateGroupForm from "../forms/CreateGroupForm";
import LinkUserForm from "../forms/LinkUserForm";
import CreateRandomUsers from "../forms/CreateRandomUsers";
import type { GroupAdminResponse } from "../../api/admin/groups";
import { Group, PageMetadata } from "../../utils/Interfaces";
import { useTranslation } from 'react-i18next'

const Users = () => {
    const { t } = useTranslation();
    type DisplayMode = "groupsTable" | "usersTable"

    const [pageUsers, setPageUsers] = useState<UserAdminResponse[]>([]);
    const [pageMetadata, setPageMetadata] = useState<PageMetadata>({
        size: 0,
        number: 0,
        totalElements: 0,
        totalPages: 0
    });

    const [tableGroup, setTableGroups] = useState<{ group: GroupAdminResponse; users: UserAdminResponse[] }[]>([]);
    const [openGroups, setOpenGroups] = useState<number[]>([]);

    const [displayOption, setDisplayOption] = useState<DisplayMode>();
    const [sortField, setSortField] = useState<string | null>(null);
    const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('ASC');

    const [dynamicPanel, setDynamicPanel] = useState<boolean>(false);
    const [isBusy, setIsBusy] = useState<boolean>(false);
    
    type TabKeys = 'users' | 'groups' | 'links';

    type OpenTabsState = {
      [key in TabKeys]: boolean;
    };

    const [openTabs, setOpenTabs] = useState<OpenTabsState>({
        users: false,
        groups: false,
        links: false
    });
    const [activeForm, setActiveForm] = useState<string>();
    const [isFormOpen, setIsFormOpen] = useState<boolean>(false);

    useEffect(() =>{
        switch(displayOption){
            case "groupsTable":
                setSortField(null);
                setSortDirection('ASC');
                setPageUsers([]);
                fetchGroups();
                break;
            case "usersTable":
                setSortField(null);
                setSortDirection('ASC');
                setTableGroups([]);
                setOpenGroups([]);
                fetchUsers();
                break;
        }
    }, [displayOption]);

    const fetchGroups = async() =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            const data = await getGroups(
                pageMetadata.number, 
                pageMetadata.size, 
                {
                    sortField: sortField || 'id',
                    sortDirection: sortDirection
                }
            );
            const prepared = data.content.map((group: GroupAdminResponse) => ({
                group,
                users: []
           }));
            setTableGroups(prepared);
        } catch (error) {
            console.log("Error!", error);
            alert(t('users.error'));
        } finally{
            setIsBusy(false);
        }
    }
    const fetchUsers = async(pageNumber = 0, pageSize = pageMetadata.size) =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            const data: UserPageResponse = await getUsers(
                pageNumber, 
                pageSize, 
                sortField ? {
                    sortField: sortField,
                    sortDirection: sortDirection
                } : null
            );
            setPageUsers(data.content);
            setPageMetadata({...data});
        } catch (error) {
            console.log("Error!", error);
            alert(t('users.error'));
        } finally{
            setIsBusy(false);
        }
    }
    const importUsers = async(event: React.ChangeEvent<HTMLInputElement>) =>{
        const file = event.target.files?.[0];
        if (!file || isBusy)
            return;
        setIsBusy(true);
        const formData = new FormData();
        formData.append('file', file);

        try {
            await importUsersFromJson(formData);
        } catch (error) {
            console.log("Error!", error);
            alert('Ошибка!');
        } finally{
            setIsBusy(false);
            if(displayOption === "groupsTable")
                fetchGroups();
            else
                fetchUsers();
        }
    }
    const exportUsers = async() =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            await exportUsersToJson();
        } catch (error) {
            alert('Error!');
            console.log("Error!", error);
        } finally{
            setIsBusy(false);
        }
    }
    const exportGroup = async(group: Group) =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            await exportGroupToJSON(group);
        } catch (error) {
            alert('Error!');
            console.log("Error!", error);
        } finally{
            setIsBusy(false);
        }
    }
   const deleteUser = async(user: UserAdminResponse) =>{
        if(isBusy)
            return;
        setIsBusy(true);
        try {
            await apiDeleteUser(user.id);
            if (displayOption === "groupsTable" && user.group?.id) {
                const data = await getUsers(
                    pageMetadata.number, 
                    pageMetadata.size, 
                    sortField ? {
                        sortField: sortField,
                        sortDirection: sortDirection
                    } : null,
                    { groupId: user.group.id }
                );
                setTableGroups(prev => prev.map(item =>
                    item.group.id === user.group?.id ? { ...item, users: data.content } : item
                ));
            } else {
                const data: UserPageResponse = await getUsers(
                    pageMetadata.number, 
                    pageMetadata.size, 
                    sortField ? {
                        sortField: sortField,
                        sortDirection: sortDirection
                    } : null
                );
                setPageUsers(data.content);
                setPageMetadata({...data});
            }
        } catch (error) {
            alert('Error!');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

  const loadGroupUsers = async(groupId: number) =>{
        try {
            const data: UserPageResponse = await getUsers(
                0,
                100,
                null,
                { groupId }
            );
            setTableGroups(prev => prev.map(item =>
                item.group.id === groupId ? { ...item, users: data.content } : item
            ));
        } catch (error) {
            alert('Error!');
            console.log("Error!", error);
        } finally {
            setIsBusy(false);
        }
    }

    const renderDisplayOption = (option: DisplayMode) =>{
        document.querySelector(`label[for=${displayOption}]`)?.classList.remove('active');
        document.querySelector(`label[for=${option}]`)?.classList.add('active');
        setDisplayOption(option);
    };
    
    const toggleGroup = (groupId: number) =>{
        if(openGroups.includes(groupId)){
            setTableGroups(prev => prev.map(item =>
                item.group.id === groupId? {...item, users:[]} : item
            ));
            setOpenGroups(prev => prev.filter(id => id !== groupId));
        } else{
            try {
                loadGroupUsers(groupId);
            } catch (error) {
                console.log("Error!", error);
                alert("Ошибка!");
            }
            setOpenGroups(prev => [...prev, groupId]);
        }
    };


    const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) =>{
        const newSize = parseInt(event.target.value, 10);
        fetchUsers(0, newSize);
    };

    const handlePageChange = (pageNumber: number) =>{
        fetchUsers(pageNumber);
    };

    const renderPageNumbers = () => {
        const { number: currentPage, totalPages } = pageMetadata;

        if (totalPages <= 1) {
            return renderPageButton(0);
        }

        const pages = new Set<number>();
        pages.add(0);
        pages.add(totalPages - 1);

        for (
            let page = currentPage - 1;
            page <= currentPage + 1;
            page++
        ) {
            if (page >= 0 && page < totalPages) {
                pages.add(page);
            }
        }
        if (currentPage > 2) {
            pages.add(1);
        }

        if (currentPage < totalPages - 3) {
            pages.add(totalPages - 2);
        }

        const sortedPages = [...pages].sort((a, b) => a - b);

        const result: React.ReactNode[] = [];

        sortedPages.forEach((page, index) => {
            if (index > 0 && page - sortedPages[index - 1] > 1) {
                result.push(
                    <span key={`dots-${page}`} style={{ padding: '0 5px' }}>
                        ...
                    </span>
                );
            }

            result.push(renderPageButton(page));
        });

        return result;
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

      const rerenderCurrentPage = () =>{
        switch(displayOption){
            case 'groupsTable':
                fetchGroups();
                break;
            case 'usersTable':
                fetchUsers();
                break;
        }
    }

    const toggleTab = (tab: TabKeys) =>{
        setOpenTabs(prev => ({
            ...prev,
            [tab]: !prev[tab],
        }))
    }

    const HiddenPassword = ({ password }: {password: string}) =>{
        const [visible, setVisible] = useState<boolean>(false);
        return(
            <span>
                {visible? password: "********"}
                <button
                    style={{
                        background: "none"
                    }}
                    onClick={() => setVisible(val => !val)}
                    aria-label={visible ? "Hide password" : "Show password"}
                >
                    {visible? "👁️": "👁️‍🗨️"}
                </button>
            </span>
        )
    }

    useEffect(() => {
        rerenderCurrentPage();
    }, [sortField, sortDirection]);
    
    return (
        <div id="users-containter">
            <MovingPanel isOpen={dynamicPanel} onClose={() => setDynamicPanel(dynamicPanel? false: true)}>
                <div className="dynamic-panel-tab">
                    <button onClick={() => toggleTab('users')}>{t("groups.users")}</button>
                    {openTabs.users && <div className="subsection">
                        <button onClick={() => {setActiveForm('createUser'); setIsFormOpen(true);}}>{t("groups.createUser")}</button>
                        <button onClick={() => {setActiveForm('createRandomUsers'); setIsFormOpen(true)}}>{t("groups.createRandomUser")}</button>
                    </div>}
                    <button onClick={() => toggleTab('groups')}>{t("groups.groups")}</button>
                    {openTabs.groups && <div className="subsection">
                        <button onClick={() => {setActiveForm('createGroup'); setIsFormOpen(true)}}>{t("groups.createGroup")}</button>
                        <button onClick={() => {setActiveForm('deleteGroup'); setIsFormOpen(true)}}>{t("groups.deleteGroup")}</button>
                    </div>}
                    <button onClick={() => toggleTab('links')}>{t("groups.link")}</button>
                    {openTabs.links && <div className="subsection">
                        <button onClick={() => {setActiveForm('linkUser'); setIsFormOpen(true)}}>{t("groups.linkUser")}</button>
                    </div>}
                </div>
                    
                <div className="dynamic-panel-tab-content">
                    {activeForm === 'deleteGroup' && <DeleteGroupForm onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)}/>}
                    {activeForm === 'createUser' && <CreateUserForm onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)}/>}
                    {activeForm === 'createGroup' && <CreateGroupForm onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)}/>}
                    {activeForm === 'linkUser' && <LinkUserForm onSuccessCall={() => rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} />}
                    {activeForm === 'createRandomUsers' && <CreateRandomUsers onSuccessCall={()=>rerenderCurrentPage()} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} />}
                </div>
            </MovingPanel>
            
            <div id="display-mode-container">
                <label htmlFor="groupsTable">
                    <input type="radio" name="displayMode" onChange={() => renderDisplayOption("groupsTable")} />
                    {t("groups.groups")}
                </label>
                <label htmlFor="usersTable">
                    <input type="radio" name="displayMode" onChange={() => renderDisplayOption("usersTable")} />
                    {t("groups.users")}
                </label>
            </div>
            <div id="button-container">
                <label>
                    <input type="file" accept=".json" onChange={importUsers} />
                    <span>{t("groups.import")}</span>
                </label>
                <button onClick={() => exportUsers()}>{t("groups.export")}</button>
            </div>
            <div id="user-container-render">
                {displayOption === "groupsTable" && tableGroup.map(item =>(
                    <div className="drop-table" key={item.group.id}>
                        {activeForm === 'groupRename' && <GroupRenameForm group={item.group} isOpen={isFormOpen} onClose={() => setIsFormOpen(false)}></GroupRenameForm>}
                        <div className="drop-table-groups" onClick={() => toggleGroup(item.group.id)}>
                            {item.group.name} {openGroups.includes(item.group.id)? '▲' : '▼'}
                            <div>
                                <button onClick={() => exportGroup(item.group)}>{t("groups.export")}</button>
                                <button onClick={() => {setActiveForm('groupRename'); setIsFormOpen(true)}}>{t("groups.rename")}</button>
                            </div>
                        </div>
                        {openGroups.includes(item.group.id) && (
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>№</th>
                                        <th>Id</th>
                                        <th>{t("users.username")}</th>
                                        <th>{t("users.password")}</th>
                                        <th>{t("users.createdAt")}</th>
                                        <th>{t("users.action")}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {item.users.length === 0 ? 
                                        (<tr><td>{t("users.empty")}</td></tr>): 
                                        item.users.map((user,index) =>(
                                            <tr key={user.id}>
                                                <td>{index+1}</td>
                                                <td>{user.id}</td>
                                                <td>{user.username}</td>
                                                <td><HiddenPassword password={user.password}/></td>
                                                <td>{new Date(user.createdAt).toLocaleString()}</td>
                                                <td>
                                                    <button onClick={() => deleteUser(user)}>{t("users.delete")}</button>
                                                </td>
                                            </tr>
                                        ))
                                    }
                                </tbody>
                            </table>
                        )}
                    </div>
                ))}
                {displayOption === "usersTable" &&
                    <div className="table-option-container">
                        <div className="table-container">
                            {pageUsers.length === 0 ? (<h2>{t("users.loading")}</h2>) : <>
                                <table className="table">
                                    <thead>
                                        <tr>
                                               <th>№</th>
                                            <th onClick={() => handleSort('id')}>Id
                                                {sortField === 'id' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('username')}>{t('users.username')}
                                                {sortField === 'username' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('password')}>{t('users.password')}
                                                {sortField === 'password' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('group.name')}>{t('users.group')}
                                                {sortField === 'group.name' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th onClick={() => handleSort('createdAt')}>{t('users.createdAt')}
                                                {sortField === 'createdAt' && (sortDirection === 'ASC' ? '↑' : '↓')}</th>
                                            <th>{t('users.action')}</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                          {pageUsers.length === 0 ? (<tr><td>{t('users.loading')}</td></tr>) :
                                            pageUsers.map((user, index) => (
                                                <tr key={user.id}>
                                                    <td>{index+1}</td>
                                                    <td>{user.id}</td>
                                                    <td>{user.username}</td>
                                                    <td><HiddenPassword password={user.password}/></td>
                                                    <td>{user.group !== null ? user.group.name : t('common.no')}</td>
                                                    <td>{new Date(user.createdAt).toLocaleString()}</td>
                                                    <td>
                                                        <button onClick={() => deleteUser(user)}>{t('users.delete')}</button>
                                                    </td>
                                                </tr>
                                            ))
                                        }
                                    </tbody>
                                </table>
                            </>}
                        </div>
                        <div className="pagination">
                             <button className="prev-button" onClick={() => handlePageChange(pageMetadata.number - 1)} disabled={pageMetadata.number === 0}>
                                {t('common.back')}
                            </button>

                            {renderPageNumbers()}

                            <button className="next-button" onClick={() => handlePageChange(pageMetadata.number + 1)} disabled={pageMetadata.number === pageMetadata.totalPages}>
                                {t('common.next')}
                            </button>

                            <select title={t("profiles.selectTitle")} value={pageMetadata.size} onChange={handlePageSizeChange}>
                                <option value={20}>20</option>
                                <option value={30}>30</option>
                                <option value={50}>50</option>
                                <option value={100}>100</option>
                            </select>
                        </div>
                    </div>
                }
            </div>
        </div>
    )
}
export default Users;