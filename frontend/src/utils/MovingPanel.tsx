import "../styles/components/movingPanel.css"
import { useEffect } from "react";

interface MovingPanelProps{
    isOpen: boolean;
    onClose: () => void;
    children: React.ReactNode;
}

const MovingPanelContent: React.FC<MovingPanelProps> = ({isOpen, onClose, children}) =>{
    useEffect(() => {
        if (isOpen) {
            document.getElementById('panel')?.classList.add('active');
            document.getElementById('panel-content')?.classList.add('active');
        } else {
            document.getElementById('panel')?.classList.remove('active');
            document.getElementById('panel-content')?.classList.remove('active');
        }
    }, [isOpen])

    return(
        <div id="panel" className="dynamic-panel-container">
        <div onClick={onClose} className="dynamic-panel-overlay"></div>
        <div id="panel-content" className="dynamic-panel">
            <button onClick={onClose} className="dynamic-panel-toggle" aria-label="Toggle panel">
                <svg className="arrow-icon" viewBox="0 0 24 24">
                    <path d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6z"></path>
                </svg>
                
            </button>
                {isOpen &&
                    <div className="dynamic-panel-content">
                        {children}
                    </div>
                }
        </div>
    </div>
    )
}

export default MovingPanelContent;