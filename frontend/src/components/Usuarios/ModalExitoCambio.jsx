import React from 'react';
import "../../css/CerrarSesionModal.css";

const ModalExitoCambio = ({ alPerfil, alInicio }) => {
  return (
    <div className="modal-overlay">
      <div className="logout-card" style={{ textAlign: "center" }}>
        <h2 className="logout-title" style={{ color: "#28a745", marginBottom: "10px" }}>¡Éxito!</h2>
        <p style={{ marginBottom: "25px", color: "#666", fontSize: "16px" }}>
          Tu contraseña ha sido actualizada correctamente.<br/>
          ¿Qué deseas hacer a continuación?
        </p>
        
        <div className="logout-buttons">
          <button 
            className="btn-cancelar" 
            onClick={alPerfil}
            style={{ padding: "10px 20px" }}
          >
            Ir a Perfil
          </button>
          
          <button 
            className="btn-aceptar" 
            onClick={alInicio}
            style={{ padding: "10px 20px", backgroundColor: "#0056b3" }}
          >
            Iniciar Sesión
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalExitoCambio;
