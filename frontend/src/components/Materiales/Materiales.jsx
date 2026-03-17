import './Materiales.css';
import React, {useState} from 'react';
import { Search, Plus } from 'lucide-react';
import AgregarMaterial from './AgregarMaterial.jsx';

const Materiales = () => {
    const [mostrarModal, setMostrarModal] = useState(false);

    const materiales = [
        { id: 1, nombre: "Acuarelas", cantidad: 4, unidad: "pz", precio: 320.00 },
        { id: 2, nombre: "Pinceles Óleo", cantidad: 12, unidad: "pz", precio: 546.00 },
        { id: 3, nombre: "Lienzo 40x60", cantidad: 2, unidad: "pz", precio: 240.00 }
    ];

    // Cálculo automático del total -> para el back
    const totalSuma = materiales.reduce((acc, item) => acc + item.precio, 0);

    return (
        <div className="materiales-container-web">
            <div className="total-material-card-full">
                <small className="date-label-style">TOTAL DE MATERIAL</small>
                <h1 className="budget-value">
                    ${totalSuma.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                </h1>
            </div>

            <div className="search-bar-materials">
                <Search size={20} className="search-icon-inner" />
                <input type="text" placeholder="Buscar materiales..." className="input-search-styled" />
            </div>

            <div className="material-actions-row">
                <button className="gold-add-btn" onClick={() => setMostrarModal(true)}>
                    <Plus size={18} />
                    <span>Agregar material</span>
                </button>
            </div>

            <div className="materials-stack-list">
                {materiales.map((m) => (
                    <div key={m.id} className="member-card-item">
                        <div className="member-data">
                            <strong>{m.nombre}</strong>
                            <span>{m.cantidad} {m.unidad}</span>
                        </div>
                        <div className="mat-price-tag">
                            ${m.precio.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                        </div>
                    </div>
                ))}
            </div>

            {mostrarModal && (
                <AgregarMaterial alCerrar={() => setMostrarModal(false)} />
            )}
        </div>
    );
};

export default Materiales;