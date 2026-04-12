import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/DashProyecto.css';
import ModalRegistrarProyecto from '../components/ModalRegistrarProyecto';
import ModalConsultarProyecto from '../components/ModalConsultarProyecto';
import ModalEditarProyecto from '../components/ModalEditarProyecto';
import ModalCerrarSesion from '../components/Usuarios/ModalCerrarSesion';
import { Eye, LogOut, Pencil } from 'lucide-react';
import ModalMensajes from '../components/Usuarios/ModalMensajes'
import Pagination from '../components/Pagination';
import '../css/Pagination.css';
import { formatCurrencyWithSign } from '../utils/formatters';

const DashProyectos = () => {
  const navigate = useNavigate();
  // Estado para controlar los modales
  const [mostrarModalCerrarSesion, setMostrarModalCerrarSesion] = useState(false);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [mostrarModalConsultar, setMostrarModalConsultar] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);
  const [mensajeModal, setModalMensajes] = useState(null);

  // Estado para guardar la lista de proyectos y paginación
  const [proyectos, setProyectos] = useState([]);
  const [paginaActual, setPaginaActual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [tamanoPagina] = useState(10);

  // Estado para búsqueda
  const [busqueda, setBusqueda] = useState("");

  // Estado para proyecto seleccionado
  const [proyectoSeleccionado, setProyectoSeleccionado] = useState(null);

  // Función para traer proyectos desde el backend
  const fetchProjects = async (page = 0) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/proyectos?page=${page}&size=${tamanoPagina}`, {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setProyectos(data.content || []);
        setTotalPaginas(data.totalPages || 0);
        setPaginaActual(data.pageNumber || 0);
      }
    } catch (error) {
      console.error("Error al cargar proyectos:", error);
    }
  };

  // Cargar proyectos al montar el componente o al cambiar de página
  useEffect(() => {
    fetchProjects(paginaActual);
  }, [paginaActual]);

  const handleCerrarSesion = () => {
    localStorage.clear();
    navigate('/login');
  };

  // Función que se ejecuta cuando se registra un nuevo proyecto
  const registrarProyecto = async (nuevoProyecto) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/proyectos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(nuevoProyecto),
      });

      // Esto agrege para que se muestre el mensaje
      if (response.ok) {
        const data = await response.json();
        console.log("Proyecto registrado en backend:", data);
        setModalMensajes({
          titulo: "Registro Exitoso",
          mensaje: "Proyecto agregado correctamente",
          tipo: "exito"
        });
        fetchProjects();
      } else {
        const errorData = await response.json();
        setModalMensajes({
          titulo: "Error",
          mensaje: errorData.error || "Error al agregar proyecto",
          tipo: "error"
        });
      }

    } catch (error) {
      console.error("Error:", error);
      //alert("Error de conexión con el servidor");
    }
  };

  // Función para actualizar proyecto
  const actualizarProyecto = async (proyectoActualizado) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/proyectos/${proyectoActualizado.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(proyectoActualizado),
      });

      //Agregue esto parea que se muestre el mensaje de actulización exitosa
      if (response.ok) {
        setModalMensajes({
          titulo: "Actualización Exitosa",
          mensaje: "Proyecto actualizado correctamente",
          tipo: "exito"
        });
        fetchProjects();
      } else {
        const errorData = await response.json();
        setModalMensajes({
          titulo: "Error",
          mensaje: errorData.error || "Error al actualizar proyecto",
          tipo: "error"
        });
      }

    } catch (error) {
      console.error("Error:", error);
      alert("Error de conexión con el servidor");
    }
  };

  // Función para determinar el estado visual del presupuesto
  const calculateBudgetStatus = (actual, autorizado) => {
    if (!autorizado || autorizado <= 0) return { perc: 0, colorClass: 'budget-exhausted', text: '' };
    const perc = (actual / autorizado) * 100;

    if (perc <= 0) return { perc: 0, colorClass: 'budget-exhausted', text: 'Agotado' };
    if (perc <= 10) return { perc, colorClass: 'budget-critical', text: 'Crítico' };
    if (perc <= 20) return { perc, colorClass: 'budget-warning', text: 'En riesgo' };
    return { perc, colorClass: 'budget-healthy', text: 'Equilibrado' };
  };

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-brand">
          <h4>Panel Administrador</h4>
        </div>
        <div className="header-title">
          <h4>Proyectos</h4>
        </div>
      </header>

      <div className="dashboard-body">
        <aside className="sidebar">
          <nav className="sidebar-nav">
            <Link to="/lideres" className="nav-item">
              <span>Líderes</span>
            </Link>
            <Link to="/proyectos" className="nav-item active">
              <span>Proyectos</span>
            </Link>
          </nav>
          <div className="sidebar-footer">
            <button className="logout-btn" onClick={(e) => { e.preventDefault(); setMostrarModalCerrarSesion(true); }}>
              <LogOut size={20} />
              <span>Salir</span>
            </button>
          </div>
        </aside>

        <main className="main-content">
          <div className="content-actions">
            <input
              type="text"
              className="form-control search-input"
              placeholder="Buscar"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
            {/* Botón para abrir modal de registro */}
            <button className="btn add-btn" onClick={() => setMostrarModal(true)}>Agregar</button>
          </div>

          <div className="table-wrapper">
            <div className="table-responsive">
              <table className="table custom-table">
                <thead>
                  <tr>
                    <th>NO.</th>
                    <th>NOMBRE</th>
                    <th>LÍDER</th>
                    <th>PRESUPUESTO INICIAL</th>
                    <th>AUTORIZADO</th>
                    <th>RESTANTE</th>
                    <th>PROGRESO</th>
                    <th>ESTADO</th>
                    <th>ACCIONES</th>
                  </tr>
                </thead>
                <tbody>
                  {proyectos.length === 0 ? (
                    <tr>
                      <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#6c757d' }}>
                        No hay proyectos registrados aún.
                      </td>
                    </tr>
                  ) : (
                    proyectos
                      .filter((p) =>
                        (p.nombre || "").toLowerCase().includes(busqueda.toLowerCase()) ||
                        (p.liderNombre || "").toLowerCase().includes(busqueda.toLowerCase()) ||
                        (p.descripcion || "").toLowerCase().includes(busqueda.toLowerCase())
                      )
                      .map((p, index) => (
                        <tr key={p.id}>
                          <td>{index + 1}</td>
                          <td>{p.nombre}</td>
                          <td>{p.liderNombre}</td>
                          <td className="text-right">
                            {formatCurrencyWithSign(p.presupuestoInicial)}
                          </td>
                          <td className="text-right">
                            {formatCurrencyWithSign(p.presupuestoAutorizado || p.presupuestoInicial)}
                          </td>
                          <td className="text-right">
                            {formatCurrencyWithSign(p.presupuesto)}
                          </td>
                          <td>
                            <div className="budget-cell-container">
                              {(() => {
                                const status = calculateBudgetStatus(p.presupuesto, p.presupuestoAutorizado || p.presupuestoInicial);
                                return (
                                  <>
                                    <div className="budget-progress-outer">
                                      <div
                                        className={`budget-progress-inner ${status.colorClass}`}
                                        style={{ width: `${Math.min(status.perc, 100)}%` }}
                                      ></div>
                                    </div>
                                    <span className={`budget-status-text ${status.colorClass.replace('budget-', 'text-')}`}>
                                      {Math.round(status.perc)}%
                                    </span>
                                  </>
                                );
                              })()}
                            </div>
                          </td>
                          <td>
                            {/*Agrego esto para poner el estado del Proyecto*/}
                            <span className={`badge ${p.estado === 'ACTIVO' ? 'bg-success' : 'bg-danger'}`}>
                              {p.estado === 'ACTIVO' ? 'ACTIVO' : 'INACTIVO'}
                            </span>
                          </td>
                          <td>
                            <div className="dropdown-container">
                              <div className="dropdown-item" onClick={() => { setProyectoSeleccionado(p); setMostrarModalEditar(true); }}>
                                <Pencil size={14} />
                              </div>

                              <div className="dropdown-item" onClick={() => { setProyectoSeleccionado(p); setMostrarModalConsultar(true); }}>
                                <Eye size={14} />
                              </div>
                            </div>
                          </td>
                        </tr>
                      ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
          <Pagination
            currentPage={paginaActual}
            totalPages={totalPaginas}
            onPageChange={(p) => setPaginaActual(p)}
          />
        </main>
      </div>

      {/* Modal Registrar */}
      {mostrarModal && (
        <ModalRegistrarProyecto
          alCerrar={() => setMostrarModal(false)}
          alRegistrar={registrarProyecto}
          setModalMensajes={setModalMensajes}
        />
      )}

      {/* Modal Consultar */}
      {mostrarModalConsultar && (
        <ModalConsultarProyecto
          proyecto={proyectoSeleccionado}
          onClose={() => setMostrarModalConsultar(false)}
        />
      )}

      {/* Modal Editar */}
      {mostrarModalEditar && (
        <ModalEditarProyecto
          proyecto={proyectoSeleccionado}
          alCerrar={() => setMostrarModalEditar(false)}
          alActualizar={actualizarProyecto}
          setModalMensajes={setModalMensajes}
        />
      )}

      {/* Modal Cerrar Sesion */}
      {mostrarModalCerrarSesion && (
        <ModalCerrarSesion
          alCancelar={() => setMostrarModalCerrarSesion(false)}
          alAceptar={handleCerrarSesion}
        />
      )}


      {mensajeModal && (
        <ModalMensajes
          titulo={mensajeModal.titulo}
          mensaje={mensajeModal.mensaje}
          tipo={mensajeModal.tipo}
          onConfirm={() => setModalMensajes(null)}
          onCancel={() => setModalMensajes(null)}
        />
      )}
    </div>
  );
};

export default DashProyectos;