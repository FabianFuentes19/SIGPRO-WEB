import React from 'react';
import '../css/GestionUsuario.css';
import { AlertTriangle } from 'lucide-react';

const BorrarUsuario = ({ usuario, alCerrar, alConfirmar, tipo = "Usuario" }) => {
    if (!usuario) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-container modal-delete">
                <div className="delete-icon-container">
                    <AlertTriangle size={32} color="#dc2626" />
                </div>

                <h2 className="delete-title">Eliminar {tipo}</h2>
                <p className="delete-subtitle">
                    ¿Estás seguro de que deseas eliminar a este {tipo.toLowerCase()}?
                </p>

                <div className="delete-data-box">
                    <div className="data-row">
                        <span>Nombre</span>
                        <strong>{usuario.nombreCompleto}</strong>
                    </div>
                    <div className="data-row">
                        <span>Matrícula</span>
                        <strong>{usuario.matricula}</strong>
                    </div>
                </div>

                <div className="modal-actions delete-actions">
                    <button type="button" className="btn-cancel-outline" onClick={alCerrar}>
                        Cancelar
                    </button>
                    <button
                        type="button"
                        className="btn-delete-confirm"
                        onClick={() => {
                            // Usamos matricula para el backend
                            alConfirmar(usuario.matricula);
                            alCerrar();
                        }}
                    >
                        Eliminar
                    </button>
                </div>
            </div>
        </div>
    );
};

export default BorrarUsuario;