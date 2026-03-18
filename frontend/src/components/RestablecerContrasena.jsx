import React, { useState } from "react";
import "./RestablecerContrasena.css";

const RestablecerContrasena = () => {
  const [matricula, setMatricula] = useState("");
  const [codigoOtp, setCodigoOtp] = useState("");
  const [nuevaContrasena, setNuevaContrasena] = useState("");
  const [mensaje, setMensaje] = useState("");

  const enviarFormulario = async (e) => {
    e.preventDefault();
    try {
      const respuesta = await fetch("http://localhost:8080/auth/reset-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ matricula, token: codigoOtp, nuevaContrasena }),
      });
      const datos = await respuesta.json();
      setMensaje(datos.mensaje || datos.error);
    } catch {
      setMensaje("Error de conexión con el servidor.");
    }
  };

  return (
    <div className="restablecer-container">
      <div className="restablecer-box">
        <h2>Restablecer Contraseña</h2>
        <form onSubmit={enviarFormulario}>
          <label>Matrícula</label>
          <input
            value={matricula}
            onChange={(e) => setMatricula(e.target.value)}
            required
          />

          <label>Código OTP</label>
          <input
            value={codigoOtp}
            onChange={(e) => setCodigoOtp(e.target.value)}
            required
          />

          <label>Nueva Contraseña</label>
          <input
            type="password"
            value={nuevaContrasena}
            onChange={(e) => setNuevaContrasena(e.target.value)}
            required
          />

          <button type="submit">Actualizar Contraseña</button>
        </form>
        {mensaje && <p>{mensaje}</p>}
      </div>
    </div>
  );
};

export default RestablecerContrasena;