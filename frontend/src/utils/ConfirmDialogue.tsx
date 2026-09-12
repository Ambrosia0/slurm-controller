import '../styles/components/confirmDialogue.css'

interface ConfirmDialogProps {
    message: string;
    onConfirm: () => void;
    onCancel: () => void;
    isOpen: boolean;
}

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({ message, onConfirm, onCancel, isOpen }) => {
    if (!isOpen) return null;
  
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-sm">
            <p className="text-gray-800 text-lg">{message}</p>
            <div className="mt-4 flex justify-end gap-2">
              <button
                onClick={onConfirm}
                className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md"
              >
                Да
              </button>
              <button
                onClick={onCancel}
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-md"
              >
                Отмена
              </button>
            </div>
          </div>
        </div>
      );
  };