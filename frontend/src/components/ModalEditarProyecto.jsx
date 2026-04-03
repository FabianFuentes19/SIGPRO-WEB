import React, { useState, useEffect } from 'react';
import '../css/ModalEditarProyecto.css';

const ModalEditarProyecto = ({ proyecto, alCerrar, alActualizar, setModalMensajes }) => {
  const [datosFormulario, setDatosFormulario] = useState({
    nombre: '',
    objetivoGeneral: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    liderNombre: '',
    presupuesto: ''
  });

  // Estados para validación de inputs
  const [touched, setTouched] = useState({
    nombre: false,
    objetivoGeneral: false,
    descripcion: false,
    presupuesto: false
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

    // Forzamos la validacion visual de los campos editables al intentar guardar
    setTouched({
      nombre: true,
      objetivoGeneral: true,
      descripcion: true,
      presupuesto: true
    });

    // valida que la fecha de fin no sea anterior a la de inicio
    if (datosFormulario.fechaInicio && datosFormulario.fechaFin) {
      const inicio = new Date(datosFormulario.fechaInicio);
      const fin = new Date(datosFormulario.fechaFin);
      if (fin < inicio) {
        setModalMensajes({
          titulo: "Fecha Inválida",
          mensaje: "La fecha de fin no puede ser anterior a la fecha de inicio",
          tipo: "error"
        });
        return;
      }
    }

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
            <input
              type="text"
              name="nombre"
              className={`form-control ${!touched.nombre ? "" : datosFormulario.nombre.trim() === "" ? "invalido" : "valido"
                }`}
              value={datosFormulario.nombre || ''}
              onChange={cambiarValor}
              onBlur={() => setTouched({ ...touched, nombre: true })}
              required
            />
          </div>

          <div className="form-group">
            <label>Objetivo *</label>
            <input
              type="text"
              name="objetivoGeneral"
              className={`form-control ${!touched.objetivoGeneral ? "" : datosFormulario.objetivoGeneral.trim() === "" ? "invalido" : "valido"
                }`}
              value={datosFormulario.objetivoGeneral || ''}
              onChange={cambiarValor}
              onBlur={() => setTouched({ ...touched, objetivoGeneral: true })}
              required
            />
          </div>

          <div className="form-group">
            <label>Descripción *</label>
            <input
              type="text"
              name="descripcion"
              className={`form-control ${!touched.descripcion ? "" : datosFormulario.descripcion.trim() === "" ? "invalido" : "valido"
                }`}
              value={datosFormulario.descripcion || ''}
              onChange={cambiarValor}
              onBlur={() => setTouched({ ...touched, descripcion: true })}
              required
            />
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Fecha Inicio *</label>
              <input type="date" name="fechaInicio" className="form-control" value={datosFormulario.fechaInicio || ''} onChange={cambiarValor} disabled required />
            </div>
            <div className="form-group">
              <label>Fecha Fin *</label>
              <input type="date" name="fechaFin" className="form-control" value={datosFormulario.fechaFin || ''} onChange={cambiarValor} disabled required />
            </div>
          </div>

          <div className="form-group">
            <label>Líder *</label>
            <input type="text" name="liderNombre" className="form-control" value={datosFormulario.liderNombre || ''} onChange={cambiarValor} disabled required />
          </div>

          <div className="form-group">
            <label>Presupuesto *</label>
            <div className={`input-money-wrapper form-control ${!touched.presupuesto ? "" : String(datosFormulario.presupuesto).trim() === "" ? "invalido" : "valido"
              }`}>
              <span className="currency-symbol">$</span>
              <input
                type="text"
                name="presupuesto"
                value={datosFormulario.presupuesto || ''}
                onChange={cambiarValor}
                onBlur={() => setTouched({ ...touched, presupuesto: true })}
                required
              />
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
