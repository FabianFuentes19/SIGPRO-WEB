import React from "react";
import "../../css/ModalMensajes.css";

const ModalMensajes = ({ titulo, mensaje, tipo = "exito", onConfirm }) => {
  const getIcon = () => {
    if (tipo === "exito") return "✓";
    if (tipo === "advertencia") return "!";
    return "✕";
  };

  const getIconClass = () => {
    if (tipo === "exito") return "icon-check";
    if (tipo === "advertencia") return "icon-warn";
    return "icon-cross";
  };

  return (
    <div className="overlay">
      <div className={`modal-${tipo}`}>
        <div className={`icon-container-${tipo}`}>
          <div className={`icon-bg-${tipo}`}>
            <span className={getIconClass()}>
              {getIcon()}
            </span>
          </div>
        </div>

        <h2 className={`modal-titulo-${tipo}`}>{titulo}</h2>
        <p className={`modal-mensaje-${tipo}`}>{mensaje}</p>

        <div className={`acciones-${tipo}`}>
          <button
            onClick={onConfirm}
            className={`btn-aceptar-${tipo}`}
          >
            Aceptar
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalMensajes;