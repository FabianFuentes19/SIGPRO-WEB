import React, { useState } from 'react';
import '../css/GestionUsuario.css';

const ModalRegistrarLider = ({ alCerrar, alRegistrar }) => {
  const [datosFormulario, setDatosFormulario] = useState({
    nombre: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    matricula: '',
    correo: '',
    contrasena: ''
  });

  const cambiarValor = (e) => {
    const { name, value } = e.target;
    setDatosFormulario({ ...datosFormulario, [name]: value });
  };

  const guardarLider = (e) => {
    e.preventDefault();
    alRegistrar(datosFormulario);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <h2 className="modal-title">Agregar Líder</h2>
        <form onSubmit={guardarLider} className="modal-form">
          <div className="form-group">
            <label>Nombre*</label>
            <input type="text" name="nombre" value={datosFormulario.nombre} onChange={cambiarValor} required />
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Apellido Paterno*</label>
              <input type="text" name="apellidoPaterno" value={datosFormulario.apellidoPaterno} onChange={cambiarValor} required />
            </div>
            <div className="form-group">
              <label>Apellido Materno*</label>
              <input type="text" name="apellidoMaterno" value={datosFormulario.apellidoMaterno} onChange={cambiarValor} required />
            </div>
          </div>

          <div className="form-group">
            <label>Matrícula*</label>
            <input type="text" name="matricula" placeholder="Ej. 20243DS012" value={datosFormulario.matricula} onChange={cambiarValor} required />
          </div>

          <div className="form-group">
            <label>Correo Electrónico*</label>
            <input type="email" name="correo" placeholder="ejemplo@utez.edu.mx" value={datosFormulario.correo} onChange={cambiarValor} required />
          </div>

          <div className="form-group">
            <label>Contraseña Provisional*</label>
            <input type="password" name="contrasena" value={datosFormulario.contrasena} onChange={cambiarValor} required />
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

export default ModalRegistrarLider;
