import { useState } from "react"

const useModal = () =>{
    const [isOpen, setIsOpen] = useState<boolean>(false);

    const openModal = () => setIsOpen(true);
    const closeModal = () => setIsOpen(true);

    return {isOpen, openModal, closeModal};
}

export default useModal;