import React, { useState } from 'react';
import '../css/GestionUsuario.css';

const AgregarUsuario = ({ alCerrar, alRegistrar, tipo = "Usuario" }) => {

    const [datosFormulario, setDatosFormulario] = useState({
        nombreCompleto: '',
        matricula: '',
        cuatrimestre: '',
        grupo: '',
        carrera: '',
        puesto: '',
        salarioQuincenal: '',
        fechaIngreso: ''
    });

    const cambiarValor = (e) => {
        const { name, value } = e.target;
        setDatosFormulario({ ...datosFormulario, [name]: value });
    };

    const guardarUsuario = (e) => {
        e.preventDefault();
        const payload = {
            ...datosFormulario,
            cuatrimestre: parseInt(datosFormulario.cuatrimestre, 10),
            salarioQuincenal: datosFormulario.salarioQuincenal ? parseFloat(datosFormulario.salarioQuincenal) : null
        };
        alRegistrar(payload);
    };

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
                            placeholder="Ej. Juan Perez"
                            value={datosFormulario.nombreCompleto}
                            onChange={cambiarValor}
                            required
                        />
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Matrícula*</label>
                            <input
                                type="text"
                                name="matricula"
                                placeholder="Ej. 20243ds001"
                                value={datosFormulario.matricula}
                                onChange={cambiarValor}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Contraseña*</label>
                            <input
                                type="password"
                                name="contrasena"
                                placeholder="Mín. 8 caracteres"
                                value={datosFormulario.contrasena}
                                onChange={cambiarValor}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Carrera*</label>
                        <select name="carrera" value={datosFormulario.carrera} onChange={cambiarValor} required>
                            <option value="" disabled hidden>Seleccionar</option>
                            <option value="DS">Desarrollo de software</option>
                            <option value="DD">Diseño digital</option>
                        </select>
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Cuatrimestre*</label>
                            <select name="cuatrimestre" value={datosFormulario.cuatrimestre} onChange={cambiarValor} required>
                                <option value="" disabled hidden>Seleccionar</option>
                                {[1,2,3,4,5,6,7,8,9,10,11].map(n => <option key={n} value={n}>{n}°</option>)}
                            </select>
                        </div>
                        <div className="form-group">
                            <label>Grupo*</label>
                            <select name="grupo" value={datosFormulario.grupo} onChange={cambiarValor} required>
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
                                placeholder="Ej. Programador"
                                value={datosFormulario.puesto}
                                onChange={cambiarValor}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Salario quincenal</label>
                            <input
                                type="number"
                                name="salarioQuincenal"
                                placeholder="Ej. 5000"
                                value={datosFormulario.salarioQuincenal}
                                onChange={cambiarValor}
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