import React from 'react';
import './DashProyecto.css';

const DashProyectos = () => {   
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
            <button className="btn add-btn">Agregar</button>
          </div>

          <div className="table-wrapper">
            <div className="table-responsive">
              <table className="table custom-table">
                <thead>
                  <tr>
                    <th>NO.</th>
                    <th>NOMBRE</th>
                    <th>LIDER</th>
                    <th>DESCRIPCIÓN</th>
                    <th>PRESUPUESTO</th>
                    <th>ACCIONES</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colSpan="6" style={{ textAlign: 'center', padding: '30px', color: '#6c757d' }}>
                      No hay proyectos registrados aún.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default DashProyectos;   
