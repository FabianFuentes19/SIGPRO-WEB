import React from 'react';
import './ModalConsultarProyecto.css';

const ModalConsultarProyecto = ({ proyecto, onClose }) => {
  // si no hay un proyecto seleccionado el modal no se renderiza y retorna null
  if (!proyecto) return null;

  return (
    // estilos de boostrap
    <div className="modal-overlay">
      <div className="modal-container">
        <h3>Consultar Proyecto</h3>
        <div className="modal-content">
          <p><strong>Nombre proyecto:</strong> {proyecto.nombre}</p>
          <p><strong>Objetivo:</strong> {proyecto.objetivo}</p>
          <p><strong>Descripción:</strong> {proyecto.descripcion}</p>
          <p><strong>Fecha Inicio:</strong> {proyecto.fechaInicio}</p>
          <p><strong>Fecha Fin:</strong> {proyecto.fechaFin}</p>
          <p><strong>Líder:</strong> {proyecto.lider}</p>
          <p><strong>Presupuesto:</strong> {proyecto.presupuesto}</p>
        </div>
        <div className="modal-actions">
          <button onClick={onClose} className="btn-cerrar">Cerrar</button>
        </div>
      </div>
    </div>
  );
};

export default ModalConsultarProyecto;
