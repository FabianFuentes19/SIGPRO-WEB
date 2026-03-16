import React, { useState, useEffect, useRef } from 'react';
import {Pencil, Trash2, Eye, History, MoreVertical} from 'lucide-react';

const MenuOpcionesUsuario = ({ usuario, onAction }) => {
    const [abierto, setAbierto] = useState(false);
    const menuRef = useRef(null);

    // Cerrar al hacer clic fuera
    useEffect(() => {
        const handleClickFuera = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setAbierto(false);
            }
        };
        document.addEventListener('mousedown', handleClickFuera);
        return () => document.removeEventListener('mousedown', handleClickFuera);
    }, []);

    const opciones = [
        { id: 'editar', label: 'Editar', icon: <Pencil size={16} /> },
        { id: 'borrar', label: 'Borrar', icon: <Trash2 size={16} /> },
        { id: 'detalles', label: 'Ver detalles', icon: <Eye size={16} /> },
        { id: 'historial', label: 'Ver historial', icon: <History size={16} /> },
    ];

    return (
        <div className="dropdown-container" ref={menuRef}>
            <div className="more-dots" onClick={() => setAbierto(!abierto)}>
                <MoreVertical size={20} />
            </div>

            {abierto && (
                <div className="dropdown-menu-opciones">
                    {opciones.map((opt) => (
                        <div
                            key={opt.id}
                            className="dropdown-item"
                            onClick={() => {
                                onAction(opt.id, usuario);
                                setAbierto(false);
                            }}
                        >
                            {opt.icon}
                            <span>{opt.label}</span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};
export default MenuOpcionesUsuario;