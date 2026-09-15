import "../../styles/statistics.css"
import "../../styles/components/list.css"

import { useState, useEffect } from "react";
import { AreaChart, Area, CartesianGrid, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';
import { apiUrl } from '../../utils/config';
import CustomTooltip from "../../utils/CustomTooltip";
import { Statistics } from "../../utils/Interfaces";
import { useTranslation } from 'react-i18next'

type StatisticsProps = {
    clusterId: number;
    bindedCluster: string;
}

const StatisticsDisplay: React.FC<StatisticsProps> = ({ clusterId, bindedCluster }) => {
    const { t } = useTranslation();
    const [statistics, setStatistics] = useState<Statistics[]>([]);
    const [selectedTab, setSelectedTab] = useState('cluster');

    const numberOfPoints = 20;

    type ChartDataItem = {
        name: string;
        value: number;
    };
    const [data, setData] = useState<ChartDataItem[]>([]);
    const COLORS = ['#4CAF50', '#F44336', '#FFF231','#FF9800', '#2196F3', '#2979FF'];
    useEffect(() => {
        const socket = new WebSocket(`${apiUrl}/api/ws/admin/statistics?clusterId=${clusterId}&clusterName=${bindedCluster}`);

         socket.onopen = () => {
            console.log(t('statistics.getting'));
        }

        socket.onmessage = (event: MessageEvent) => {
            const newStatistics: Statistics = JSON.parse(event.data);
            setStatistics(prevStatistics => {
                const update = [...prevStatistics, newStatistics];
                return update.length > numberOfPoints ? update.slice(-numberOfPoints) : update;
            })
        }

        socket.onerror = (error) => {
            console.error(t('statistics.websocketError'), error);
        }

        return () => {
            if (socket) {
                socket.close();
            }
        };
    }, [])

    useEffect(() =>{
        if (statistics.length === 0) return;
        console.log(statistics);
         setData([
            { name: t('statistics.completed'), value: statistics[statistics.length - 1].jobsCompleted },
            { name: t('statistics.failed'), value: statistics[statistics.length - 1].jobsFailed },
            { name: t('statistics.cancelled'), value: statistics[statistics.length - 1].jobsCancelled },
            { name: t('statistics.running'), value: statistics[statistics.length - 1].jobsRunning },
            { name: t('statistics.started'), value: statistics[statistics.length - 1].jobsStarted },
            { name: t('statistics.total'), value: statistics[statistics.length - 1].jobsSubmitted },
        ]);
    },[statistics])
    
    return (
        <div id="statistics-container">
            {statistics.length !== 0 && <>
                <div className="tabs">
              <button
                        className={selectedTab === 'cluster' ? 'active' : ''}
                        onClick={() => setSelectedTab('cluster')}
                    >
                        {t('statistics.clusterTitle')}
                    </button>
                    {statistics[statistics.length - 1].nodes.map(node => (
                        <button
                            key={node.hostname}
                            className={selectedTab === node.hostname ? 'active' : ''}
                            onClick={() => setSelectedTab(node.hostname)}
                        >
                            {node.hostname}
                        </button>
                    ))}
                </div>

                 {selectedTab === 'cluster' ? (
                    <div id="statistics-cluster-container">
                        <h2>{t('statistics.clusterTitle')}</h2>
                        <PieChart width={600} height={300}>
                            <Pie
                                data={data}
                                dataKey="value"
                                nameKey="name"
                                cx="50%"
                                cy="50%"
                                outerRadius={100}
                                fill="#8884d8"
                                label
                            >
                                {data.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip />
                            <Legend />
                        </PieChart>
                        <div className="list-item">
                            <span>{t('statistics.totalCompleted')}</span>
                            <span>{statistics[statistics.length - 1].jobsCompleted}</span>
                        </div>
                        <div className="list-item">
                            <span>{t('statistics.totalFailed')}</span>
                            <span>{statistics[statistics.length - 1].jobsFailed}</span>
                        </div>
                        <div className="list-item">
                            <span>{t('statistics.runningTasks')}</span>
                            <span>{statistics[statistics.length - 1].jobsRunning}</span>
                        </div>
                        <div className="list-item">
                            <span>{t('statistics.startedTasks')}</span>
                            <span>{statistics[statistics.length - 1].jobsStarted}</span>
                        </div>
                        <div className="list-item">
                            <span>{t('statistics.totalTasks')}</span>
                            <span>{statistics[statistics.length - 1].jobsSubmitted}</span>
                        </div>
                    </div>
                ) : (
                    (() => {
                        const node = statistics[statistics.length - 1].nodes.find(n => n.hostname === selectedTab);
                                  if (!node) return <div>{t('statistics.nodeNotFound')}</div>;

                        const nodeHistory = statistics.map(stat => {
                            const matchingNode = stat.nodes.find(n => n.hostname === node.hostname);
                            const mem = matchingNode?.tres
                                .split(",")
                                .map(v => v.trim().split("="))
                                .find(([name]) => name === "mem")?.[1];

                            return {
                                cpuLoad: matchingNode?.cpuLoad ?? 0,
                                ramLoad: Math.round((matchingNode?.allocatedMemory ?? 0)/1024).toFixed(1),
                                totalMemory: (Number(mem?.replace(/M$/, ""))).toFixed(1),
                                timestamp: new Date(stat.timestamp).toLocaleTimeString()
                            };
                        });

                        return (
                            <div id="statistics-node-container" key={node.name ?? node.hostname}>
                                <h2>{t('statistics.nodeState')}: {node.name ?? node.hostname}</h2>
                                <ul className="list">
                                    <li className="list-item">
                                        <span>{t('statistics.bootTime')}</span>
                                        <span>{new Date(node.bootTime.number * 1000).toLocaleString()}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.sockets')}</span>
                                        <span>{node.sockets}</span>
                                    </li>
                                      <li className="list-item">
                                        <span>{t('statistics.logicalCpus')}</span>
                                        <span>{node.cpus}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.coresPerCpu')}</span>
                                        <span>{node.cores}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.threads')}</span>
                                        <span>{node.threads}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.resources')}</span>
                                        <span>{node.gres.length > 0? node.gres: t('common.no')}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.resourcesUsed')}</span>
                                        <span>{node.gresUsed.length > 0? node.gresUsed: t('common.no')}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.resourcesUnavail')}</span>
                                        <span>{node.gresDrained}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.lastUsed')}</span>
                                        <span>{new Date(node.lastBusy.number * 1000).toLocaleString()}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>{t('statistics.state')}</span>
                                        <span>{node.state[node.state.length - 1]}</span>
                                    </li>
                                </ul>

                                <div className="chart-container">
                                  <div className="chart">
                                        <h3>{t('statistics.cpuLoad')}</h3>
                                        <ResponsiveContainer width="100%" height={200}>
                                            <AreaChart data={nodeHistory} margin={{ top: 5, right: 10, bottom: 5, left: 0 }}>
                                                <Area type="monotone" name={t('statistics.cpuLoad')} dataKey="cpuLoad" stroke="#8884d8" fill="#8884d8" />
                                                <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                                                <XAxis dataKey="timestamp" />
                                                <YAxis domain={[0, 100]} />
                                                <Tooltip content={<CustomTooltip />} />
                                            </AreaChart>
                                        </ResponsiveContainer>
                                    </div>

                                   <div className="chart">
                                        <h3>{t('statistics.memoryLoad')}</h3>
                                        <ResponsiveContainer width="100%" height={200}>
                                            <AreaChart data={nodeHistory} margin={{ top: 5, right: 10, bottom: 5, left: 0 }}>
                                                <Area type="monotone" name={t('statistics.memoryLoad')} dataKey="ramLoad" stackId={1} stroke="#82ca9d" fill="#82ca9d" />
                                                <Area type="monotone" name={t('statistics.memoryAvailable')} dataKey="totalMemory" stackId={1} stroke="#ffc658" fill="#ffc658" />
                                                <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                                                <XAxis dataKey="timestamp" />
                                                <YAxis />
                                                <Tooltip content={<CustomTooltip />} />
                                            </AreaChart>
                                        </ResponsiveContainer>
                                    </div>
                                </div>
                            </div>
                        );
                    })()
                )}
            </>}
        </div>
    );
}
export default StatisticsDisplay;