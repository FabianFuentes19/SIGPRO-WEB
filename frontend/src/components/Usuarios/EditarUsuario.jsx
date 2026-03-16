import React, { useState, useEffect } from 'react';
import './GestionUsuarios.css';

const EditarUsuario = ({alCerrar, alGuardar }) => {
    const [datosFormulario, setDatosFormulario] = useState({
        nombreCompleto: '',
        matricula: '',
        cuatrimestre: '',
        grupo: '',
        carrera: '',
        contrasenia: '',
        puesto: '',
        salarioQuincenal: '',
        fechaIngreso: ''
    });


    const cambiarValor = (e) => {
        const { name, value } = e.target;
        setDatosFormulario({ ...datosFormulario, [name]: value });
    };

    const enviarEdicion = (e) => {
        e.preventDefault();
        alGuardar(datosFormulario);
        alCerrar();
    };

    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2 className="modal-title">Editar Miembro</h2>
                <form onSubmit={enviarEdicion} className="modal-form">

                    <div className="form-group">
                        <label>Nombre completo*</label>
                        <input
                            type="text"
                            name="nombreCompleto"
                            value={datosFormulario.nombreCompleto}
                            onChange={cambiarValor}
                            required
                        />
                    </div>


                    <div className="form-group">
                        <label>Matrícula</label>
                        <input
                            type="text"
                            name="matricula"
                            className="input-readonly"
                            value={datosFormulario.matricula}
                            readOnly
                            />
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
                                {[1,2,3,4,5,6,7,8,9,10].map(n => <option key={n} value={n}>{n}°</option>)}
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
                            <label>Puesto*</label>
                            <input
                                type="text"
                                name="puesto"
                                value={datosFormulario.puesto}
                                onChange={cambiarValor}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Salario quincenal*</label>
                            <input
                                type="number"
                                name="salarioQuincenal"
                                value={datosFormulario.salarioQuincenal}
                                onChange={cambiarValor}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Fecha ingreso*</label>
                        <input
                            type="date"
                            name="fechaIngreso"
                            value={datosFormulario.fechaIngreso}
                            onChange={cambiarValor}
                            required
                        />
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