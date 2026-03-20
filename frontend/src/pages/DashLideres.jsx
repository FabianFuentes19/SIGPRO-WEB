import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../css/DashProyecto.css';
import AgregarUsuario from '../components/AgregarUsuario';
import EditarUsuario from '../components/EditarUsuario';
import VerDetallesUsuario from '../components/VerDetallesUsuario';
import { obtenerUsuarios } from '../services/api';
import { Eye, LogOut, Pencil, Trash2 } from 'lucide-react';
import BorrarUsuario from '../components/BorrarUsuario';

const DashLideres = () => {
  const [mostrarModal, setMostrarModal] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);
  const [mostrarModalConsultar, setMostrarModalConsultar] = useState(false);
  const [mostrarModalEliminar, setMostrarModalEliminar] = useState(false);

  const [lideres, setLideres] = useState([]);
  const [liderSeleccionado, setLiderSeleccionado] = useState(null);
  const [busqueda, setBusqueda] = useState("");

  useEffect(() => {
    fetchLideres();
  }, []);

  const fetchLideres = async () => {
    try {
      const data = await obtenerUsuarios("LIDER");
      const activos = Array.isArray(data) ? data.filter(l => l.estado === "ACTIVO") : [];
      setLideres(activos);
      //setLideres(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error("Error al cargar líderes:", error);
    }
  };

  const registrarLider = async (nuevoLider) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/auth/register/lider", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(nuevoLider),
      });

      if (response.ok) {
        alert("Líder agregado correctamente");
        setMostrarModal(false);
        fetchLideres();
      } else {
        const errorData = await response.json();
        alert(errorData.error || "Error al agregar líder");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Error de conexión con el servidor");
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
        alert("Líder actualizado correctamente");
        setMostrarModalEditar(false);
        fetchLideres();
      } else {
        const errorData = await response.json();
        alert(errorData.error || "Error al actualizar líder");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Error de conexión con el servidor");
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
        alert("Líder desactivado correctamente");
        fetchLideres();
      } else {
        alert("No se pudo desactivar el líder");
      }
    } catch (error) {
      console.error("Error al desactivar:", error);
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
            <Link to="/login" className="logout-btn" onClick={() => localStorage.clear()}>
            <LogOut size={20} />
            <span>Salir</span>
            </Link>
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
                    lideres
                      .filter((l) =>
                        l.nombreCompleto.toLowerCase().includes(busqueda.toLowerCase()) ||
                        l.matricula.toLowerCase().includes(busqueda.toLowerCase())
                      )
                      .map((l, index) => (
                        <tr key={l.matricula}>
                          <td>{index + 1}</td>
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

                                 {/* <label className="switch">
                                  <input
                                    type="checkbox"
                                    checked={l.estado === "ACTIVO"}
                                    onChange={() => {
                                      setLiderSeleccionado(l);
                                      if (l.estado === "ACTIVO") {
                                        eliminarLider(l.matricula);
                                      } else {
                                        //falta implementar desactivar en back
                                      }
                                    }}
                                  />
                                  <span className="slider round"></span>
                                </label>*/}

                            </div>
                          </td>
                        </tr>
                      ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </main>
      </div>

      {/* MODALES REUTILIZABLES */}
      {mostrarModal && (
        <AgregarUsuario
          tipo="Líder"
          alCerrar={() => setMostrarModal(false)}
          alRegistrar={registrarLider}
        />
      )}

      {mostrarModalEditar && (
        <EditarUsuario
          tipo="Líder"
          usuario={liderSeleccionado}
          alCerrar={() => setMostrarModalEditar(false)}
          alGuardar={actualizarLider}
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
    </div>
  );
};

export default DashLideres;
