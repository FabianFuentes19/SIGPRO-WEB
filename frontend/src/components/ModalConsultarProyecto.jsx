import React from 'react';
import '../css/ModalConsultarProyecto.css';

const ModalConsultarProyecto = ({ proyecto, onClose }) => {
  // si no hay un proyecto seleccionado el modal no se renderiza y retorna null
  if (!proyecto) return null;

  return (
    // estilos de bootstrap
    <div className="modal-overlay">
      <div className="modal-container">
        <h3 className="modal-title">Consultar Proyecto</h3>
        <form className="modal-form">
          <div className="form-group">
            <label>Nombre proyecto:</label>
            <input type="text" value={proyecto.nombre} disabled />
          </div>
          <div className="form-group">
            <label>Objetivo:</label>
            <input type="text" value={proyecto.objetivoGeneral} disabled />
          </div>
          <div className="form-group">
            <label>Descripción:</label>
            <textarea value={proyecto.descripcion} disabled />
          </div>
          <div className="form-row-2-col">
            <div className="form-group">
              <label>Fecha Inicio:</label>
              <input type="date" value={proyecto.fechaInicio} disabled />
            </div>
            <div className="form-group">
              <label>Fecha Fin:</label>
              <input type="date" value={proyecto.fechaFin} disabled />
            </div>
          </div>
          <div className="form-group">
            <label>Líder:</label>
            <input type="text" value={proyecto.liderNombre} disabled />
          </div>
          <div className="form-group input-money-wrapper">
            <label>Presupuesto:</label>
            <span className="currency-symbol">$</span>
            <input type="number" value={proyecto.presupuesto} disabled />
          </div>
        </form>
        <div className="modal-actions">
          <button onClick={onClose} className="btn-cancelar">Cerrar</button>
        </div>
      </div>
    </div>
  );
};

export default ModalConsultarProyecto;
