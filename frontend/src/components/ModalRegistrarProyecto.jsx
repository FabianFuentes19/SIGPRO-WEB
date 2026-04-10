import React, { useEffect, useState } from 'react';
import '../css/ModalRegistrarProyecto.css';
import { obtenerUsuarios } from '../services/api';

// recibe 2 props al cerrar y al registra , que son funciones
const ModalRegistrarProyecto = ({ alCerrar, alRegistrar, setModalMensajes }) => {
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

  // Estados para validación de inputs
  const [touched, setTouched] = useState({
    nombre: false,
    objetivoGeneral: false,
    descripcion: false,
    fechaInicio: false,
    fechaFin: false,
    liderId: false,
    presupuesto: false
  });

  const [lideres, setLideres] = useState([]);

  useEffect(() => {
    const cargarLideres = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/usuarios/lideres/sin-proyecto", {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        });

        if (response.ok) {
          const data = await response.json();
          setLideres(data);
        } else {
          setLideres("No hay lideres disponibles");
        }
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

 // se valida los input antes de registrar
    setTouched({
      nombre: true,
      objetivoGeneral: true,
      descripcion: true,
      fechaInicio: true,
      fechaFin: true,
      liderId: true,
      presupuesto: true
    });

    // Validar campos obligatorios antes de enviar al backend
    const camposVacios = [];
    if (!datosFormulario.nombre.trim()) camposVacios.push("Nombre");
    if (!datosFormulario.objetivoGeneral.trim()) camposVacios.push("Objetivo");
    if (!datosFormulario.descripcion.trim()) camposVacios.push("Descripción");
    if (!datosFormulario.fechaInicio) camposVacios.push("Fecha inicio");
    if (!datosFormulario.fechaFin) camposVacios.push("Fecha fin");
    if (!datosFormulario.liderId) camposVacios.push("Líder");
    if (!datosFormulario.presupuesto) camposVacios.push("Presupuesto");

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

    // valida que la fecha de inicio no esté en el pasado
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaInicioElegida = new Date(datosFormulario.fechaInicio + "T00:00:00");

    if (fechaInicioElegida < hoy) {
      setModalMensajes({
        titulo: "Fecha Invalida",
        mensaje: "No puedes registrar un proyecto con una fecha de inicio en el pasado.",
        tipo: "error"
      });
      return;
    }

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
            <input
              type="text"
              name="nombre"
              className={`form-control ${!touched.nombre ? "" : datosFormulario.nombre.trim() === "" ? "invalido" : "valido"
                }`}
              placeholder="Ej. Sistema Administrativo"
              value={datosFormulario.nombre}
              onChange={cambiarValor}
              onBlur={() => setTouched({ ...touched, nombre: true })}
            />
          </div>

          <div className="form-group">
            <label>Objetivo*</label>
            <input
              type="text"
              name="objetivoGeneral"
              className={`form-control ${!touched.objetivoGeneral ? "" : datosFormulario.objetivoGeneral.trim() === "" ? "invalido" : "valido"
                }`}
              placeholder="Ej. El objetivo de este proyecto es . . ."
              value={datosFormulario.objetivoGeneral}
              onChange={cambiarValor}
              onBlur={() => setTouched({ ...touched, objetivoGeneral: true })}
            />
          </div>

          <div className="form-group">
            <label>Descripción*</label>
            <input
              type="text"
              name="descripcion"
              className={`form-control ${!touched.descripcion ? "" : datosFormulario.descripcion.trim() === "" ? "invalido" : "valido"
                }`}
              placeholder="Ej. Este proyecto trata de  . . ."
              value={datosFormulario.descripcion}
              onChange={cambiarValor}
              onBlur={() => setTouched({ ...touched, descripcion: true })}
            />
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Fecha Inicio*</label>
              <input
                type="date"
                name="fechaInicio"
                className={`form-control ${!touched.fechaInicio ? "" : datosFormulario.fechaInicio === "" ? "invalido" : "valido"
                  }`}
                value={datosFormulario.fechaInicio}
                onChange={cambiarValor}
                onBlur={() => setTouched({ ...touched, fechaInicio: true })}
                min={new Date().toISOString().split('T')[0]}
              />
            </div>
            <div className="form-group">
              <label>Fecha Fin*</label>
              <input
                type="date"
                name="fechaFin"
                className={`form-control ${!touched.fechaFin ? "" : datosFormulario.fechaFin === "" ? "invalido" : "valido"
                  }`}
                value={datosFormulario.fechaFin}
                onChange={cambiarValor}
                onBlur={() => setTouched({ ...touched, fechaFin: true })}
              />
            </div>
          </div>

          <div className="form-row-2-col">
            <div className="form-group">
              <label>Líder*</label>
              <select
                name="liderId"
                className={`form-control ${!touched.liderId ? "" : datosFormulario.liderId === "" ? "invalido" : "valido"
                  }`}
                value={datosFormulario.liderId}
                onChange={cambiarValor}
                onBlur={() => setTouched({ ...touched, liderId: true })}
              >
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
              <input
                type="number"
                name="presupuesto"
                className={`form-control ${!touched.presupuesto ? "" : String(datosFormulario.presupuesto).trim() === "" ? "invalido" : "valido"
                  }`}
                placeholder="10,000"
                value={datosFormulario.presupuesto}
                onChange={cambiarValor}
                onBlur={() => setTouched({ ...touched, presupuesto: true })}
              />
            </div>
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
