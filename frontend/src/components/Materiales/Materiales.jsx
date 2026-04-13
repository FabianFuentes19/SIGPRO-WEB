import './Materiales.css';
import React, { useEffect, useMemo, useState } from 'react';
import { Search, Plus } from 'lucide-react';
import AgregarMaterial from './AgregarMaterial.jsx';
import { obtenerMaterialesPorProyecto, registrarMaterial } from '../../services/api.js';
import ModalMensajes from '../Usuarios/ModalMensajes.jsx';
import { formatCurrencyWithSign } from '../../utils/formatters';

const Materiales = ({ proyectoId, proyecto, onMaterialSuccess }) => {
    const [mostrarModal, setMostrarModal] = useState(false);
    const [materiales, setMateriales] = useState([]);
    const [busqueda, setBusqueda] = useState('');
    const [cargando, setCargando] = useState(false);
    const [mensajeModal, setMensajeModal] = useState(null);

    const cargarMateriales = async () => {
        if (!proyectoId) {
            setMateriales([]);
            return;
        }
        setCargando(true);
        try {
            const data = await obtenerMaterialesPorProyecto(proyectoId);
            setMateriales(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
            setMateriales([]);
        } finally {
            setCargando(false);
        }
    };

    useEffect(() => {
        cargarMateriales();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [proyectoId]);

    const materialesFiltrados = useMemo(() => {
        const q = busqueda.trim().toLowerCase();
        if (!q) return materiales;
        return materiales.filter((m) => (m?.nombre || '').toLowerCase().includes(q));
    }, [materiales, busqueda]);

    const totalSuma = useMemo(() => {
        return materiales.reduce((acc, item) => acc + (Number(item?.costoTotal) || 0), 0);
    }, [materiales]);


    const formatearMoneda = (v) => formatCurrencyWithSign(v);

    const onRegistrar = async (datos) => {
        if (!proyectoId) {
            alert('No se encontró el proyecto del líder. Recarga la página.');
            return;
        }
        try {
            const result = await registrarMaterial({
                nombre: datos?.nombre,
                monto: datos?.monto,
                cantidad: datos?.cantidad,
                proyectoId,
            });
            setMostrarModal(false);
            setMensajeModal({
                titulo: "Registro Exitoso",
                mensaje: "Material registrado correctamente",
                tipo: "exito"
            });

            if (result?.alerta) {
                setTimeout(() => {
                    setMensajeModal({
                        titulo: "ALERTA",
                        mensaje: result.alerta.mensaje,
                        tipo: result.alerta.tipo === "advertencia" ? "advertencia" : "error"
                    });
                }, 2000);
            }

            await cargarMateriales(); // refresca lista y total automáticamente
            if (typeof onMaterialSuccess === 'function') {
                onMaterialSuccess();
            }
        } catch (e) {
            setMostrarModal(false);
            setMensajeModal({
                titulo: "Error",
                mensaje: e?.message  || "Error al registrar material",
                tipo: "error"
            });
        }
    };

    return (
        <div className="materiales-container-web">
            <div className="total-material-card-full">
                <small className="date-label-style">TOTAL DE MATERIAL</small>
                <h1 className="budget-value">
                    {formatearMoneda(totalSuma)}
                </h1>
            </div>

            <div className="search-bar-materials">
                <Search size={20} className="search-icon-inner" />
                <input
                    type="text"
                    placeholder="Buscar materiales..."
                    className="input-search-styled"
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                />
            </div>

            <div className="material-actions-row">
                <button
                    className="gold-add-btn"
                    onClick={() => setMostrarModal(true)}
                    disabled={proyecto?.estado?.toUpperCase() !== 'ACTIVO'}
                    style={{ 
                        cursor: proyecto?.estado?.toUpperCase() !== 'ACTIVO' ? 'not-allowed' : 'pointer',
                        opacity: proyecto?.estado?.toUpperCase() !== 'ACTIVO' ? 0.6 : 1,
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px'
                    }}
                >
                    <Plus size={18} />
                    <span>Agregar material</span>
                </button>
            </div>

            <div className="materials-stack-list">
                {cargando ? (
                    <div className="member-card-item">
                        <div className="member-data">
                            <strong>Cargando...</strong>
                            <span>Consultando materiales</span>
                        </div>
                    </div>
                ) : materialesFiltrados.map((m) => (
                    <div key={m.id} className="member-card-item">
                        <div className="member-data">
                            <strong>{m.nombre}</strong>
                            <span>{m.cantidad} pz</span>
                        </div>
                        <div className="mat-price-tag">
                            {formatearMoneda(m.costoTotal)}
                        </div>
                    </div>
                ))}
            </div>

            {mostrarModal && (
                <AgregarMaterial
                    alCerrar={() => setMostrarModal(false)}
                    alRegistrar={onRegistrar}
                    onError={setMensajeModal}
                />
            )}

            {mensajeModal && (
                <ModalMensajes
                    titulo={mensajeModal.titulo}
                    mensaje={mensajeModal.mensaje}
                    tipo={mensajeModal.tipo}
                    onConfirm={() => setMensajeModal(null)}
                />
                )}
        </div>
    );
};

export default Materiales;