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
        const payload = {
            ...datosFormulario,
            cuatrimestre: parseInt(datosFormulario.cuatrimestre, 10),
            salarioQuincenal: datosFormulario.salarioQuincenal ? parseFloat(datosFormulario.salarioQuincenal) : null
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
                            value={datosFormulario.nombreCompleto}
                            onChange={cambiarValor}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Matrícula (No editable)</label>
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
                            <option value="DS">Desarrollo de software</option>
                            <option value="DD">Diseño digital</option>
                        </select>
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Cuatrimestre*</label>
                            <select name="cuatrimestre" value={datosFormulario.cuatrimestre} onChange={cambiarValor} required>
                                {[1,2,3,4,5,6,7,8,9,10,11].map(n => <option key={n} value={n}>{n}°</option>)}
                            </select>
                        </div>
                        <div className="form-group">
                            <label>Grupo*</label>
                            <select name="grupo" value={datosFormulario.grupo} onChange={cambiarValor} required>
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
                                value={datosFormulario.puesto}
                                onChange={cambiarValor}
                            />
                        </div>
                        <div className="form-group">
                            <label>Salario quincenal</label>
                            <input
                                type="number"
                                name="salarioQuincenal"
                                value={datosFormulario.salarioQuincenal}
                                onChange={cambiarValor}
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