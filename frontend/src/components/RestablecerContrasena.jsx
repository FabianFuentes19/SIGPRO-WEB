import React, { useState } from "react";
import "./RestablecerContrasena.css";


function RestablecerContraseña() {
  const [codigo, setCodigo] = useState(Array(6).fill(""));
  const [mensaje, setMensaje] = useState("");

  const handleChange = (value, index) => {
    if (/^\d?$/.test(value)) { // solo números
      const nuevoCodigo = [...codigo];
      nuevoCodigo[index] = value;
      setCodigo(nuevoCodigo);

      // mover al siguiente input automáticamente
      if (value && index < 5) {
        document.getElementById(`input-${index + 1}`).focus();
      }
    }
  };

  const verificarCodigo = (e) => {
    e.preventDefault();
    const codigoCompleto = codigo.join("");

    if (codigoCompleto.length < 6) {
      setMensaje("Debes ingresar los 6 dígitos");
      return;
    }

    // Aquí iría la llamada al backend
    if (codigoCompleto === "123456") {
      setMensaje("Código verificado correctamente");
    } else {
      setMensaje("Código inválido o expirado");
    }
  };

  return (
    <div className="codigo-container">
      <h2>Verificar Código</h2>
      <p>Ingresa el código de 6 dígitos que enviamos a tu correo electrónico para continuar con el restablecimiento de tu contraseña.</p>

      <form onSubmit={verificarCodigo} className="codigo-form">
        <div className="codigo-inputs">
          {codigo.map((valor, index) => (
            <input
              key={index}
              id={`input-${index}`}
              type="text"
              maxLength="1"
              value={valor}
              onChange={(e) => handleChange(e.target.value, index)}
              className="codigo-input"
            />
          ))}
        </div>

        <button type="submit" className="btn-verificar">
          VERIFICAR Y CONTINUAR
        </button>
      </form>

      {mensaje && (
        <p className={mensaje.includes("correctamente") ? "mensaje-exito" : "mensaje-error"}>
          {mensaje}
        </p>
      )}

      <div className="codigo-opciones">
        <a href="#">¿No recibiste el código? Reenviar</a>
        <a href="/login">← Volver al inicio de sesión</a>
      </div>

      <p className="codigo-info">
        ℹPor razones de seguridad, este código expirará en 10 minutos.  
        Revisa tu carpeta de spam si no lo visualizas.
      </p>
    </div>
  );
}

export default RestablecerContraseña;
