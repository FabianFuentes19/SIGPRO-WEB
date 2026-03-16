import React, { useState, useEffect, useRef } from 'react';
import './DashLider.css';

import AgregarUsuario from '../Usuarios/AgregarUsuario.jsx';
import Materiales from '../Materiales/Materiales.jsx';
import EditarUsuario from '../Usuarios/EditarUsuario.jsx';
import BorrarUsuario from '../Usuarios/BorrarUsuario.jsx';
import VerDetallesUsuario from '../Usuarios/VerDetallesUsuario.jsx';
import VerHistorialPagosUsuario from '../Usuarios/VerHistorialPagosUsuario.jsx';
import Nominas from '../../pages/Nominas.jsx';

import {
    LayoutDashboard,
    Box,
    Wallet,
    LogOut,
    Calendar,
    UserPlus,
    MoreVertical,
    CircleUserRound,
    Pencil,
    Trash2,
    Eye,
    History
} from 'lucide-react';

const DashboardLider = () => {
    const [mostrarModal, setMostrarModal] = useState(false);
    const [vistaActual, setVistaActual] = useState('proyecto');

    // Estados para CRUD miembros (Tres puntitos)
    const [menuAbiertoId, setMenuAbiertoId] = useState(null);
    const [usuarioSeleccionado, setUsuarioSeleccionado] = useState(null);
    const [modalActivo, setModalActivo] = useState(null);

    const proyecto = {
        nombre: "Sistema gestión de productos",
        descripcion: "Se trata de un modelo de suscripción mensual que entrega productos orgánicos directamente de productores locales a los hogares. El proyecto integra un sistema de gestión de inventarios y una pasarela de pagos segura, buscando eliminar a los intermediarios y asegurar precios justos para los agricultores mediante una arquitectura de microservicios.",
        presupuesto: "$400,000",
        fechaInicio: "21/02/2026",
        fechaFin: "29/04/2026",
        progreso: 65
    };

    const miembros = [
        { id: 1, nombre: "Marcos Ríos", rol: "Diseñador", iniciales: "MR" },
        { id: 2, nombre: "Juan López", rol: "Coordinador", iniciales: "JL" },
        { id: 3, nombre: "Marcos Ríos", rol: "Diseñador", iniciales: "MR" },
        { id: 4, nombre: "Marcos Ríos", rol: "Diseñador", iniciales: "MR" }
    ];

    // Cerrar el menú desplegable si se hace clic fuera
    useEffect(() => {
        const cerrarMenu = () => setMenuAbiertoId(null);
        document.addEventListener('click', cerrarMenu);
        return () => document.removeEventListener('click', cerrarMenu);
    }, []);

    // Función para abrir el menú de opciones
    const toggleMenu = (e, id) => {
        e.stopPropagation();
        setMenuAbiertoId(menuAbiertoId === id ? null : id);
    };

    // Función para abrir los modales
    const abrirAccion = (tipo, usuario) => {
        setUsuarioSeleccionado(usuario);
        setModalActivo(tipo);
        setMenuAbiertoId(null);
    };

    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <div className="header-brand">Panel Líder</div>
                <div className="header-title">
                    {vistaActual === 'proyecto' ? 'Proyecto' :
                        vistaActual === 'materiales' ? 'Materiales' : 'Nóminas'}
                </div>
                <div className="header-user">
                    <CircleUserRound size={30} strokeWidth={1.5} />
                </div>
            </header>

            <div className="dashboard-body">
                <aside className="sidebar">
                    <nav className="sidebar-nav">
                        <div
                            className={`nav-item ${vistaActual === 'proyecto' ? 'active' : ''}`}
                            onClick={() => setVistaActual('proyecto')}
                        >
                            <LayoutDashboard size={20} />
                            <span>Proyecto</span>
                        </div>
                        <div
                            className={`nav-item ${vistaActual === 'materiales' ? 'active' : ''}`}
                            onClick={() => setVistaActual('materiales')}
                        >
                            <Box size={20} />
                            <span>Materiales</span>
                        </div>
                        <div
                            className={`nav-item ${vistaActual === 'nominas' ? 'active' : ''}`}
                            onClick={() => setVistaActual('nominas')}
                        >
                            <Wallet size={20} />
                            <span>Nóminas</span>
                        </div>
                    </nav>
                    <div className="sidebar-footer">
                        <button className="logout-btn">
                            <LogOut size={20} />
                            <span>Salir</span>
                        </button>
                    </div>
                </aside>

                <main className="main-content">
                    {vistaActual === 'proyecto' ? (
                        <>
                            <div className="project-card">
                                <div className="project-header-section">
                                    <h2>{proyecto.nombre}</h2>
                                    <p className="description-text">{proyecto.descripcion}</p>
                                </div>

                                <div className="dates-row-container">
                                    <div className="date-inner-box">
                                        <div className="calendar-icon-styled"><Calendar size={20} /></div>
                                        <div className="date-text-group">
                                            <small className="date-label-style">FECHA INICIO</small>
                                            <p className="date-range-style">{proyecto.fechaInicio}</p>
                                        </div>
                                    </div>
                                    <div className="date-inner-box">
                                        <div className="calendar-icon-styled"><Calendar size={20} /></div>
                                        <div className="date-text-group">
                                            <small className="date-label-style">FECHA FIN</small>
                                            <p className="date-range-style">{proyecto.fechaFin}</p>
                                        </div>
                                    </div>
                                </div>

                                <div className="budget-section-left">
                                    <span className="budget-label">Presupuesto Total</span>
                                    <h3 className="budget-value">{proyecto.presupuesto}</h3>
                                </div>

                                <div className="progress-section-bottom">
                                    <div className="progress-info-row">
                                        <span>Progreso Del Proyecto</span>
                                        <span className="progress-perc">{proyecto.progreso}%</span>
                                    </div>
                                    <div className="progress-bar-outer">
                                        <div className="progress-bar-inner-fill" style={{ width: `${proyecto.progreso}%` }}></div>
                                    </div>
                                </div>
                            </div>

                            <div className="members-section">
                                <div className="members-top-row">
                                    <h2>Miembros</h2>
                                    <button className="gold-add-btn" onClick={() => setMostrarModal(true)}>
                                        <UserPlus size={18} />
                                        <span>Agregar miembro</span>
                                    </button>
                                </div>

                                <div className="members-stack-list">
                                    {miembros.map((m) => (
                                        <div key={m.id} className="member-card-item">
                                            <div className="circle-avatar">{m.iniciales}</div>
                                            <div className="member-data">
                                                <strong>{m.nombre}</strong>
                                                <span>{m.rol}</span>
                                            </div>

                                            <div className="more-dots-container" style={{ position: 'relative' }}>
                                                <div className="more-dots" onClick={(e) => toggleMenu(e, m.id)}>
                                                    <MoreVertical size={20} />
                                                </div>

                                                {menuAbiertoId === m.id && (
                                                    <div className="dropdown-menu-opciones">
                                                        <div className="dropdown-item" onClick={() => abrirAccion('editar', m)}>
                                                            <Pencil size={14} /> <span>Editar</span>
                                                        </div>
                                                        <div className="dropdown-item" onClick={() => abrirAccion('detalles', m)}>
                                                            <Eye size={14} /> <span>Ver detalles</span>
                                                        </div>
                                                        <div className="dropdown-item" onClick={() => abrirAccion('historial', m)}>
                                                            <History size={14} /> <span>Ver historial</span>
                                                        </div>
                                                        <div className="dropdown-item" onClick={() => abrirAccion('borrar', m)}>
                                                            <Trash2 size={14} /> <span>Borrar</span>
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </>
                    ) : vistaActual === 'materiales' ? (
                        <Materiales />
                    ) : (
                        <Nominas />
                    )}
                </main>
            </div>

            {mostrarModal && <AgregarUsuario alCerrar={() => setMostrarModal(false)} />}
            {modalActivo === 'editar' && <EditarUsuario usuario={usuarioSeleccionado} alCerrar={() => setModalActivo(null)} />}
            {modalActivo === 'borrar' && <BorrarUsuario usuario={usuarioSeleccionado} alCerrar={() => setModalActivo(null)} />}
            {modalActivo === 'detalles' && <VerDetallesUsuario usuario={usuarioSeleccionado} alCerrar={() => setModalActivo(null)} />}
            {modalActivo === 'historial' && <VerHistorialPagosUsuario usuario={usuarioSeleccionado} alCerrar={() => setModalActivo(null)} />}
        </div>
    );
};

export default DashboardLider;