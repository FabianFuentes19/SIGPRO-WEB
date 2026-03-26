import React, { useState } from "react";
import { User } from "lucide-react"; 
import "./RecuperarContrasena.css";
import { useNavigate } from "react-router-dom";


function RecuperarContrasena() {
  const [matricula, setMatricula] = useState("");
  const [mensaje, setMensaje] = useState("");
  const navigate = useNavigate();


  const enviarFormulario = async (e) => {
    e.preventDefault();

    if (!matricula.trim()) {
      setMensaje("Debes ingresar tu matrícula");
      return;
    }

    try {
      const respuesta = await fetch("http://localhost:8080/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ matricula }),
      });

      const data = await respuesta.json().catch(() => null);
      console.log("Respuesta backend:", respuesta.status, data);

      if (respuesta.ok) {
        setMensaje("Se envió un correo para restablecer tu contraseña");
        navigate("/restablecer-contraseña");
      } else {
        setMensaje("No se pudo procesar la solicitud");
      }
    } catch (error) {
      setMensaje("Error de conexión con el servidor");
    }
  };

  return (
    <div className="recover-page">
      <header className="recover-header">
        <div className="header-content">
          <span className="brand-text">
            Sistema de Gestión de Proyectos | <strong>SIGPRO</strong>
          </span>
        </div>
      </header>

      <div className="recover-container">
        <div className="recover-box">
          <div className="recover-box-header">
            <h3>Recuperar Contraseña</h3>
          </div>
          
          <div className="recover-box-body">
            <p className="instruction-text">
              Ingrese su usuario para restablecer su contraseña
            </p>

            <form onSubmit={enviarFormulario}>
              <div className="input-group-custom">
                <label>Usuario *</label>
                <div className="input-with-icon">
                  <User size={18} className="icon-input" />
                  <input
                    type="text"
                    placeholder="Ej. 20243ds067"
                    value={matricula}
                    onChange={(e) => setMatricula(e.target.value)}
                  />
                </div>
              </div>

              <div className="button-container">
                <button type="submit" className="btn-recover">
                  Recuperar Contraseña
                </button>
              </div>
              
              {mensaje && <p className="status-message">{mensaje}</p>}
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RecuperarContrasena;