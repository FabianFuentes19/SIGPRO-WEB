import React, { useState, useEffect } from 'react';
import '../css/GestionUsuario.css';

const EditarUsuario = ({ usuario, alCerrar, alGuardar, tipo = "Usuario" }) => {
    const [datosFormulario, setDatosFormulario] = useState({
        nombreCompleto: '',
        matricula: '',
        cuatrimestre: '',
        grupo: '',
        carrera: '',
        puesto: '',
        salarioQuincenal: ''
    });

    // Estados para validaciónde imputs
    const [touched, setTouched] = useState({
        nombreCompleto: false,
        carrera: false,
        cuatrimestre: false,
        grupo: false,
        puesto: false,
        salarioQuincenal: false
    });

    useEffect(() => {
        if (usuario) {
            setDatosFormulario({
                nombreCompleto: usuario.nombreCompleto || '',
                matricula: usuario.matricula || '',
                cuatrimestre: usuario.cuatrimestre || '',
                grupo: usuario.grupo || '',
                carrera: usuario.carrera || '',
                puesto: usuario.puesto || '',
                salarioQuincenal: usuario.salarioQuincenal || ''
            });
        }
    }, [usuario]);

    const cambiarValor = (e) => {
        const { name, value } = e.target;
        setDatosFormulario({ ...datosFormulario, [name]: value });
    };


  const enviarEdicion = (e) => {
    e.preventDefault();

     // se valida los input antes de registrar
    setTouched({
        nombreCompleto: true,
        carrera: true,
        cuatrimestre: true,
        grupo: true,
        puesto: true,
        salarioQuincenal: true
    });

    const payload = {
      nombreCompleto: datosFormulario.nombreCompleto,
      matricula: datosFormulario.matricula,
      contrasena: usuario?.contrasena || "1234", 
      grupo: datosFormulario.grupo,
      carrera: datosFormulario.carrera,
      cuatrimestre: parseInt(datosFormulario.cuatrimestre, 10),
      puesto: datosFormulario.puesto,
      salarioQuincenal: datosFormulario.salarioQuincenal
        ? parseFloat(datosFormulario.salarioQuincenal)
        : 0,
      estado: "ACTIVO",
      rol: { id: 2, nombre: "LIDER" } 
    };
    alGuardar(payload);
  };


    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2 className="modal-title">Editar {tipo}</h2>
                <form onSubmit={enviarEdicion} className="modal-form">

                    <div className="form-group">
                        <label>Nombre completo*</label>
                        <input
                            type="text"
                            name="nombreCompleto"
                            className={`form-control ${
                                !touched.nombreCompleto ? "" : datosFormulario.nombreCompleto.trim() === "" ? "invalido" : "valido"
                            }`}
                            value={datosFormulario.nombreCompleto}
                            onChange={cambiarValor}
                            onBlur={() => setTouched({...touched, nombreCompleto: true})}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Matrícula</label>
                        <input
                            type="text"
                            name="matricula"
                            className="input-readonly form-control"
                            value={datosFormulario.matricula}
                            readOnly
                        />
                    </div>

                    <div className="form-group">
                        <label>Carrera*</label>
                        <select 
                            name="carrera" 
                            className={`form-control ${
                                !touched.carrera ? "" : datosFormulario.carrera === "" ? "invalido" : "valido"
                            }`}
                            value={datosFormulario.carrera} 
                            onChange={cambiarValor} 
                            onBlur={() => setTouched({...touched, carrera: true})}
                            required
                        >
                            <option value="DS">Desarrollo de software</option>
                            <option value="DD">Diseño digital</option>
                        </select>
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Cuatrimestre*</label>
                            <select 
                                name="cuatrimestre" 
                                className={`form-control ${
                                    !touched.cuatrimestre ? "" : datosFormulario.cuatrimestre === "" ? "invalido" : "valido"
                                }`}
                                value={datosFormulario.cuatrimestre} 
                                onChange={cambiarValor} 
                                onBlur={() => setTouched({...touched, cuatrimestre: true})}
                                required
                            >
                                {[1,2,3,4,5,6,7,8,9,10,11].map(n => <option key={n} value={n}>{n}°</option>)}
                            </select>
                        </div>
                        <div className="form-group">
                            <label>Grupo*</label>
                            <select 
                                name="grupo" 
                                className={`form-control ${
                                    !touched.grupo ? "" : datosFormulario.grupo === "" ? "invalido" : "valido"
                                }`}
                                value={datosFormulario.grupo} 
                                onChange={cambiarValor} 
                                onBlur={() => setTouched({...touched, grupo: true})}
                                required
                            >
                                {['A','B','C','D','E','F'].map(g => <option key={g} value={g}>{g}</option>)}
                            </select>
                        </div>
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Puesto</label>
                            <input
                                type="text"
                                name="puesto"
                                className={`form-control ${
                                    !touched.puesto ? "" : datosFormulario.puesto.trim() === "" ? "invalido" : "valido"
                                }`}
                                value={datosFormulario.puesto}
                                onChange={cambiarValor}
                                onBlur={() => setTouched({...touched, puesto: true})}
                            />
                        </div>
                        <div className="form-group">
                            <label>Salario quincenal</label>
                            <input
                                type="number"
                                name="salarioQuincenal"
                                className={`form-control ${
                                    !touched.salarioQuincenal ? "" : String(datosFormulario.salarioQuincenal).trim() === "" ? "invalido" : "valido"
                                }`}
                                value={datosFormulario.salarioQuincenal}
                                onChange={cambiarValor}
                                onBlur={() => setTouched({...touched, salarioQuincenal: true})}
                            />
                        </div>
                    </div>

                    <div className="modal-actions">
                        <button type="button" className="btn-cancelar" onClick={alCerrar}>Cancelar</button>
                        <button type="submit" className="btn-registrar">Guardar Cambios</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditarUsuario;