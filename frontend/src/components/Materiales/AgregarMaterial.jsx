import React, { useState } from 'react';
import './Materiales.css';

const AgregarMaterial = ({ alCerrar, alRegistrar, tipo = "Material" }) => {

    const [datosFormulario, setDatosFormulario] = useState({
        nombre: '',
        cantidad: '',
        precio: '',
    });

    const cambiarValor = (e) => {
        const { name, value } = e.target;
        setDatosFormulario({ ...datosFormulario, [name]: value });
    };

    const guardarMaterial = (e) => {
        e.preventDefault();
        alRegistrar(datosFormulario);
        alCerrar();
    };

    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2 className="modal-title">Agregar {tipo}</h2>
                <form onSubmit={guardarMaterial} className="modal-form">

                    <div className="form-group">
                        <label>Nombre material*</label>
                        <input
                            type="text"
                            name="nombre"
                            placeholder="Ej. Resistol líquido"
                            value={datosFormulario.nombre}
                            onChange={cambiarValor}
                            required
                        />
                    </div>

                    <div className="form-row-2-col">
                        <div className="form-group">
                            <label>Cantidad*</label>
                            <input
                                type="number"
                                name="cantidad"
                                placeholder="Ej. 2"
                                value={datosFormulario.cantidad}
                                onChange={cambiarValor}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Precio*</label>
                            <input
                                type="number"
                                name="precio"
                                placeholder="$70"
                                value={datosFormulario.precio}
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

export default AgregarMaterial;