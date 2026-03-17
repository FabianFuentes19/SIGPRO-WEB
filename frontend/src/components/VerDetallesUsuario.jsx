import React from 'react';
import '../css/GestionUsuario.css';
import { Briefcase, Hash, BookText, Calendar, GraduationCap, Users, Wallet } from 'lucide-react';

const VerDetallesUsuario = ({ usuario, alCerrar, tipo = "Usuario" }) => {
    if (!usuario) return null;

    return (
        <div className="modal-overlay">
            <div className="ux-card-wide modal-container">
                <div className="ux-identity-container">
                    <span className="ux-tag">INFORMACIÓN DEL {tipo.toUpperCase()}</span>
                    <h2 className="ux-name">{usuario.nombreCompleto}</h2>
                    <div className="ux-badge-puesto">
                        <Briefcase size={14} /> {usuario.puesto || 'Puesto no asignado'}
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
                                <strong>{usuario.matricula}</strong>
                            </div>
                        </div>

                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <BookText size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Carrera</label>
                                <strong>{usuario.carrera}</strong>
                            </div>
                        </div>

                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <Calendar size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Estado</label>
                                <strong>{usuario.estado}</strong>
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
                                <strong>{usuario.cuatrimestre}° </strong>
                            </div>
                        </div>

                        <div className="ux-field-card">
                            <div className="ux-field-icon-bg">
                                <Users size={18} color="#0c9d72" />
                            </div>
                            <div className="ux-field-content">
                                <label>Grupo</label>
                                <strong>{usuario.grupo}</strong>
                            </div>
                        </div>
                    </div>

                    {usuario.salarioQuincenal && (
                        <div className="ux-salary-card-custom">
                            <div className="ux-salary-icon-main">
                                <Wallet size={24} color="#fff" />
                            </div>
                            <div className="ux-salary-info">
                                <label className="label-blue">SALARIO QUINCENAL</label>
                                <span>Monto total</span>
                            </div>
                            <div className="ux-salary-value">
                                ${new Intl.NumberFormat('es-MX').format(usuario.salarioQuincenal)}
                                <span className="ux-currency">MXN</span>
                            </div>
                        </div>
                    )}
                </div>

                <div className="ux-footer-history">
                    <button className="ux-btn-cerrar" onClick={alCerrar}>Cerrar Detalles</button>
                </div>
            </div>
        </div>
    );
};

export default VerDetallesUsuario;