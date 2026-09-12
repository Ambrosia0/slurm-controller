import '../styles/components/modalForm.css'
import ReactDOM from "react-dom";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
}
  
  const ModalForm: React.FC<ModalProps> = ({ isOpen, onClose, children }) => {
    if (!isOpen) return null;
  
    return ReactDOM.createPortal(
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <button className="close-button" onClick={onClose} aria-label="Close modal">
            &times;
          </button>
          {children}
        </div>
      </div>,
      document.body
    );
  };
  
  export default ModalForm;

  