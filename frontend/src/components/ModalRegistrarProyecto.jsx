import React, { useEffect, useState } from 'react';
import '../css/ModalRegistrarProyecto.css';
import { obtenerUsuarios } from '../services/api';

// recibe 2 props al cerrar y al registra , que son funciones
const ModalRegistrarProyecto = ({ alCerrar, alRegistrar }) => {
  //hook useState, 
  const [datosFormulario, setDatosFormulario] = useState({
    nombre: '',
    objetivoGeneral: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    liderId: '',
    presupuesto: ''
  });

  const [lideres, setLideres] = useState([]);

  useEffect(() => {
    const cargarLideres = async () => {
      try {
        const data = await obtenerUsuarios("LIDER");
        const activos = (Array.isArray(data) ? data : []).filter((u) => (u?.estado || "").toUpperCase() === "ACTIVO");
        setLideres(activos);
      } catch (error) {
        console.error("Error al cargar líderes:", error);
        setLideres([]);
      }
    };
    cargarLideres();
  }, []);

  const cambiarValor = (e) => {
    const { name, value } = e.target;
    setDatosFormulario({ ...datosFormulario, [name]: value });
  };

  const guardarProyecto = (e) => {
    e.preventDefault();
    const payload = {
      ...datosFormulario,
      liderId: datosFormulario.liderId ? Number(datosFormulario.liderId) : null,
      presupuesto: datosFormulario.presupuesto ? Number(datosFormulario.presupuesto) : null,
    };
    alRegistrar(payload); 
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
            <input type="text" name="objetivoGeneral" placeholder="Ej. El objetivo de este proyecto es . . ." value={datosFormulario.objetivoGeneral} onChange={cambiarValor} required />
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
            <select name="liderId" value={datosFormulario.liderId} onChange={cambiarValor} required>
              <option value="" disabled>Selecciona al líder</option>
              {lideres.map((l) => (
                <option key={l.id || l.matricula} value={l.id}>
                  {l.nombreCompleto} ({l.matricula})
                </option>
              ))}
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
