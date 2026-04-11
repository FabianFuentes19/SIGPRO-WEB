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
    presupuestoInicial: '',
    presupuestoAutorizado: ''
  });

  // Estados para validación de inputs
  const [touched, setTouched] = useState({
    nombre: false,
    objetivoGeneral: false,
    descripcion: false,
    presupuestoInicial: false,
    presupuestoAutorizado: false
  });

  // Cargar los datos del proyecto al abrir el modal, use useEffect, para cargar los datos
  useEffect(() => {
    if (proyecto) {
      setDatosFormulario({
        ...proyecto,
        presupuestoAutorizado: proyecto.presupuestoAutorizado || proyecto.presupuestoInicial
      });
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

    // se valida los input antes de registrar
    setTouched({
      nombre: true,
      objetivoGeneral: true,
      descripcion: true,
      presupuestoInicial: true
    });

    // Validar campos obligatorios antes de enviar
    const camposVacios = [];
    if (!(datosFormulario.nombre || '').trim()) camposVacios.push("Nombre");
    if (!(datosFormulario.objetivoGeneral || '').trim()) camposVacios.push("Objetivo");
    if (!(datosFormulario.descripcion || '').trim()) camposVacios.push("Descripción");
    if (!datosFormulario.presupuestoAutorizado && datosFormulario.presupuestoAutorizado !== 0) camposVacios.push("Presupuesto Autorizado");

    if (camposVacios.length > 0) {
      setModalMensajes({
        titulo: "Datos incompletos",
        mensaje: `Faltan campos obligatorios: ${camposVacios.join(", ")}`,
        tipo: "error"
      });
      return;
    }

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

    // Enviamos el presupuestoInicial como 'presupuesto' para el DTO
    alActualizar({
      ...datosFormulario,
      presupuesto: datosFormulario.presupuestoAutorizado
    });
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
            />
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Fecha Inicio *</label>
              <input type="date" name="fechaInicio" className="form-control" value={datosFormulario.fechaInicio || ''} onChange={cambiarValor} disabled  />
            </div>
            <div className="form-group">
              <label>Fecha Fin *</label>
              <input type="date" name="fechaFin" className="form-control" value={datosFormulario.fechaFin || ''} onChange={cambiarValor} disabled  />
            </div>
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Presupuesto Inicial (Histórico)</label>
              <div className="input-money-wrapper form-control disabled-looking">
                <span className="currency-symbol">$</span>
                <input
                  type="text"
                  value={datosFormulario.presupuestoInicial || ''}
                  disabled
                  className="read-only-input"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Presupuesto Autorizado *</label>
              <div className={`input-money-wrapper form-control ${!touched.presupuestoAutorizado ? "" : String(datosFormulario.presupuestoAutorizado).trim() === "" ? "invalido" : "valido"
                }`}>
                <span className="currency-symbol">$</span>
                <input
                  type="text"
                  name="presupuestoAutorizado"
                  value={datosFormulario.presupuestoAutorizado || ''}
                  onChange={cambiarValor}
                  onBlur={() => setTouched({ ...touched, presupuestoAutorizado: true })}
                />
              </div>
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
