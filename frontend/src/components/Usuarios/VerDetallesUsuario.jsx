import React from 'react';
import './GestionUsuarios.css';
import { User, GraduationCap, Calendar, Wallet, Briefcase, Hash, BookText, Users } from 'lucide-react';

const VerDetallesUsuario = ({ usuario, alCerrar }) => {
    return (
        <div className="modal-overlay">
            <div className="ux-card-wide">
                <div className="ux-identity-container">
                    <span className="ux-tag">INFORMACIÓN DEL MIEMBRO</span>
                    <h2 className="ux-name">{usuario?.nombreCompleto || 'Tania Sánchez Reyes'}</h2>
                    <div className="ux-badge-puesto">
                        <Briefcase size={14} /> {usuario?.puesto || 'Programador'}
                    </div>
                </div>

                <div className="ux-body">
                    <div className="ux-fields-row">
                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <Hash size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Matrícula</label>
                                <strong>{usuario?.matricula || '20243ds026'}</strong>
                            </div>
                        </div>

                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <BookText size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Carrera</label>
                                <strong>{usuario?.carrera || 'Sistemas'}</strong>
                            </div>
                        </div>

                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <Calendar size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Fecha de Ingreso</label>
                                <strong>{usuario?.fechaIngreso || '04/02/2026'}</strong>
                            </div>
                        </div>
                    </div>

                    <div className="ux-fields-row mt-25">
                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <GraduationCap size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Cuatrimestre</label>
                                <strong>{usuario?.cuatrimestre || '5'}° </strong>
                            </div>
                        </div>

                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <Users size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Grupo</label>
                                <strong>{usuario?.grupo || 'A'}</strong>
                            </div>
                        </div>
                    </div>

                    <div className="ux-salary-card-custom">
                        <div className="ux-salary-icon-main">
                            <Wallet size={24} color="#fff" />
                        </div>
                        <div className="ux-salary-info">
                            <label className="label-blue">SALARIO QUINCENAL</label>
                            <span>Monto total</span>
                        </div>
                        <div className="ux-salary-value">
                            ${new Intl.NumberFormat('es-MX').format(usuario?.salarioQuincenal || 15000)}.00
                            <span className="ux-currency">MXN</span>
                        </div>
                    </div>
                </div>

                <div className="ux-footer">
                    <button className="ux-btn-cerrar" onClick={alCerrar}>Cerrar</button>
                </div>
            </div>
        </div>
    );
};

export default VerDetallesUsuario;