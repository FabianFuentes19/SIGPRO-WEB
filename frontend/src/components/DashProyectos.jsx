import React, { useState, useEffect } from 'react';
import './DashProyecto.css';
import ModalRegistrarProyecto from './ModalRegistrarProyecto';
import ModalConsultarProyecto from './ModalConsultarProyecto';
import ModalEditarProyecto from './ModalEditarProyecto';



const DashProyectos = () => {  
  // Estado para controlar los momdales modales
  const [mostrarModal, setMostrarModal] = useState(false);
  const [mostrarModalConsultar, setMostrarModalConsultar] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);

  // Estado para guardar la lista de proyectos
  const [proyectos, setProyectos] = useState([]);

  // Estado para proyecto seleccionado
  const [proyectoSeleccionado, setProyectoSeleccionado] = useState(null);

  // Función para traer proyectos desde el backend
  const fetchProjects = async () => {
    try {
      const response = await fetch("http://localhost:8085/proyectos");
      if (response.ok) {
        const data = await response.json();
        setProyectos(data);
      }
    } catch (error) {
      console.error("Error al cargar proyectos:", error);
    }
  };

  // Cargar proyectos al montar el componente
  useEffect(() => {
    fetchProjects();
  }, []);

  // Función que se ejecuta cuando se registra un nuevo proyecto
  const registrarProyecto = async (nuevoProyecto) => {
    try {
      const response = await fetch("http://localhost:8085/proyectos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          
        },
        body: JSON.stringify(nuevoProyecto),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("Proyecto registrado en backend:", data);
        alert("Proyecto agregado correctamente");
        fetchProjects(); // este es para que se recargue la liste de proyectos
      } else {
        alert("Error al agregar proyecto");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Error de conexión con el servidor");
    }
  };

  // Función para actualizar proyecto
  const actualizarProyecto = async (proyectoActualizado) => {
    try {
      const response = await fetch(`http://localhost:8085/proyectos/${proyectoActualizado.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(proyectoActualizado),
      });

      if (response.ok) {
        alert("Proyecto actualizado correctamente");
        fetchProjects();
      } else {
        alert("Error al actualizar proyecto");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Error de conexión con el servidor");
    }
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
            <a href="#" className="nav-item">
              <span>Líderes</span>
            </a>
            <a href="#" className="nav-item active">
              <span>Proyectos</span>
            </a>
          </nav>
          <div className="sidebar-footer">
            <a href="#" className="logout-btn">Salir</a>
          </div>
        </aside>

        <main className="main-content">
          <div className="content-actions">
            <input type="text" className="form-control search-input" placeholder="Buscar" />
            {/* Este es el boton para que abra el modal de registro*/}
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
                    <th>DESCRIPCIÓN</th>
                    <th>PRESUPUESTO</th>
                    <th>ACCIONES</th>
                  </tr>
                </thead>
                <tbody>
                  {proyectos.length === 0 ? (
                    <tr>
                      <td colSpan="6" style={{ textAlign: 'center', padding: '30px', color: '#6c757d' }}>
                        No hay proyectos registrados aún.
                      </td>
                    </tr>
                  ) : (
                    proyectos.map((p, index) => (
                      <tr key={p.id}>
                        <td>{index + 1}</td>
                        <td>{p.nombre}</td>
                        <td>{p.lider}</td>
                        <td>{p.descripcion}</td>
                        <td>{p.presupuesto}</td>
                        <td>
                          <button 
                            className="btn btn-sm" 
                            onClick={() => { setProyectoSeleccionado(p); setMostrarModalConsultar(true); }}
                          >
                            Consultar
                          </button>
                          <button 
                            className="btn btn-sm" 
                            onClick={() => { setProyectoSeleccionado(p); setMostrarModalEditar(true); }}
                          >
                            Editar
                          </button>
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

      {/* Modal Registrar */}
      {mostrarModal && (
        <ModalRegistrarProyecto
          alCerrar={() => setMostrarModal(false)}
          alRegistrar={registrarProyecto}
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
        />
      )}
    </div>
  );
};

export default DashProyectos;
