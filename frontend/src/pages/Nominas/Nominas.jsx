import React, { useState } from 'react';
import './Nominas.css';
import NominaCard from '../../components/Nominas/NominaCard';
import { Search, ChevronDown } from 'lucide-react';

const Nominas = () => {
  const [busqueda, setBusqueda] = useState("");
  const [filtro, setFiltro] = useState("Todos");

  // Datos mockeados basados en la imagen
  const [nominas, setNominas] = useState([
    { id: 1, nombre: "Marcos Ortíz", puesto: "Diseñador gráfico", voucher: "00123", monto: 15000, estado: "PENDIENTE", fecha: "15/10/2023" },
    { id: 2, nombre: "Roberto Sánchez", puesto: "Ingeniero Civil", voucher: "00119", monto: 18000, estado: "PAGADO", fecha: "15/10/2023" },
    { id: 3, nombre: "Fabian Fuentes", puesto: "Arquitecto", voucher: "00129", monto: 16000, estado: "PAGADO", fecha: "15/10/2023" },
    { id: 4, nombre: "Zurisaddai Rodriguez", puesto: "Diseñador gráfico", voucher: "00122", monto: 17000, estado: "PENDIENTE", fecha: "15/10/2023" }
  ]);

  const handlePay = (id) => {
    // Pendiente: Integrar con el backend
    console.log("Pagar nómina ID:", id);
  };

  const nominasFiltradas = nominas;

  return (
    <div className="nominas-container">
      <div className="nominas-filters">
        <div className="search-bar-wrapper">
          <Search className="search-icon" size={18} />
          <input
            type="text"
            placeholder="Buscar por nombre o puesto"
            className="nomina-search-input"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>

        <div className="dropdown-filter">
          <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
            <option value="Todos">Todos</option>
            <option value="Pagados">Pagados</option>
            <option value="Pendientes">Pendientes</option>
          </select>
          <ChevronDown className="dropdown-icon" size={16} />
        </div>
      </div>

      <div className="nominas-grid">
        {nominasFiltradas.length > 0 ? (
          nominasFiltradas.map(nomina => (
            <NominaCard key={nomina.id} nomina={nomina} onPay={handlePay} />
          ))
        ) : (
          <div className="no-results">No se encontraron nóminas para mostrar.</div>
        )}
      </div>
    </div>
  );
};

export default Nominas;
