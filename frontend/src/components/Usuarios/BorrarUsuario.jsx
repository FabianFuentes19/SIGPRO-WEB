import React from 'react';
import './GestionUsuarios.css';
import { AlertTriangle } from 'lucide-react';

const BorrarUsuario = ({ usuario, alCerrar, alConfirmar }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-container modal-delete">
                <div className="delete-icon-container">
                    <AlertTriangle size={32} color="#d9534f" />
                </div>

                <h2 className="delete-title">Eliminar miembro</h2>
                <p className="delete-subtitle">
                    ¿Estás seguro de que deseas eliminar a este miembro?
                </p>

                {/* Contenedor gris de datos clave */}
                <div className="delete-data-box">
                    <div className="data-row">
                        <span>Nombre</span>
                        <strong>{usuario?.nombreCompleto || "Juan Pérez García"}</strong>
                    </div>
                    <div className="data-row">
                        <span>Matrícula:</span>
                        <strong>{usuario?.matricula || "20213UT0045"}</strong>
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
                            alConfirmar(usuario.id);
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