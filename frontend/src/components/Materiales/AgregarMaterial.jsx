import React, { useState } from 'react';
import './Materiales.css';

const AgregarMaterial = ({ alCerrar, alRegistrar, tipo = "Material", onError }) => {

    const [datosFormulario, setDatosFormulario] = useState({
        nombre: '',
        cantidad: '',
        monto: '',
    });

    const cambiarValor = (e) => {
        const { name, value } = e.target;
        setDatosFormulario({ ...datosFormulario, [name]: value });
    };

    const guardarMaterial = (e) => {
        e.preventDefault();

        // Primero validar campos obligatorios (vacíos)
        const camposVacios = [];
        if (datosFormulario.nombre.trim() === '') camposVacios.push("Nombre material");
        if (datosFormulario.cantidad === '') camposVacios.push("Cantidad");
        if (datosFormulario.monto === '') camposVacios.push("Precio");

        if (camposVacios.length > 0) {
            if (onError) {
                onError({
                    titulo: "Campos incompletos",
                    mensaje: `Faltan campos obligatorios: ${camposVacios.join(", ")}`,
                    tipo: "error"
                });
            }
            return;
        }

        // Validar cantidad
        const cantidadNum = Number(datosFormulario.cantidad);
        if (Number.isNaN(cantidadNum) || cantidadNum <= 0) {
            if (onError) {
                onError({
                    titulo: "Cantidad inválida",
                    mensaje: "La cantidad debe ser un número mayor a 0",
                    tipo: "error"
                });
            }
            return;
        }

        // Validar precio
        const montoNum = Number(datosFormulario.monto);
        if (Number.isNaN(montoNum) || montoNum <= 0) {
            if (onError) {
                onError({
                    titulo: "Precio inválido",
                    mensaje: "El precio debe ser un número mayor a 0",
                    tipo: "error"
                });
            }
            return;
        }

        alRegistrar(datosFormulario);
        alCerrar();
    };

    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <h2 className="modal-title">Agregar {tipo}</h2>
                <form onSubmit={guardarMaterial} className="modal-form" noValidate>

                    <div className="form-group">
                        <label>Nombre material*</label>
                        <input
                            type="text"
                            name="nombre"
                            placeholder="Ej. Resistol líquido"
                            value={datosFormulario.nombre}
                            onChange={cambiarValor}
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
                            />
                        </div>
                        <div className="form-group">
                            <label>Precio*</label>
                            <input
                                type="number"
                                name="monto"
                                step="0.01"
                                placeholder="$70"
                                value={datosFormulario.monto}
                                onChange={cambiarValor}
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