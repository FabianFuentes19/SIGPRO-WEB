import React from "react";
import "../../css/ModalMensajes.css";

const ModalMensajes = ({ titulo, mensaje, onConfirm }) => {
  return (
    <div className="overlay">
      <div className="modal modal-exito">
        {/* Este es el icono Superior Checkmark  */}
        <div className="icon-container-exito">
          <div className="icon-bg-exito">
            <span className="icon-check">✓</span>
          </div>
        </div>

        <h2 className="modal-titulo-exito">{titulo}</h2>
        <p className="modal-mensaje-exito">{mensaje}</p>

        <div className="acciones-exito">
          <button onClick={onConfirm} className="btn-aceptar-exito">
            Aceptar
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalMensajes;