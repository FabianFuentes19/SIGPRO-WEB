import React from 'react';
import './NominaCard.css';
import { Banknote, CheckCircle2, Calendar } from 'lucide-react';

const NominaCard = ({ nomina }) => {
  const isPaid = nomina.estado === 'PAGADO';

  return (
    <div className="nomina-card">
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

      <div className="nomina-card-body">
        <div className="amount-section">
          <span className="amount-label">TOTAL A PAGAR</span>
          <h2 className="amount-value">${nomina.monto.toLocaleString()}</h2>
        </div>
        <div className="status-badge-container">
          <div className={`status-badge ${nomina.estado.toLowerCase()}`}>
            {nomina.estado}
          </div>
          <div className="date-info">
            <Calendar size={14} />
            <span>{nomina.fecha}</span>
          </div>
        </div>
      </div>

      <div className="nomina-card-footer">
        {isPaid ? (
          <button className="btn-paid-status" disabled>
            <CheckCircle2 size={18} />
            <span>Pagado con éxito</span>
          </button>
        ) : (
          <button className="btn-pay-nomina">
            <Banknote size={18} />
            <span>Pagar Nómina</span>
          </button>
        )}
      </div>
    </div>
  );
};

export default NominaCard;
