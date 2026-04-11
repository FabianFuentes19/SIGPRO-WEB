import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/DashProyecto.css';
import AgregarUsuario from '../components/AgregarUsuario';
import EditarUsuario from '../components/EditarUsuario';
import VerDetallesUsuario from '../components/VerDetallesUsuario';
import { obtenerUsuarios } from '../services/api';
import { Eye, LogOut, Pencil, Trash2 } from 'lucide-react';
import BorrarUsuario from '../components/BorrarUsuario';
import ModalCerrarSesion from '../components/Usuarios/ModalCerrarSesion';
import ModalMensajes from '../components/Usuarios/ModalMensajes';
import Pagination from '../components/Pagination';
import '../css/Pagination.css';


const DashLideres = () => {
  const navigate = useNavigate();
  const [mostrarModalCerrarSesion, setMostrarModalCerrarSesion] = useState(false);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);
  const [mostrarModalConsultar, setMostrarModalConsultar] = useState(false);
  const [mostrarModalEliminar, setMostrarModalEliminar] = useState(false);

  const [lideres, setLideres] = useState([]);
  const [paginaActual, setPaginaActual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [tamanoPagina] = useState(10);

  const [liderSeleccionado, setLiderSeleccionado] = useState(null);
  const [busqueda, setBusqueda] = useState("");

  const [mensajeModal, setMensajeModal] = useState(null);

  // Nueva búsqueda: Resetear a página 0
  useEffect(() => {
    setPaginaActual(0);
  }, [busqueda]);

  useEffect(() => {
    fetchLideres(paginaActual);
  }, [paginaActual, busqueda]);

  const handleCerrarSesion = () => {
    localStorage.clear();
    navigate('/login');
  };

  const fetchLideres = async (page = 0) => {
    try {
      const data = await obtenerUsuarios("LIDER", page, tamanoPagina, busqueda);
      setLideres(data.content || []);
      setTotalPaginas(data.totalPages || 0);
      // Importante: No forzar setPaginaActual aquí para evitar loops si ya estamos en esa página
    } catch (error) {
      console.error("Error al cargar líderes:", error);
    }
  };

  const registrarLider = async (nuevoLider) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/usuarios/registrar/lider", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(nuevoLider),
      });

      if (response.ok) {
        const data = await response.json();
        setMensajeModal({
          titulo: "Registro Exitoso",
          mensaje: data.mensaje || "Líder agregado correctamente",
          tipo: "exito"
        });
        setMostrarModal(false);
        fetchLideres(paginaActual);
      } else {
        const errorData = await response.json();
        setMensajeModal({
          titulo: "Error",
          mensaje: errorData.error || "Error al agregar líder",
          tipo: "error"
        });
      }
    } catch (error) {
      console.error("Error:", error);
      setMensajeModal({
        titulo: "Error",
        mensaje: "Error de conexión con el servidor",
        tipo: "error"
      });
    }
  };

  const actualizarLider = async (datosActualizados) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/usuarios/${liderSeleccionado.matricula}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(datosActualizados),
      });

      if (response.ok) {
        setMensajeModal({
          titulo: "Actualización Exitosa",
          mensaje: "Líder actualizado correctamente",
          tipo: "exito"
        });
        setMostrarModalEditar(false);
        fetchLideres(paginaActual);
      } else {
        const errorData = await response.json();
        setMensajeModal({
          titulo: "Error",
          mensaje: errorData.error || "Error al actualizar líder",
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

  const eliminarLider = async (matricula) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/usuarios/${matricula}/desactivar`, {
        method: "PATCH",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (response.ok) {
        setMostrarModalEliminar(false);
        setMensajeModal({
          titulo: "Eliminación Exitosa",
          mensaje: "Líder desactivado correctamente",
          tipo: "exito"
        });
        fetchLideres(paginaActual);
      } else {
        const errorData = await response.json();
        setMensajeModal({
          titulo: "Error",
          mensaje: errorData.error || "No se pudo desactivar el líder",
          tipo: "error"
        });
      }
    } catch (error) {
      console.error("Error al desactivar:", error);
      setMensajeModal({
        titulo: "Error",
        mensaje: "Error de conexión con el servidor",
        tipo: "error"
      });
    }
  };

  const activarLider = async (matricula) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/usuarios/${matricula}/activar`, {
        method: "PATCH",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (response.ok) {
        setMensajeModal({
          titulo: "Activación Exitosa",
          mensaje: "Líder activado correctamente",
          tipo: "exito"
        });
        fetchLideres(paginaActual);
      } else {
        const errorData = await response.json();
        setMensajeModal({
          titulo: "Error",
          mensaje: errorData.error || "No se pudo activar el líder",
          tipo: "error"
        });
      }
    } catch (error) {
      console.error("Error al activar:", error);
      setMensajeModal({
        titulo: "Error",
        mensaje: "Error de conexión con el servidor",
        tipo: "error"
      });
    }
  };

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-brand">
          <h4>Panel Administrador</h4>
        </div>
        <div className="header-title">
          <h4>Líderes</h4>
        </div>
      </header>

      <div className="dashboard-body">
        <aside className="sidebar">
          <nav className="sidebar-nav">
            <Link to="/lideres" className="nav-item active">
              <span>Líderes</span>
            </Link>
            <Link to="/proyectos" className="nav-item">
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
              placeholder="Buscar por matrícula o nombre"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
            <button className="btn add-btn" onClick={() => setMostrarModal(true)}>Agregar Líder</button>
          </div>

          <div className="table-wrapper">
            <div className="table-responsive">
              <table className="table custom-table">
                <thead>
                  <tr>
                    <th>NO.</th>
                    <th>NOMBRE COMPLETO</th>
                    <th>MATRÍCULA</th>
                    <th>ESTADO</th>
                    <th>ACCIONES</th>
                  </tr>
                </thead>
                <tbody>
                  {lideres.length === 0 ? (
                    <tr>
                      <td colSpan="5" style={{ textAlign: 'center', padding: '30px', color: '#6c757d' }}>
                        No hay registros existentes.
                      </td>
                    </tr>
                  ) : (
                    lideres.map((l, index) => (
                        <tr key={l.matricula}>
                          <td>{paginaActual * tamanoPagina + index + 1}</td>
                          <td>{l.nombreCompleto}</td>
                          <td>{l.matricula}</td>
                          <td>
                            <span className={`badge ${l.estado === 'ACTIVO' ? 'bg-success' : 'bg-danger'}`}>
                              {l.estado}
                            </span>
                          </td>
                          <td>
                            <div className="dropdown-container">
                              <div className="dropdown-item" onClick={() => { setLiderSeleccionado(l); setMostrarModalEditar(true); }}>
                                <Pencil size={14} />
                              </div>

                              <div className="dropdown-item" onClick={() => { setLiderSeleccionado(l); setMostrarModalConsultar(true); }}>
                                <Eye size={14} />
                              </div>

                              {<div className="dropdown-item" onClick={() => { setLiderSeleccionado(l); setMostrarModalEliminar(true); }}>
                                <Trash2 size={14} />
                              </div>}

                              {/*<label className="switch">
                                <input
                                  type="checkbox"
                                  checked={l.estado === "ACTIVO"}
                                  onChange={() => {
                                    if (l.estado === "ACTIVO") {
                                      eliminarLider(l.matricula);
                                    } else {
                                      activarLider(l.matricula);
                                    }
                                  }}
                                />
                                <span className="slider round"></span>
                              </label> */}

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

      {/* MODALES REUTILIZABLES */}
      {mostrarModal && (
        <AgregarUsuario
          tipo="Líder"
          alCerrar={() => setMostrarModal(false)}
          alRegistrar={registrarLider}
          onError={setMensajeModal}
        />
      )}

      {mostrarModalEditar && (
        <EditarUsuario
          tipo="Líder"
          usuario={liderSeleccionado}
          alCerrar={() => setMostrarModalEditar(false)}
          alGuardar={actualizarLider}
          onError={setMensajeModal}
        />
      )}

      {mostrarModalConsultar && (
        <VerDetallesUsuario
          tipo="Líder"
          usuario={liderSeleccionado}
          alCerrar={() => setMostrarModalConsultar(false)}
        />
      )}

      {mostrarModalEliminar && (
        <BorrarUsuario
          tipo="Líder"
          usuario={liderSeleccionado}
          alCerrar={() => setMostrarModalEliminar(false)}
          alConfirmar={() => eliminarLider(liderSeleccionado.matricula)}
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
          onConfirm={() => setMensajeModal(null)}
        />
      )}

    </div>
  );
};

export default DashLideres;
