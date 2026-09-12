import '../../utils/config'
import { apiUrl } from '../../utils/config';
import ShellTerminal from '../../utils/ShellTerminal';


type ClusterTerminalProps = {
    clusterId: number;
    bindedCluster: string;
}

const ClusterTerminal: React.FC<ClusterTerminalProps> = ({clusterId, bindedCluster}) =>{
    return <ShellTerminal socketUrl={`${apiUrl}/api/ws/admin/terminal?clusterId=${clusterId}&clusterName=${bindedCluster}`} />
};
export default ClusterTerminal;