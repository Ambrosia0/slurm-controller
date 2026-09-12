import "../../styles/statistics.css"
import "../../styles/components/list.css"

import { useState, useEffect } from "react";
import { AreaChart, Area, CartesianGrid, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';
import { apiUrl } from '../../utils/config';
import CustomTooltip from "../../utils/CustomTooltip";
import { Statistics } from "../../utils/Interfaces";

type StatisticsProps = {
    clusterId: number;
    bindedCluster: string;
}

const StatisticsDisplay: React.FC<StatisticsProps> = ({ clusterId, bindedCluster }) => {
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
            console.log('Getting statistics');
        }

        socket.onmessage = (event: MessageEvent) => {
            const newStatistics: Statistics = JSON.parse(event.data);
            setStatistics(prevStatistics => {
                const update = [...prevStatistics, newStatistics];
                return update.length > numberOfPoints ? update.slice(-numberOfPoints) : update;
            })
        }

        socket.onerror = (error) => {
            console.error('Ошибка, не удается установить соединение через WebSocket: ', error);
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
            { name: 'Выполнено', value: statistics[statistics.length - 1].jobsCompleted },
            { name: 'Провалено', value: statistics[statistics.length - 1].jobsFailed },
            { name: 'Отменено', value: statistics[statistics.length - 1].jobsCancelled },
            { name: 'Выполняется', value: statistics[statistics.length - 1].jobsRunning },
            { name: 'Начато', value: statistics[statistics.length - 1].jobsStarted },
            { name: 'Всего', value: statistics[statistics.length - 1].jobsSubmitted },
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
                        Статистика по кластеру
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
                        <h2>Статистика по кластеру</h2>
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
                            <span>Всего задач выполнено:</span>
                            <span>{statistics[statistics.length - 1].jobsCompleted}</span>
                        </div>
                        <div className="list-item">
                            <span>Всего задач провалено:</span>
                            <span>{statistics[statistics.length - 1].jobsFailed}</span>
                        </div>
                        <div className="list-item">
                            <span>Задач выполняется:</span>
                            <span>{statistics[statistics.length - 1].jobsRunning}</span>
                        </div>
                        <div className="list-item">
                            <span>Задач начато:</span>
                            <span>{statistics[statistics.length - 1].jobsStarted}</span>
                        </div>
                        <div className="list-item">
                            <span>Всего задач:</span>
                            <span>{statistics[statistics.length - 1].jobsSubmitted}</span>
                        </div>
                    </div>
                ) : (
                    (() => {
                        const node = statistics[statistics.length - 1].nodes.find(n => n.hostname === selectedTab);
                        if (!node) return <div>Узел не найден</div>;

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
                                <h2>Состояние узла: {node.name ?? node.hostname}</h2>
                                <ul className="list">
                                    <li className="list-item">
                                        <span>Время запуска:</span>
                                        <span>{new Date(node.bootTime.number * 1000).toLocaleString()}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Число сокетов (физических процессоров):</span>
                                        <span>{node.sockets}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Число логических процессоров:</span>
                                        <span>{node.cpus}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Число ядер на процессор:</span>
                                        <span>{node.cores}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Число потоков:</span>
                                        <span>{node.threads}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Ресурсы:</span>
                                        <span>{node.gres.length > 0? node.gres: "Нет"}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Ресурсы использованы:</span>
                                        <span>{node.gresUsed.length > 0? node.gresUsed: "Нет"}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Ресурсы недоступны:</span>
                                        <span>{node.gresDrained}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Последнее использование:</span>
                                        <span>{new Date(node.lastBusy.number * 1000).toLocaleString()}</span>
                                    </li>
                                    <li className="list-item">
                                        <span>Состояние:</span>
                                        <span>{node.state[node.state.length - 1]}</span>
                                    </li>
                                </ul>

                                <div className="chart-container">
                                    <div className="chart">
                                        <h3>Загрузка процессоров (%)</h3>
                                        <ResponsiveContainer width="100%" height={200}>
                                            <AreaChart data={nodeHistory} margin={{ top: 5, right: 10, bottom: 5, left: 0 }}>
                                                <Area type="monotone" name="Загрузка процессоров" dataKey="cpuLoad" stroke="#8884d8" fill="#8884d8" />
                                                <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                                                <XAxis dataKey="timestamp" />
                                                <YAxis domain={[0, 100]} />
                                                <Tooltip content={<CustomTooltip />} />
                                            </AreaChart>
                                        </ResponsiveContainer>
                                    </div>

                                    <div className="chart">
                                        <h3>Загрузка памяти (МБ)</h3>
                                        <ResponsiveContainer width="100%" height={200}>
                                            <AreaChart data={nodeHistory} margin={{ top: 5, right: 10, bottom: 5, left: 0 }}>
                                                <Area type="monotone" name="Загрузка памяти" dataKey="ramLoad" stackId={1} stroke="#82ca9d" fill="#82ca9d" />
                                                <Area type="monotone" name="Доступно памяти" dataKey="totalMemory" stackId={1} stroke="#ffc658" fill="#ffc658" />
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