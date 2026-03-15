import React, { useState, useEffect } from 'react';
import './ModalEditarProyecto.css';

const ModalEditarProyecto = ({ proyecto, alCerrar, alActualizar }) => {
  const [datosFormulario, setDatosFormulario] = useState({
    nombre: '',
    objetivo: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    lider: '',
    presupuesto: ''
  });

  // Cargar los datos del proyecto al abrir el modal, use useEffect, para cargar los datos
  useEffect(() => {
    if (proyecto) {
      setDatosFormulario(proyecto);
    }
  }, [proyecto]);

  // Esto lo puse para actualizar los valores de los imputs
  const cambiarValor = (e) => {
    const { name, value } = e.target;
    setDatosFormulario({ ...datosFormulario, [name]: value });
  };

  // Al enviar el formulario, llama a alActualizar pasando los datos del proyecto editado
  const guardarProyecto = (e) => {
    e.preventDefault();
    alActualizar(datosFormulario); 
    alCerrar();
  };

  // Aqui renderiza el modal
  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <h2 className="modal-title">Editar Proyecto</h2>
        <form onSubmit={guardarProyecto} className="modal-form">
          <div className="form-group">
            <label>Nombre proyecto *</label>
            <input type="text" name="nombre" value={datosFormulario.nombre || ''} onChange={cambiarValor} required />
          </div>

          <div className="form-group">
            <label>Objetivo *</label>
            <input type="text" name="objetivo" value={datosFormulario.objetivo || ''} onChange={cambiarValor} required />
          </div>

          <div className="form-group">
            <label>Descripción *</label>
            <input type="text" name="descripcion" value={datosFormulario.descripcion || ''} onChange={cambiarValor} required />
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Fecha Inicio *</label>
              <input type="date" name="fechaInicio" value={datosFormulario.fechaInicio || ''} onChange={cambiarValor} disabled required />
            </div>
            <div className="form-group">
              <label>Fecha Fin *</label>
              <input type="date" name="fechaFin" value={datosFormulario.fechaFin || ''} onChange={cambiarValor} disabled required />
            </div>
          </div>

          <div className="form-group">
            <label>Líder *</label>
            <input type="text" name="lider" value={datosFormulario.lider || ''} onChange={cambiarValor} disabled required />
          </div>

          <div className="form-group">
            <label>Presupuesto *</label>
            <div className="input-money-wrapper">
              <span className="currency-symbol">$</span>
              <input type="text" name="presupuesto" value={datosFormulario.presupuesto || ''} onChange={cambiarValor} required />
            </div>
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-cancelar" onClick={alCerrar}>Cancelar</button>
            <button type="submit" className="btn-registrar">Actualizar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ModalEditarProyecto;
