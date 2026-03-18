import React, { useState, useEffect, useRef } from 'react';
import './DashLider.css';
import AgregarUsuario from '../AgregarUsuario.jsx';
import Materiales from '../Materiales/Materiales.jsx';
import EditarUsuario from '../EditarUsuario.jsx';
import BorrarUsuario from '../BorrarUsuario.jsx';
import VerDetallesUsuario from '../VerDetallesUsuario.jsx';
import VerHistorialPagosUsuario from '../VerHistorialPagosUsuario.jsx';
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

const BASE_URL = "http://localhost:8080";

const DashboardLider = () => {
    const [mostrarModal, setMostrarModal] = useState(false);
    const [vistaActual, setVistaActual] = useState('proyecto');

    // Estados para CRUD miembros (Tres puntitos)
    const [menuAbiertoId, setMenuAbiertoId] = useState(null);
    const [usuarioSeleccionado, setUsuarioSeleccionado] = useState(null);
    const [modalActivo, setModalActivo] = useState(null);
    const [miembros, setMiembros] = useState([]);
    const [proyecto, setProyecto] = useState(null);
    const [proyectoId, setProyectoId] = useState(null);

    // Cargar el proyecto del líder
    const cargarProyecto = async () => {
        const token = localStorage.getItem("token");
        if (!token) return;
        try {
            const response = await fetch(`${BASE_URL}/proyectos/mi-proyecto/lider`, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });
            if (!response.ok) throw new Error("No se pudo obtener el proyecto");
            const data = await response.json();
            console.log("Proyecto del líder:", data);
            setProyecto(data);
            setProyectoId(data.id);
        } catch (error) {
            console.error("Error al cargar proyecto:", error);
        }
    };

    // Cargar miembros del líder
    const cargarMiembros = async () => {
        const matriculaLider = localStorage.getItem("matricula");
        const token = localStorage.getItem("token");
        console.log("Cargando miembros para líder:", matriculaLider);
        if (!matriculaLider || !token) {
            console.warn("No se encontró la matrícula o token del líder en localStorage");
            return;
        }
        try {
            const response = await fetch(`${BASE_URL}/usuarios/lider/${encodeURIComponent(matriculaLider)}`, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });
            if (!response.ok) throw new Error("Error al obtener miembros");
            const data = await response.json();
            console.log("Miembros recibidos:", data);
            const lista = Array.isArray(data) ? data : [];
            setMiembros(lista
                .filter((m) => m.estado !== 'INACTIVO')
                .map((m, index) => ({
                    ...m,
                    id: m.matricula || `temp-${index}`,
                    iniciales: (m.nombreCompleto || '').trim().split(/\s+/).map((s) => s[0]).join('').slice(0, 2).toUpperCase() || '??',
                    rol: m.rolNombre || m.puesto || ''
                })));
        } catch (error) {
            console.error("Error al cargar miembros:", error);
            setMiembros([]);
        }
    };

    const actualizarLider = async (datosActualizados) => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${BASE_URL}/usuarios/${usuarioSeleccionado.matricula}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(datosActualizados),
            });

            if (response.ok) {
                alert("Miembro actualizado correctamente");
                setModalActivo(null);
                await cargarMiembros();
            } else {
                const errorData = await response.json();
                alert(errorData.error || "Error al actualizar miembro");
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Error de conexión con el servidor");
        }
    };

    useEffect(() => {
        cargarProyecto();
        cargarMiembros();
    }, []);

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
                <div className="header-user" onClick={() => setVistaActual('perfil')}>
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
                    {vistaActual === 'proyecto' && (
                        <>
                            {proyecto ? (
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
                                        <h3 className="budget-value">${proyecto.presupuesto}</h3>
                                    </div>

                                    <div className="progress-section-bottom">
                                        <div className="progress-info-row">
                                            <span>Progreso Del Proyecto</span>
                                            <span className="progress-perc">0%</span>
                                        </div>
                                        <div className="progress-bar-outer">
                                            <div className="progress-bar-inner-fill" style={{ width: '0%' }}></div>
                                        </div>
                                    </div>
                                </div>
                            ) : (
                                <p>Cargando proyecto...</p>
                            )}

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
                                                <strong>{m.nombreCompleto}</strong>
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
                    )}

                    {vistaActual === 'materiales' && <Materiales />}
                    {vistaActual === 'nominas' && <Nominas />}

                </main>
            </div>

            {mostrarModal && (
                <AgregarUsuario
                    tipo="Miembro"
                    alCerrar={() => setMostrarModal(false)}
                    alRegistrar={async (datos) => {
                        try {
                            if (!proyectoId) {
                                alert("No se encontró el proyecto del líder. Recarga la página.");
                                return;
                            }
                            const token = localStorage.getItem("token");
                            const response = await fetch(`${BASE_URL}/proyectos/${proyectoId}/miembros`, {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${token}`
                                },
                                body: JSON.stringify(datos)
                            });
                            const data = await response.json();
                            if (!response.ok) {
                                throw new Error(data.error || "Error al registrar miembro");
                            }
                            setMostrarModal(false);
                            alert("Miembro registrado correctamente");
                            await cargarMiembros();
                        } catch (error) {
                            console.error("Error al registrar miembro:", error);
                            alert(error.message || "Error al registrar miembro");
                        }
                    }}
                />
            )}

            {modalActivo === 'editar' && (
                <EditarUsuario
                    tipo="Miembro"
                    usuario={usuarioSeleccionado}
                    alCerrar={() => setModalActivo(null)}
                    alGuardar={actualizarLider}
                />
            )}

            {modalActivo === 'borrar' && (
                <BorrarUsuario
                    tipo="Miembro"
                    usuario={usuarioSeleccionado}
                    alCerrar={() => setModalActivo(null)}
                    alConfirmar={async (mat) => {
                        try {
                            const token = localStorage.getItem("token");
                            const response = await fetch(`${BASE_URL}/usuarios/${encodeURIComponent(mat)}/desactivar`, {
                                method: "PATCH",
                                headers: {
                                    "Content-Type": "application/json",
                                    "Authorization": `Bearer ${token}`
                                }
                            });
                            if (!response.ok) {
                                const errorData = await response.json();
                                throw new Error(errorData.error || "Error al desactivar miembro");
                            }
                            alert("Miembro eliminado correctamente");
                            setModalActivo(null);
                            await cargarMiembros();
                        } catch (error) {
                            console.error("Error al desactivar miembro:", error);
                            alert(error.message || "Error al desactivar miembro");
                        }
                    }}
                />
            )}

            {modalActivo === 'detalles' && (
                <VerDetallesUsuario
                    tipo="Miembro"
                    usuario={usuarioSeleccionado}
                    alCerrar={() => setModalActivo(null)}
                />
            )}

            {modalActivo === 'historial' && (
                <VerHistorialPagosUsuario
                    tipo="Miembro"
                    usuario={usuarioSeleccionado}
                    alCerrar={() => setModalActivo(null)}
                />
            )}
        </div>
    );
};

export default DashboardLider;