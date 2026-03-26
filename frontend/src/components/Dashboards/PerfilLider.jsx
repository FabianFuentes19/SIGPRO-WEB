import React, { useState } from "react";
import { 
  LayoutGrid, Users, Wallet, Info, History, LogOut, UserCircle 
} from "lucide-react";
import "./PerfilLider.css";

const PerfilLider = () => {
  
  const [usuario] = useState({
    nombreCompleto: "Tania Sanchez Reyes",
    matricula: "20243ds026",
    cuatrimestre: "7mo",
    carrera: "Ingeniería en Tecnologías de la Información",
    puesto: "Programador",
  });

  const [listaPagos] = useState([
    {
      concepto: "Nómina Quincenal",
      matricula: "20243ds026",
      fecha: "15 Oct 2023",
      monto: "$1,200.00",
      estado: "PAGADO",
    },
  ]);

  return (
    <div className="contenedor-perfil">
      {/*
      <header className="barra-superior">
        <div className="marca-panel">Panel Líder</div>
        <div className="titulo-seccion">Perfil</div>
        <div className="usuario-icono">
          <UserCircle size={32} strokeWidth={1.5} />
        </div>
      </header>
      */}

      <div className="cuerpo-principal">
       

        {}
        <main className="area-contenido">
          {}
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
                  <p>{usuario.cuatrimestre}</p>
                </div>
                <div className="dato-item">
                  <label>CARRERA</label>
                  <p>{usuario.carrera}</p>
                </div>
                <div className="dato-item">
                  <label>PUESTO ACTUAL</label>
                  <span className="etiqueta-puesto">{usuario.puesto}</span>
                </div>
              </div>
            </div>
          </section>

          {}
          <section className="seccion-info">
            <div className="encabezado-bloque">
              <History size={18} className="color-teal" /> 
              <span>Historial de Pagos</span>
            </div>
            <div className="tarjeta-datos">
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
                        <Wallet size={16} /> {pago.concepto}
                      </td>
                      <td>{pago.matricula}</td>
                      <td className="color-gris">{pago.fecha}</td>
                      <td className="texto-negrita">{pago.monto}</td>
                      <td>
                        <span className="estado-pagado">{pago.estado}</span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </main>
      </div>
    </div>
  );
};

export default PerfilLider;