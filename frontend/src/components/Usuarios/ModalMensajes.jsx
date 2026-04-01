import React from "react";
import "../../css/ModalMensajes.css";

const ModalMensajes = ({ titulo, mensaje, tipo = "exito", onConfirm }) => {
  const esExito = tipo === "exito";

  return (
    <div className="overlay">
      <div className={esExito ? "modal-exito" : "modal-error"}>
        <div className={esExito ? "icon-container-exito" : "icon-container-error"}>
          <div className={esExito ? "icon-bg-exito" : "icon-bg-error"}>
            <span className={esExito ? "icon-check" : "icon-cross"}>
              {esExito ? "✓" : "✕"}
            </span>
          </div>
        </div>

        <h2 className={esExito ? "modal-titulo-exito" : "modal-titulo-error"}>{titulo}</h2>
        <p className={esExito ? "modal-mensaje-exito" : "modal-mensaje-error"}>{mensaje}</p>

        <div className={esExito ? "acciones-exito" : "acciones-error"}>
          <button
            onClick={onConfirm}
            className={esExito ? "btn-aceptar-exito" : "btn-aceptar-error"}
          >
            Aceptar
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModalMensajes;