import React, { useState, useEffect } from 'react';
import '../css/GestionUsuario.css';
import { ReceiptText, Loader2 } from 'lucide-react';
import { formatCurrencyWithSign } from '../utils/formatters';
import { apiFetch } from '../services/api';

const BASE_URL = "http://localhost:8080";

const VerHistorialPagosUsuario = ({ usuario, alCerrar, tipo = "Usuario" }) => {
    const [pagos, setPagos] = useState([]);
    const [totalAcumulado, setTotalAcumulado] = useState(0);
    const [cargando, setCargando] = useState(false);

    const obtenerPagos = async () => {
        if (!usuario?.matricula) return;
        const token = localStorage.getItem("token");
        setCargando(true);

        try {
            const response = await apiFetch(`/pagos/miembro/${encodeURIComponent(usuario.matricula)}`);

            if (response.ok) {
                const data = await response.json();
                // orden descendente
                const sorted = data.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
                setPagos(sorted);

                // total
                const total = sorted.reduce((sum, p) => sum + (p.monto || 0), 0);
                setTotalAcumulado(total);
            }
        } catch (error) {
            console.error("Error al cargar historial:", error);
        } finally {
            setCargando(false);
        }
    };

    useEffect(() => {
        obtenerPagos();
    }, [usuario]);

    if (!usuario) return null;

    const getIniciales = (nombre) => {
        if (!nombre) return "??";
        return nombre.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
    };

    const formatCurrency = (amount) => formatCurrencyWithSign(amount);


    const formatDate = (dateStr) => {
        if (!dateStr) return "N/A";
        const [year, month, day] = dateStr.split('-');
        const months = ["ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"];
        return `${day} DE ${months[parseInt(month) - 1]}, ${year}`;
    };

    return (
        <div className="modal-overlay">
            <div className="ux-card-wide modal-container">
                <div className="ux-identity-container-history">
                    <div className="ux-avatar-circle">
                        {getIniciales(usuario.nombreCompleto)}
                    </div>
                    <div className="ux-identity-info">
                        <h2 className="ux-name-history">{usuario.nombreCompleto}</h2>
                        <span className="ux-puesto-history">{usuario.puesto || 'Puesto no asignado'}</span>
                        <div className="ux-total-accumulated">
                            <label>Total acumulado</label>
                            <strong>{formatCurrency(totalAcumulado)}</strong>
                        </div>
                    </div>
                </div>

                <div className="ux-body-scrollable">
                    <h3 className="ux-section-title">Historial de pagos del {tipo.toLowerCase()}</h3>
                        <div className="ux-payments-list">
                            {pagos.length > 0 ? (
                                pagos.map((pago, index) => (
                                    <div key={index} className="ux-payment-item-card">
                                        <div className="ux-payment-details">
                                            <div className="ux-payment-icon">
                                                <ReceiptText size={18} color="#0c9d72" />
                                            </div>
                                            <div className="ux-payment-text">
                                                <label>FECHA DE PAGO</label>
                                                <strong>{formatDate(pago.fechaPagoReal)}</strong>
                                            </div>
                                        </div>
                                        <div className="ux-payment-monto">
                                            <label>MONTO</label>
                                            <strong>{formatCurrency(pago.monto)}</strong>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <div className="no-results-history">No se han registrado pagos para este miembro aún.</div>
                            )}
                        </div>
                </div>

                <div className="ux-footer-history">
                    <button className="ux-btn-cerrar-outline" onClick={alCerrar}>Cerrar Historial</button>
                </div>
            </div>
        </div>
    );
};

export default VerHistorialPagosUsuario;
