import '../../utils/config'
import { apiUrl } from '../../utils/config';
import ShellTerminal from '../../utils/ShellTerminal';


const ServerTerminal = () =>{
    return <ShellTerminal socketUrl={`${apiUrl}/api/ws/terminal`} />
};
export default ServerTerminal;