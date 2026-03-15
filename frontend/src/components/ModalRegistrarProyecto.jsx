import React, { useState } from 'react';
import './ModalRegistrarProyecto.css';

const ModalRegistrarProyecto = ({ alCerrar, alRegistrar }) => {
  const [datosFormulario, setDatosFormulario] = useState({
    nombre: '',
    objetivo: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    lider: '',
    presupuesto: ''
  });

  const cambiarValor = (e) => {
    const { name, value } = e.target;
    setDatosFormulario({ ...datosFormulario, [name]: value });
  };

  const guardarProyecto = (e) => {
    e.preventDefault();
    alRegistrar(datosFormulario); // Pasamos los datos del formulario (de momento solo en el front)
    alCerrar();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <h2 className="modal-title">Agregar Proyecto</h2>
        <form onSubmit={guardarProyecto} className="modal-form">
          <div className="form-group">
            <label>Nombre proyecto*</label>
            <input type="text" name="nombre" placeholder="Ej. Sistema Administrativo" value={datosFormulario.nombre} onChange={cambiarValor} required />
          </div>

          <div className="form-group">
            <label>Objetivo*</label>
            <input type="text" name="objetivo" placeholder="Ej. El objetivo de este proyecto es . . ." value={datosFormulario.objetivo} onChange={cambiarValor} required />
          </div>

          <div className="form-group">
            <label>Descripción*</label>
            <input type="text" name="descripcion" value={datosFormulario.descripcion} onChange={cambiarValor} required />
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Fecha Inicio*</label>
              <input type="date" name="fechaInicio" value={datosFormulario.fechaInicio} onChange={cambiarValor} required />
            </div>
            <div className="form-group">
              <label>Fecha Fin*</label>
              <input type="date" name="fechaFin" value={datosFormulario.fechaFin} onChange={cambiarValor} required />
            </div>
          </div>

          <div className="form-group">
            <label>Líder*</label>
            <select name="lider" value={datosFormulario.lider} onChange={cambiarValor} required>
              <option value="" disabled>Selecciona al lider</option>
              <option value="Lider 1">Líder 1</option>
              <option value="Lider 2">Líder 2</option>
              {/* Añade más opciones según sea necesario */}
            </select>
          </div>

          <div className="form-group">
            <label>Presupuesto*</label>
            <input type="number" name="presupuesto" placeholder="10,000" value={datosFormulario.presupuesto} onChange={cambiarValor} required />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-cancelar" onClick={alCerrar}>Cancelar</button>
            <button type="submit" className="btn-registrar">Registrar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ModalRegistrarProyecto;
