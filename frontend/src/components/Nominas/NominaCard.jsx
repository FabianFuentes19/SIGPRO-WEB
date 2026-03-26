import React from 'react';
import './NominaCard.css';
import { Banknote, CheckCircle2, Calendar } from 'lucide-react';

/**
 * Componente que representa una tarjeta de nómina (voucher).
 * @param {Object} nomina - Objeto con la información de la nómina (nombre, monto, estado, etc.)
 * @param {Function} onPay - Función que se ejecuta al hacer clic en el botón de pagar
 */
const NominaCard = ({ nomina, onPay }) => {
  // Verificamos si la nómina ya ha sido pagada para cambiar el diseño del botón
  const isPaid = nomina.estado === 'PAGADO';

  return (
    <div className="nomina-card">
      {/* Cabecera: Información del empleado y número de voucher/pago */}
      <div className="nomina-card-header">
        <div className="employee-info">
          <h3 className="employee-name">{nomina.nombre}</h3>
          <span className="employee-role">{nomina.puesto}</span>
        </div>
        <div className="voucher-info">
          <span className="voucher-label">NO. VOUCHER:</span>
          <span className="voucher-number">{nomina.voucher}</span>
        </div>
      </div>

      {/* Cuerpo: Monto a pagar, estado visual y fecha del periodo */}
      <div className="nomina-card-body">
        <div className="amount-section">
          <span className="amount-label">TOTAL A PAGAR</span>
          <h2 className="amount-value">
            ${typeof nomina.monto === 'number' ? nomina.monto.toLocaleString() : nomina.monto}
          </h2>
        </div>
        <div className="status-badge-container">
          {/* El badge cambia de color según el estado (clases CSS: pagado, pendiente) */}
          <div className={`status-badge ${nomina.estado.toLowerCase()}`}>
            {nomina.estado}
          </div>
          <div className="date-info">
            <Calendar size={14} />
            <span>{nomina.fecha}</span>
          </div>
        </div>
      </div>

      {/* Pie de la tarjeta: Botón de acción */}
      <div className="nomina-card-footer">
        {isPaid ? (
          // Si ya está pagado, mostramos un botón deshabilitado de éxito
          <button className="btn-paid-status" disabled>
            <CheckCircle2 size={18} />
            <span>Pagado con éxito</span>
          </button>
        ) : (
          // Si está pendiente, habilitamos el botón para ejecutar la función onPay del padre
          <button className="btn-pay-nomina" onClick={onPay}>
            <Banknote size={18} />
            <span>Pagar Nómina</span>
          </button>
        )}
      </div>
    </div>
  );
};

export default NominaCard;
