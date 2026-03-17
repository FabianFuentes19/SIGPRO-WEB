import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './DashProyecto.css';
import ModalRegistrarLider from './ModalRegistrarLider';

const DashLideres = () => {
  const [mostrarModal, setMostrarModal] = useState(false);
  const [lideres, setLideres] = useState([]);
  const [busqueda, setBusqueda] = useState("");

  const fetchLideres = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8082/usuarios", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setLideres(data);
      }
    } catch (error) {
      console.error("Error al cargar líderes:", error);
    }
  };

  useEffect(() => {
    fetchLideres();
  }, []);

  const registrarLider = async (nuevoLider) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8082/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ ...nuevoLider, rol: "LIDER" }),
      });

      if (response.ok) {
        alert("Líder agregado correctamente");
        setMostrarModal(false);
        fetchLideres();
      } else {
        alert("Error al agregar líder");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Error de conexión con el servidor");
    }
  };

  const eliminarLider = async (id) => {
    if (!window.confirm("¿Estás seguro de eliminar a este líder?")) return;
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8082/usuarios/${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (response.ok) {
        alert("Líder eliminado");
        fetchLideres();
      }
    } catch (error) {
      console.error("Error al eliminar:", error);
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
            <Link to="/login" className="logout-btn" onClick={() => localStorage.clear()}>Salir</Link>
          </div>
        </aside>

        <main className="main-content">
          <div className="content-actions">
            <input
              type="text"
              className="form-control search-input"
              placeholder="Buscar líder"
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
                    <th>NOMBRE</th>
                    <th>MATRÍCULA</th>
                    <th>CORREO</th>
                    <th>ACCIONES</th>
                  </tr>
                </thead>
                <tbody>
                  {lideres.length === 0 ? (
                    <tr>
                      <td colSpan="5" style={{ textAlign: 'center', padding: '30px', color: '#6c757d' }}>
                        No hay líderes registrados aún.
                      </td>
                    </tr>
                  ) : (
                    lideres
                      .filter((l) =>
                        (l.nombre + " " + l.apellidoPaterno + " " + l.apellidoMaterno).toLowerCase().includes(busqueda.toLowerCase()) ||
                        l.matricula.toLowerCase().includes(busqueda.toLowerCase())
                      )
                      .map((l, index) => (
                        <tr key={l.id}>
                          <td>{index + 1}</td>
                          <td>{`${l.nombre} ${l.apellidoPaterno} ${l.apellidoMaterno}`}</td>
                          <td>{l.matricula}</td>
                          <td>{l.correo}</td>
                          <td>
                            <button className="btn btn-sm btn-danger" onClick={() => eliminarLider(l.id)}>Eliminar</button>
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

      {mostrarModal && (
        <ModalRegistrarLider
          alCerrar={() => setMostrarModal(false)}
          alRegistrar={registrarLider}
        />
      )}
    </div>
  );
};

export default DashLideres;
