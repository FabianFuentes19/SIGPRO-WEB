import React, { useState, useEffect, useRef } from 'react';
import './DashLider.css';
import AgregarUsuario from '../AgregarUsuario.jsx';
import Materiales from '../Materiales/Materiales.jsx';
import EditarUsuario from '../EditarUsuario.jsx';
import BorrarUsuario from '../BorrarUsuario.jsx';
import VerDetallesUsuario from '../VerDetallesUsuario.jsx';
import VerHistorialPagosUsuario from '../VerHistorialPagosUsuario.jsx';
import Nominas from '../../pages/Nominas.jsx';
import { formatCurrency, formatCurrencyWithSign } from '../../utils/formatters';

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
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { registrarMiembro, apiFetch } from '../../services/api.js';
import PerfilLider from './PerfilLider.jsx';
import ModalCerrarSesion from '../Usuarios/ModalCerrarSesion.jsx';
import ModalMensajes from '../Usuarios/ModalMensajes.jsx';

const BASE_URL = "http://localhost:8080";

const DashboardLider = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [mostrarModal, setMostrarModal] = useState(false);
    const [mostrarCerrarSesion, setMostrarCerrarSesion] = useState(false);
    const [vistaActual, setVistaActual] = useState(location.state?.vista || 'proyecto');

    // Estados para CRUD miembros (Tres puntitos)
    const [menuAbiertoId, setMenuAbiertoId] = useState(null);
    const [usuarioSeleccionado, setUsuarioSeleccionado] = useState(null);
    const [modalActivo, setModalActivo] = useState(null);
    const [miembros, setMiembros] = useState([]);
    const [proyecto, setProyecto] = useState(null);
    const [proyectoId, setProyectoId] = useState(null);
    const [loadingProyecto, setLoadingProyecto] = useState(true);
    const [mensajeModal, setMensajeModal] = useState(null);

    // rastrea el presupuesto anterior y evitar alertas repetitivas al navegar
    const lastBudgetRef = useRef(null);

    // Función para calcular el estado presupuesto
    const calculateBudgetStatus = (actual, autorizado) => {
        if (!autorizado || autorizado <= 0) return { perc: 0, colorClass: 'budget-exhausted', status: 'UNKNOWN', text: '' };
        const perc = (actual / autorizado) * 100;

        if (perc <= 0) return { perc: 0, colorClass: 'budget-exhausted', status: 'CRITICAL', text: 'Presupuesto Agotado' };
        if (perc <= 10) return { perc, colorClass: 'budget-critical', status: 'CRITICAL', text: 'Te queda menos del 10% de presupuesto' };
        if (perc <= 20) return { perc, colorClass: 'budget-warning', status: 'WARNING', text: 'Te queda menos del 20% de presupuesto' };
        return { perc, colorClass: 'budget-healthy', status: 'OK', text: 'Equilibrado' };
    };


    const cargarProyecto = async (triggerAlert = false) => {
        const token = localStorage.getItem("token");
        if (!token) {
            setLoadingProyecto(false);
            return;
        }
        try {
            setLoadingProyecto(true);
            const response = await apiFetch("/proyectos/mi-proyecto/lider");
            
            if (response.status === 404) {
                setProyecto(null);
                setProyectoId(null);
                return;
            }

            if (!response.ok) throw new Error("No se pudo obtener el proyecto");
            const data = await response.json();

            // verifica si al insertar un gasto el presupuesto entra en riesgo
            if (triggerAlert && lastBudgetRef.current !== null && lastBudgetRef.current !== data.presupuesto) {
                const statusInfo = calculateBudgetStatus(data.presupuesto, data.presupuestoAutorizado || data.presupuestoInicial);
                if (statusInfo.status !== 'OK') {
                    setMensajeModal({
                        titulo: "ALERTA",
                        mensaje: statusInfo.text,
                        tipo: statusInfo.status === 'CRITICAL' ? "error" : "advertencia"
                    });
                }

            }

            lastBudgetRef.current = data.presupuesto;
            setProyecto(data);
            setProyectoId(data.id);
        } catch (error) {
            console.error("Error al cargar proyecto:", error);
            setProyecto(null);
        } finally {
            setLoadingProyecto(false);
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
            const response = await apiFetch(`/usuarios/lider/${encodeURIComponent(matriculaLider)}`);
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

    const registrarMiembro = async (datos) => {
        try {
            if (!proyectoId) {
                setMensajeModal({
                    titulo: "Error",
                    mensaje: "No se encontró el proyecto del líder. Recarga la página.",
                    tipo: "error"
                });
                return;
            }
            const token = localStorage.getItem("token");
            const response = await apiFetch(`/proyectos/${proyectoId}/miembros`, {
                method: "POST",
                body: JSON.stringify(datos)
            });
            const data = await response.json();
            if (!response.ok) throw new Error(data.error || "Error al registrar miembro");

            setMostrarModal(false);
            setMensajeModal({
                titulo: "Registro Exitoso",
                mensaje: "Miembro registrado correctamente",
                tipo: "exito"
            });
            await cargarMiembros();
        } catch (error) {
            setMensajeModal({
                titulo: "Error",
                mensaje: error.message || "Error al registrar miembro",
                tipo: "error"
            });
        }
    };

    const actualizarMiembro = async (datosActualizados) => {
        try {
            const token = localStorage.getItem("token");
            const response = await apiFetch(`/usuarios/${usuarioSeleccionado.matricula}`, {
                method: "PUT",
                body: JSON.stringify(datosActualizados),
            });

            if (response.ok) {
                const updatedData = await response.json(); // Esto recibe el objeto actualizado del backend
                setUsuarioSeleccionado(updatedData);       // Este es para que refresque el modal de detalles
                setMensajeModal({
                    titulo: "Actualización Exitosa",
                    mensaje: "Miembro actualizado correctamente",
                    tipo: "exito"
                });
                setModalActivo(null);
                await cargarMiembros(); // refresca la lista completa
            }

            else {
                const errorData = await response.json();
                setMensajeModal({
                    titulo: "Error",
                    mensaje: errorData.error || "Error al actualizar miembro",
                    tipo: "error"
                });
            }
        } catch (error) {
            setMensajeModal({
                titulo: "Error",
                mensaje: "Error de conexión con el servidor",
                tipo: "error"
            });
        }
    };

    const eliminarMiembro = async (mat) => {
        try {
            const token = localStorage.getItem("token");
            const response = await apiFetch(`/usuarios/${encodeURIComponent(mat)}/desactivar`, {
                method: "PATCH"
            });
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || "Error al desactivar miembro");
            }
            setMensajeModal({
                titulo: "Eliminación Exitosa",
                mensaje: "Miembro eliminado correctamente",
                tipo: "exito"
            });
            setModalActivo(null);
            await cargarMiembros();
        } catch (error) {
            setMensajeModal({
                titulo: "Error",
                mensaje: error.message || "Error al desactivar miembro",
                tipo: "error"
            });
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
                        vistaActual === 'materiales' ? 'Materiales' :
                            vistaActual === 'perfil' ? 'Perfil' : 'Nóminas'}
                </div>
                <div className="header-user" onClick={() => setVistaActual('perfil')} style={{ cursor: 'pointer' }}>
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
                        <button className="logout-btn" onClick={() => setMostrarCerrarSesion(true)}>
                            <LogOut size={20} />
                            <span>Salir</span>
                        </button>
                    </div>
                </aside>

                <main className="main-content">
                    {vistaActual === 'proyecto' && (
                        <>
                            {loadingProyecto ? (
                                <div className="loading-container">
                                    <div className="spinner"></div>
                                    <p>Cargando información del proyecto...</p>
                                </div>
                            ) : proyecto && proyecto.id ? (
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
                                        <div className="budget-main-display">
                                            <span className="budget-label">Restante</span>
                                            <h3 className="budget-value">{formatCurrencyWithSign(proyecto.presupuesto)}</h3>
                                        </div>
                                    </div>

                                    <div className="progress-section-bottom">
                                        {(() => {
                                            const status = calculateBudgetStatus(proyecto.presupuesto, proyecto.presupuestoAutorizado || proyecto.presupuestoInicial);
                                            return (
                                                <>
                                                    <div className="progress-info-row">
                                                        <span>Estado del presupuesto</span>
                                                        <span className={`progress-perc ${status.colorClass.replace('budget-', 'text-')}`}>
                                                            {Math.round(status.perc)}%
                                                        </span>
                                                    </div>
                                                    <div className="budget-progress-outer">
                                                        <div
                                                            className={`budget-progress-inner ${status.colorClass}`}
                                                            style={{ width: `${Math.min(status.perc, 100)}%` }}
                                                        ></div>
                                                    </div>
                                                    <div className="budget-summary-row">
                                                        <span className={status.colorClass.replace('budget-', 'text-')}>{status.text}</span>
                                                        <span>Consumido: {formatCurrencyWithSign((proyecto.presupuestoAutorizado || proyecto.presupuestoInicial) - proyecto.presupuesto)}</span>
                                                    </div>
                                                </>
                                            );
                                        })()}
                                    </div>
                                </div>
                            ) : (
                                <div className="no-project-card">
                                    <Box size={40} strokeWidth={1.5} />
                                    <div className="no-project-info">
                                        <h3>Sin proyecto asignado</h3>
                                        <p>Actualmente no cuentas con un proyecto vinculado. Una vez que se te asigne uno, podrás gestionar a tus miembros y registrar materiales/nóminas.</p>
                                    </div>
                                </div>
                            )}

                            <div className="members-section">
                                <div className="members-top-row">
                                    <h2>Miembros</h2>
                                    <button 
                                        className="gold-add-btn" 
                                        onClick={() => setMostrarModal(true)}
                                        disabled={!proyecto || !proyecto.id}
                                    >
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

                    {vistaActual === 'materiales' && <Materiales proyectoId={proyectoId} onMaterialSuccess={() => cargarProyecto(true)} />}
                    {vistaActual === 'nominas' && <Nominas onPaymentSuccess={() => cargarProyecto(true)} />}
                    {vistaActual === 'perfil' && <PerfilLider />}

                </main>
            </div>

            {mostrarModal && (
                <AgregarUsuario
                    tipo="Miembro"
                    alCerrar={() => setMostrarModal(false)}
                    alRegistrar={registrarMiembro}
                    onError={setMensajeModal}
                />
            )}

            {modalActivo === 'editar' && (
                <EditarUsuario
                    tipo="Miembro"
                    usuario={usuarioSeleccionado}
                    alCerrar={() => setModalActivo(null)}
                    alGuardar={actualizarMiembro}
                    onError={setMensajeModal}
                />
            )}

            {modalActivo === 'borrar' && (
                <BorrarUsuario
                    tipo="Miembro"
                    usuario={usuarioSeleccionado}
                    alCerrar={() => setModalActivo(null)}
                    alConfirmar={eliminarMiembro}
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

            {/* Este es el modal para cerrar sesión*/}
            {mostrarCerrarSesion && (
                <ModalCerrarSesion
                    alCancelar={() => setMostrarCerrarSesion(false)}
                    alAceptar={() => {
                        localStorage.clear();
                        navigate('/login');
                    }}
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

export default DashboardLider;