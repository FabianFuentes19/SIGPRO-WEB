import React from 'react';
import './GestionUsuarios.css';
import { Calendar, DollarSign, ReceiptText, User as UserIcon } from 'lucide-react';

const VerHistorialPagosUsuario = ({ usuario, alCerrar }) => {
    // Función para obtener iniciales
    const getIniciales = (nombre) => {
        if (!nombre) return "??";
        return nombre.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
    };

    return (
        <div className="modal-overlay">
            <div className="ux-card-wide">
                <div className="ux-identity-container-history">
                    <div className="ux-avatar-circle">
                        {getIniciales(usuario?.nombreCompleto || "Marcos Ortiz")}
                    </div>
                    <div className="ux-identity-info">
                        <h2 className="ux-name-history">{usuario?.nombreCompleto || 'Marcos Ortiz'}</h2>
                        <span className="ux-puesto-history">{usuario?.puesto || 'Diseñador gráfico'}</span>
                        <div className="ux-total-accumulated">
                            <label>Total acumulado</label>
                            <strong>${new Intl.NumberFormat('es-MX').format(usuario?.totalAcumulado || 15000)}.00</strong>
                        </div>
                    </div>
                </div>

                <div className="ux-body-scrollable">
                    <h3 className="ux-section-title">Historial de pagos</h3>

                    <div className="ux-payments-list">
                        {[
                            { fecha: '11 DE FEB, 2026', monto: '15,000.00' },
                            { fecha: '28 DE ENE, 2026', monto: '15,000.00' },
                            { fecha: '14 DE ENE, 2026', monto: '15,000.00' }
                        ].map((pago, index) => (
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
                    <button className="ux-btn-cerrar-outline" onClick={alCerrar}>Cerrar</button>
                </div>
            </div>
        </div>
    );
};

export default VerHistorialPagosUsuario;