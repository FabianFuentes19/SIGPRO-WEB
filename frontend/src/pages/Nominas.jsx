import React, { useState, useEffect } from 'react';
import '../css/Nominas.css';
import NominaCard from '../components/Nominas/NominaCard';
import { Search, ChevronDown, Loader2 } from 'lucide-react';

const BASE_URL = "http://localhost:8080";

const Nominas = () => {
  const [busqueda, setBusqueda] = useState("");
  const [filtro, setFiltro] = useState("Todos");
  const [nominas, setNominas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [proyectoId, setProyectoId] = useState(null);

  const obtenerProyecto = async (token) => {
    const resp = await fetch(`${BASE_URL}/proyectos/mi-proyecto/lider`, {
      headers: { "Authorization": `Bearer ${token}` }
    });
    if (!resp.ok) return null;
    const data = await resp.json();
    return data.id;
  };


  const obtenerMiembrosProyecto = async (token, matriculaLider) => {
    const resp = await fetch(`${BASE_URL}/usuarios/lider/${encodeURIComponent(matriculaLider)}`, {
      headers: { "Authorization": `Bearer ${token}` }
    });
    if (!resp.ok) throw new Error("No se pudieron cargar los miembros del equipo.");
    return await resp.json();
  };

  const obtenerVouchersDeMiembro = async (token, miembro) => {
    try {
      // Agregamos un timestamp para evitar el caché del navegador y asegurar datos frescos
      const resp = await fetch(`${BASE_URL}/pagos/vouchers/${miembro.matricula}?t=${Date.now()}`, {
        headers: {
          "Authorization": `Bearer ${token}`
        },
        cache: 'no-cache'
      });

      if (resp.ok) {
        const vouchersList = await resp.json();
        return vouchersList.map(v => ({
          id: `${miembro.matricula}-${v.numeroQuincena}`,
          nombre: miembro.nombreCompleto,
          puesto: miembro.rolNombre || miembro.puesto || "Miembro",
          matricula: miembro.matricula,
          voucher: v.pagoId || `Q${v.numeroQuincena}`,
          monto: v.montoEsperado,
          estado: v.estado,
          fecha: v.fechaFin,
          numeroQuincena: v.numeroQuincena
        }));
      }
      return [];
    } catch (e) {
      console.error(`Error cargando vouchers de ${miembro.matricula}:`, e);
      return [];
    }
  };


  useEffect(() => {
    cargarTodo();
  }, []);

  const cargarTodo = async () => {
    setCargando(true);
    setError(null);

    const token = localStorage.getItem("token");
    const matriculaLider = localStorage.getItem("matricula");

    if (!token || !matriculaLider) {
      setError("Sesión no válida. Por favor, inicia sesión de nuevo.");
      setCargando(false);
      return;
    }

    try {
      const idProyecto = await obtenerProyecto(token);
      setProyectoId(idProyecto);

      const miembros = await obtenerMiembrosProyecto(token, matriculaLider);

      const arraysDeNominas = await Promise.all(
        miembros.map(miembro => obtenerVouchersDeMiembro(token, miembro))
      );

      setNominas(arraysDeNominas.flat());

    } catch (err) {
      console.error("Error general en Nóminas:", err);
      setError(err.message);
    } finally {
      setCargando(false);
    }
  };

  /**
   * @param {string} matricula 
   * @param {number} monto
   */
  const handlePay = async (matricula, monto) => {
    const token = localStorage.getItem("token");

    if (!proyectoId) {
      alert("No se puede registrar el pago: ID de proyecto no encontrado.");
      return;
    }

    const pagoDTO = {
      proyectoId: proyectoId,
      matriculaUsuario: matricula,
      monto: monto,
      fecha: new Date().toISOString().split('T')[0]
    };

    try {
      const response = await fetch(`${BASE_URL}/pagos/registrar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(pagoDTO)
      });

      if (response.ok) {
        alert("¡Pago registrado con éxito!");
        await cargarTodo();
      } else {
        const errorData = await response.json();
        alert(`Error al registrar pago: ${errorData.error || "Ocurrió un problema"}`);
      }
    } catch (error) {
      console.error("Error en la petición de pago:", error);
      alert("Error de conexión al intentar registrar el pago.");
    }
  };

  const nominasFiltradas = nominas.filter(n => {
    const coincideBusqueda = n.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
      n.puesto.toLowerCase().includes(busqueda.toLowerCase());

    if (filtro === "Todos") return coincideBusqueda;
    if (filtro === "Pagados") return coincideBusqueda && n.estado === "PAGADO";
    if (filtro === "Pendientes") return coincideBusqueda && n.estado === "PENDIENTE";
    return coincideBusqueda;
  });


  /*if (cargando) return (
    <div className="loading-container">
      <Loader2 className="animate-spin" size={48} />
      <p>Cargando información de nóminas...</p>
    </div>
  );

  if (error) return <div className="error-container">{error}</div>;*/

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
            <NominaCard
              key={nomina.id}
              nomina={nomina}
              onPay={() => handlePay(nomina.matricula, nomina.monto)}
            />
          ))
        ) : (
          <div className="no-results">No se encontraron nóminas para mostrar.</div>
        )}
      </div>
    </div>
  );
};

export default Nominas;