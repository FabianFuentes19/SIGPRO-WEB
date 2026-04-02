import React, { useState } from 'react';
import '../css/GestionUsuario.css';
import { Eye, EyeOff } from 'lucide-react';

const AgregarUsuario = ({ alCerrar, alRegistrar, tipo = "Usuario" }) => {

    const [datosFormulario, setDatosFormulario] = useState({
        nombreCompleto: '',
        matricula: '',
        cuatrimestre: '',
        grupo: '',
        carrera: '',
        puesto: '',
        salarioQuincenal: '',
        fechaIngreso: '',
        contrasena: ''
    });

    const cambiarValor = (e) => {
        const { name, value } = e.target;
        setDatosFormulario({ ...datosFormulario, [name]: value });
    };

    const guardarUsuario = (e) => {
        e.preventDefault();
        // se valida los input antes de registrar
        setTouched({
            nombreCompleto: true,
            matricula: true,
            contrasena: true,
            carrera: true,
            cuatrimestre: true,
            grupo: true,
            puesto: true,
            salarioQuincenal: true
        });

        const payload = {
            ...datosFormulario,
            cuatrimestre: parseInt(datosFormulario.cuatrimestre, 10),
            salarioQuincenal: datosFormulario.salarioQuincenal ? parseFloat(datosFormulario.salarioQuincenal) : null
        };
        alRegistrar(payload);
    };

    const [mostrarPassword, setMostrarPassword] = useState(false);
    
    // Estados para validación de input
    const [touched, setTouched] = useState({
        nombreCompleto: false,
        matricula: false,
        contrasena: false,
        carrera: false,
        cuatrimestre: false,
        grupo: false,
        puesto: false,
        salarioQuincenal: false
    });

    const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2 className="modal-title">Agregar {tipo}</h2>
                <form onSubmit={guardarUsuario} className="modal-form">

                    <div className="form-group">
                        <label>Nombre completo*</label>
                        <input
                            type="text"
                            name="nombreCompleto"
                            className={`form-control ${
                                !touched.nombreCompleto ? "" : datosFormulario.nombreCompleto.trim() === "" ? "invalido" : "valido"
                            }`}
                            placeholder="Ej. Juan Perez"
                            value={datosFormulario.nombreCompleto}
                            onChange={cambiarValor}
                            onBlur={() => setTouched({...touched, nombreCompleto: true})}
                            required
                        />
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Matrícula*</label>
                            <input
                                type="text"
                                name="matricula"
                                className={`form-control ${
                                    !touched.matricula ? "" : datosFormulario.matricula.trim() === "" ? "invalido" : "valido"
                                }`}
                                placeholder="Ej. 20243ds001"
                                value={datosFormulario.matricula}
                                onChange={cambiarValor}
                                onBlur={() => setTouched({...touched, matricula: true})}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Contraseña*</label>
                            <div className="password-container">
                                <input
                                        type={mostrarPassword ? "text" : "password"}
                                        name="contrasena"
                                        className={`form-control ${
                                            !touched.contrasena ? "" : PASSWORD_PATTERN.test(datosFormulario.contrasena) ? "valido" : "invalido"
                                        }`}
                                        placeholder="Contraseña"
                                        value={datosFormulario.contrasena}
                                        onChange={cambiarValor}
                                        onBlur={() => setTouched({...touched, contrasena: true})}
                                />
                                <div 
                                    className="password-toggle-icon" 
                                    onClick={() => setMostrarPassword(!mostrarPassword)}
                                >
                                    {mostrarPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                                </div>
                            </div>
                        </div>
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
                            <option value="" disabled hidden>Seleccionar</option>
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
                                <option value="" disabled hidden>Seleccionar</option>
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
                                <option value="" disabled hidden>Seleccionar</option>
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
                                placeholder="Ej. Programador"
                                value={datosFormulario.puesto}
                                onChange={cambiarValor}
                                onBlur={() => setTouched({...touched, puesto: true})}
                                required
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
                                placeholder="Ej. 5000"
                                value={datosFormulario.salarioQuincenal}
                                onChange={cambiarValor}
                                onBlur={() => setTouched({...touched, salarioQuincenal: true})}
                                required
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

export default AgregarUsuario;