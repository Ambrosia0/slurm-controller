import { SlurmClusterRec } from '../../api/admin/clusters';
import { ClusterUserResponse } from '../../api/user/userApi';
import '../../utils/config'
import { apiUrl } from '../../utils/config';
import ShellTerminal from '../../utils/ShellTerminal';

interface ProfileTerminalProps{
    cluster: ClusterUserResponse;
    bindedCluster: SlurmClusterRec;
}

const ClusterTerminal: React.FC<ProfileTerminalProps> = ({cluster, bindedCluster}) =>{
    return <ShellTerminal socketUrl={`${apiUrl}/api/user/cluster/terminal?clusterId=${cluster.id}&clusterName=${bindedCluster.name}`} />
};
export default ClusterTerminal;