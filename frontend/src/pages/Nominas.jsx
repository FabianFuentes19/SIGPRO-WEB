import React, { useState, useEffect } from 'react';
import '../css/Nominas.css';
import NominaCard from '../components/Nominas/NominaCard';
import { Search, ChevronDown, Loader2 } from 'lucide-react';
import ModalMensajes from '../components/Usuarios/ModalMensajes';
import { apiFetch } from '../services/api';

const BASE_URL = "http://localhost:8080";

const Nominas = ({ onPaymentSuccess }) => {
  const [busqueda, setBusqueda] = useState("");
  const [filtro, setFiltro] = useState("Todos");
  const [nominas, setNominas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [proyectoId, setProyectoId] = useState(null);
  const [mensajeModal, setMensajeModal] = useState(null);
  

  const obtenerProyecto = async (token) => {
    try {
      const resp = await apiFetch("/proyectos/mi-proyecto/lider");
      if (!resp.ok) return null;

      const text = await resp.text();
      if (!text) return null;

      const data = JSON.parse(text);
      return data && data.id ? data.id : null;
    } catch (e) {
      console.error("Error al obtener proyecto en nóminas:", e);
      return null;
    }
  };


  const obtenerMiembrosProyecto = async (token) => {
    const resp = await apiFetch("/proyectos/mi-equipo");
    if (!resp.ok) throw new Error("No se pudieron cargar los miembros del equipo.");
    return await resp.json();
  };

  const obtenerVouchersDeMiembro = async (token, miembro, matriculaLider) => {
    try {
      const resp = await apiFetch(`/pagos/vouchers/${miembro.matricula}?t=${Date.now()}`, {
        cache: 'no-cache'
      });

      if (resp.ok) {
        const vouchersList = await resp.json();
        return vouchersList.map(v => ({
          id: `${miembro.matricula}-${v.numeroQuincena}`,
          nombre: miembro.nombreCompleto,
          puesto: miembro.puesto,
          matricula: miembro.matricula,
          voucher: v.pagoId || `${v.numeroQuincena}`,
          monto: v.estado === "PAGADO" ? v.montoPagado : v.montoEsperado,
          estado: v.estado,
          fecha: v.estado === "PAGADO" ? v.fechaPagoReal : v.fechaFin,
          numeroQuincena: v.numeroQuincena,
          esPropio: miembro.matricula === matriculaLider
        }));
      } else {
        const errorData = await resp.json();
        console.error(`Error 400 en vouchers para ${miembro.matricula}:`, errorData.error);
        return [];
      }
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

      // se obtiene todos los miembros incluyendo el lider
      const equipo = await obtenerMiembrosProyecto(token);

      const arraysDeNominas = await Promise.all(
        equipo.map(miembro => obtenerVouchersDeMiembro(token, miembro, matriculaLider))
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
  const handlePay = async (matricula, monto, fechaVoucher) => {
    const token = localStorage.getItem("token");

    if (!proyectoId) {
      alert("No se puede registrar el pago: ID de proyecto no encontrado.");
      return;
    }

    const pagoDTO = {
      proyectoId: proyectoId,
      matriculaUsuario: matricula,
      monto: monto,
      fecha: fechaVoucher

    };

    try {
      const response = await apiFetch("/pagos/registrar", {
        method: "POST",
        body: JSON.stringify(pagoDTO)
      });

      if (response.ok) {
          setMensajeModal({
            titulo: "Registro Exitoso",
            mensaje: "Pago registrado correctamente",
            tipo: "exito"
          });
        await cargarTodo();
        // Avisar al dashboard para que actualice el presupuesto del proyecto
        if (typeof onPaymentSuccess === 'function') {
          onPaymentSuccess();
        }
      } else {
        const errorData = await response.json();
        setMensajeModal({
            titulo: "Error al registrar",
            mensaje: errorData.error || "Ocurrió un problema al procesar el pago",
            tipo: "error"
          });
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
              onPay={() => handlePay(nomina.matricula, nomina.monto, nomina.fecha)}
            />
          ))
        ) : (
          <div className="no-results">No se encontraron nóminas para mostrar.</div>
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
    </div>
  );
};

export default Nominas;