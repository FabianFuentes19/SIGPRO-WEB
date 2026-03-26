import React from 'react';
import "../../css/CerrarSesionModal.css";


const ModalCerrarSesion = ({ alCancelar, alAceptar }) => {
  return (
    <div className="modal-overlay">
      <div className="logout-card">
        <h2 className="logout-title">¿Deseas cerrar sesión?</h2>
        
        <div className="logout-buttons">
          <button 
            className="btn-cancelar" 
            onClick={alCancelar}
          >
            Cancelar
          </button>
          
          <button 
            className="btn-aceptar" 
            onClick={alAceptar}
          >
            Aceptar
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalCerrarSesion;