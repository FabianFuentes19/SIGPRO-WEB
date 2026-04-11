import React, { useState, useEffect } from "react";
import { 
  LayoutGrid, Users, Wallet, Info, History, LogOut, UserCircle 
} from "lucide-react";
import { formatCurrencyWithSign } from "../../utils/formatters.js";
import "./PerfilLider.css";
import { useNavigate } from "react-router-dom";

const BASE_URL = "http://localhost:8080";

const PerfilLider = () => {
  //Estos son los estados 
  const [usuario, setUsuario] = useState(null);
  const [listaPagos, setListaPagos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const navigate = useNavigate();

  //Carga los datos al iniciar 
  useEffect(() => {
    //Llama este componente cargarPerfil
    const cargarPerfil = async () => {
      try {
        //Obtiene matricula y el Token
        const matricula = localStorage.getItem("matricula");
        const token = localStorage.getItem("token");

        if (!matricula || !token) {
          console.warn("No hay sesión activa");
          setCargando(false);
          return;
        }

        const headers = {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        };

        // 1. Cargar datos del usuario
        const resUser = await fetch(`${BASE_URL}/usuarios/${encodeURIComponent(matricula)}`, { headers });
        if (resUser.ok) {
          const userData = await resUser.json();
          setUsuario(userData);
        } else {
          console.error("No se pudo obtener la información del usuario");
        }

        // 2. Cargar historial de pagos
        const resPagos = await fetch(`${BASE_URL}/pagos/miembro/${encodeURIComponent(matricula)}`, { headers });
        if (resPagos.ok) {
          const pagosData = await resPagos.json();
          // Orden descendente por fecha
          const sorted = pagosData.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
          setListaPagos(sorted);
        } else {
            console.error("No se pudo obtener el historial de pagos");
        }

      } catch (error) {
        console.error("Error al cargar el perfil:", error);
      } finally {
        setCargando(false);
      }
    };

    cargarPerfil();
  }, []);

  //Este lo puse para que convierta los numeros a un formato en este caso moneda MXN
  const formatCurrency = (amount) => formatCurrencyWithSign(amount);

  //Este lo puse para que convierta fechas a un formato legible para los usuarios
  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    const [year, month, day] = dateStr.split('-');
    const months = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
    return `${day} ${months[parseInt(month) - 1]} ${year}`;
  };

  //Para que se muestre que se esta cargando la infomacion
  if (cargando) {
      return (
          <div className="contenedor-perfil" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
              <p>Cargando información del perfil...</p>
          </div>
      );
  }

  //Si no hay un usuario muestra este mensaje
  if (!usuario) {
      return (
          <div className="contenedor-perfil" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
              <p>No se pudo cargar la información del perfil.</p>
          </div>
      );
  }

  return (
    <div className="contenedor-perfil">
      <div className="cuerpo-principal">
        <main className="area-contenido">
          <section className="seccion-info">
            <div className="encabezado-bloque">
              <Info size={18} className="color-teal" /> 
              <span>Información Personal</span>
            </div>
            <div className="tarjeta-datos">
              <div className="cuadricula-info">
                <div className="dato-item ancho-completo">
                  <label>NOMBRE COMPLETO</label>
                  <p className="texto-resaltado">{usuario.nombreCompleto}</p>
                </div>
                <div className="dato-item">
                  <label>MATRICULA</label>
                  <p>{usuario.matricula}</p>
                </div>
                <div className="dato-item">
                  <label>CUATRIMESTRE</label>
                  <p>{usuario.cuatrimestre ? `${usuario.cuatrimestre} Cuatrimestre` : 'N/A'}</p>
                </div>
                <div className="dato-item">
                  <label>CARRERA</label>
                  <p>{usuario.carrera || 'N/A'}</p>
                </div>
                <div className="dato-item">
                  <label>PUESTO ACTUAL</label>
                  <span className="etiqueta-puesto">{usuario.puesto || usuario.rolNombre || 'Sin puesto'}</span>
                </div>
              </div>
            </div>
          </section>

          <section className="seccion-info">
            <div className="encabezado-bloque">
              <History size={18} className="color-teal" /> 
              <span>Historial de Pagos</span>
            </div>
            <div className="tarjeta-datos">
              {listaPagos.length > 0 ? (
                  <table className="tabla-historial">
                    <thead>
                      <tr>
                        <th>CONCEPTO</th>
                        <th>MATRICULA</th>
                        <th>FECHA</th>
                        <th>MONTO</th>
                        <th>ESTADO</th>
                      </tr>
                    </thead>
                    <tbody>
                      {listaPagos.map((pago, indice) => (
                        <tr key={indice}>
                          <td className="celda-concepto">
                            <Wallet size={16} /> {pago.concepto || "Nómina Quincenal"}
                          </td>
                          <td>{usuario.matricula}</td>
                          <td className="color-gris">{formatDate(pago.fecha)}</td>
                          <td className="texto-negrita">{formatCurrency(pago.monto)}</td>
                          <td>
                            <span className={pago.estado === "PAGADO" ? "estado-pagado" : "estado-pendiente"}>
                                {pago.estado}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
              ) : (
                  <p style={{ textAlign: 'center', color: '#666', padding: '20px 0' }}>No hay historial de pagos registrado aún.</p>
              )}
            </div>
          </section>

                {/*Agregue esto para el cambio de contraseña*/}
                <section className="seccion-info">
              <div className="encabezado-bloque">
                <UserCircle size={18} className="color-teal" /> 
                <span>Seguridad</span>
              </div>
              <div className="tarjeta-datos">
                <p className="texto-info">
                  Actualiza tu contraseña en cualquier momento. 
                  Mantener tu cuenta protegida y bajo tu control.
                </p>
                <center>
                <button 
                  className="btn-cambiar-contrasena"
                  onClick={() => navigate("/PerfilLider/cambiar-contrasena")}

                >
                  Cambiar Contraseña
                </button>
                </center>
              </div>
            </section>



        </main>
      </div>
    </div>
  );
};

export default PerfilLider;