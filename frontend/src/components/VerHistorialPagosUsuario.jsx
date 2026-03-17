import React from 'react';
import '../css/GestionUsuario.css';
import { ReceiptText } from 'lucide-react';

const VerHistorialPagosUsuario = ({ usuario, alCerrar, tipo = "Usuario" }) => {
    if (!usuario) return null;

    // Función para obtener iniciales
    const getIniciales = (nombre) => {
        if (!nombre) return "??";
        return nombre.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
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
                            <strong>${new Intl.NumberFormat('es-MX').format(usuario.totalAcumulado || 0)}.00</strong>
                        </div>
                    </div>
                </div>

                <div className="ux-body-scrollable">
                    <h3 className="ux-section-title">Historial de pagos del {tipo.toLowerCase()}</h3>

                    <div className="ux-payments-list">
                        {(usuario.pagos || [
                            { fecha: '11 DE FEB, 2026', monto: '15,000.00' },
                            { fecha: '28 DE ENE, 2026', monto: '15,000.00' }
                        ]).map((pago, index) => (
                            <div key={index} className="ux-payment-item-card">
                                <div className="ux-payment-details">
                                    <div className="ux-payment-icon">
                                        <ReceiptText size={18} color="#0c9d72" />
                                    </div>
                                    <div className="ux-payment-text">
                                        <label>FECHA DE PAGO</label>
                                        <strong>{pago.fecha}</strong>
                                    </div>
                                </div>
                                <div className="ux-payment-monto">
                                    <label>MONTO</label>
                                    <strong>${pago.monto}</strong>
                                </div>
                            </div>
                        ))}
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
