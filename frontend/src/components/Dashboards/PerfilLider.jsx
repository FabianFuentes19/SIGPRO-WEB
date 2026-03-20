import React, { useEffect, useState } from "react";
import "./PerfilLider.css";

const PerfilLider = ({ matricula, token }) => {
  const [usuario, setUsuario] = useState(null);
  const [pagos, setPagos] = useState([]);

  useEffect(() => {
    // Información personal del líder
    fetch(`http://localhost:8080/usuarios/${matricula}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((data) => setUsuario(data))
      .catch((err) => console.error(err));

    // Historial de pagos (mock por ahora)
    setPagos([
      {
        concepto: "Nómina Quincenal",
        matricula: matricula,
        fecha: "15 Oct 2023",
        monto: "$1,200.00",
        estado: "Pagado",
      },
      {
        concepto: "Nómina Quincenal",
        matricula: matricula,
        fecha: "30 Oct 2023",
        monto: "$1,200.00",
        estado: "Pagado",
      },
    ]);
  }, [matricula, token]);

  if (!usuario) return <p>Cargando perfil...</p>;

  return (
    <div className="perfil-container">
      <h2>Perfil del Líder</h2>

      <div className="perfil-section">
        <h3>Información Personal</h3>
        <div className="perfil-info">
          <p><strong>Nombre Completo:</strong> {usuario.nombreCompleto}</p>
          <p><strong>Matrícula:</strong> {usuario.matricula}</p>
          <p><strong>Cuatrimestre:</strong> {usuario.cuatrimestre}</p>
          <p><strong>Carrera:</strong> {usuario.carrera}</p>
          <p><strong>Puesto Actual:</strong> {usuario.puesto}</p>
          <p><strong>Salario Quincenal:</strong> ${usuario.salarioQuincenal}</p>
          <p><strong>Estado:</strong> {usuario.estado}</p>
        </div>
      </div>

      <div className="perfil-section">
        <h3>Historial de Pagos</h3>
        <table className="perfil-table">
          <thead>
            <tr>
              <th>Concepto</th>
              <th>Matrícula</th>
              <th>Fecha</th>
              <th>Monto</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {pagos.map((p, index) => (
              <tr key={index}>
                <td>{p.concepto}</td>
                <td>{p.matricula}</td>
                <td>{p.fecha}</td>
                <td>{p.monto}</td>
                <td>{p.estado}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default PerfilLider;
