import React, { useState } from "react";
import "../css/CambioContrasena.css";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import ModalCerrarSesion from '../components/Usuarios/ModalCerrarSesion';



import { Eye, EyeOff } from "lucide-react";

const BASE_URL = "http://localhost:8080"; 

const CambioContrasena = () => {
  const [actual, setActual] = useState("");
  const [nueva, setNueva] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [mensaje, setMensaje] = useState(null);
  const navigate = useNavigate();

    const iraperfil = () => {
    navigate("/perfil"); 
    };

    const iralinicio = () => {
    localStorage.clear(); // borra token, matrícula, rol
    navigate("/login");   // redirige al login
    };



  // Estados para mostrar/ocultar contraseñas
  const [showActual, setShowActual] = useState(false);
  const [showNueva, setShowNueva] = useState(false);
  const [showConfirmar, setShowConfirmar] = useState(false);

  const [mostrarModal, setMostrarModal] = useState(false);

  // Función para validar reglas de contraseña
  const validarContrasena = (password) => {
    const tieneLongitud = password.length >= 8;
    const tieneMayuscula = /[A-Z]/.test(password);
    const tieneMinuscula = /[a-z]/.test(password);
    const tieneNumero = /[0-9]/.test(password);
    const tieneSimbolo = /[^A-Za-z0-9]/.test(password);

    return (
      tieneLongitud &&
      tieneMayuscula &&
      tieneMinuscula &&
      tieneNumero &&
      tieneSimbolo
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (nueva !== confirmar) {
      setMensaje("La nueva contraseña y la confirmación no coinciden.");
      return;
    }

    if (!validarContrasena(nueva)) {
      setMensaje("La nueva contraseña no cumple con los requisitos de seguridad.");
      return;
    }

    try {
      const matricula = localStorage.getItem("matricula"); 
      const token = localStorage.getItem("token");     
      const res = await fetch(`${BASE_URL}/usuarios/${matricula}/cambiar-contrasena`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ actual, nueva })
      });

      if (res.ok) {
        setMensaje("Contraseña actualizada correctamente.");
        setActual(""); setNueva(""); setConfirmar("");
      } else {
        const errorData = await res.json();
        setMensaje(errorData.error || "Error al cambiar la contraseña.");
      }
    } catch (error) {
      setMensaje("Error de conexión con el servidor.");
    }
  };

  return (
    <div className="main-container">
      <header className="navbar">
        <span className="brand">Sistema de Gestión de Proyectos | SIGPRO</span>
       <span className="back-icon" onClick={() => setMostrarModal(true)}>
        <ArrowLeft size={24} />
        </span>


      </header>

      <div className="content">
        <div className="password-card">
          <div className="card-header">
            <span>Cambiar Contraseña</span>
            <span className="reload-icon">↻</span>
          </div>

          <div className="card-body">
            <p className="description">
              Por favor, ingrese su contraseña actual y su nueva contraseña.
            </p>

            <form onSubmit={handleSubmit}>
              {/* Contraseña actual */}
              <div className="form-group">
                <label>CONTRASEÑA ACTUAL *</label>
                <div className="input-box password-container">
                  <span className="icon">🔒</span>
                  <input 
                    type={showActual ? "text" : "password"} 
                    value={actual} 
                    onChange={(e) => setActual(e.target.value)} 
                    required 
                  />
                  <div 
                    className="password-toggle-icon" 
                    onClick={() => setShowActual(!showActual)}
                  >
                    {showActual ? <EyeOff size={20} /> : <Eye size={20} />}
                  </div>
                </div>
              </div>

              {/* Nueva contraseña */}
              <div className="form-group">
                <label>NUEVA CONTRASEÑA *</label>
                <div className="input-box password-container">
                  <span className="icon">🔑</span>
                  <input 
                    type={showNueva ? "text" : "password"} 
                    value={nueva} 
                    onChange={(e) => setNueva(e.target.value)} 
                    required 
                  />
                  <div 
                    className="password-toggle-icon" 
                    onClick={() => setShowNueva(!showNueva)}
                  >
                    {showNueva ? <EyeOff size={20} /> : <Eye size={20} />}
                  </div>
                </div>
                <small 
                  className={`password-note ${validarContrasena(nueva) ? "valida" : "invalida"}`}
                >
                  La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y un símbolo.
                </small>
              </div>

              {/* Confirmar nueva contraseña */}
              <div className="form-group">
                <label>CONFIRMAR NUEVA CONTRASEÑA *</label>
                <div className="input-box password-container">
                  <span className="icon">🛡️</span>
                  <input 
                    type={showConfirmar ? "text" : "password"} 
                    value={confirmar} 
                    onChange={(e) => setConfirmar(e.target.value)} 
                    required 
                  />
                  <div 
                    className="password-toggle-icon" 
                    onClick={() => setShowConfirmar(!showConfirmar)}
                  >
                    {showConfirmar ? <EyeOff size={20} /> : <Eye size={20} />}
                  </div>
                </div>
              </div>

              {/* Botón */}
              <div className="button-area">
                <button 
                  type="submit" 
                  className="btn-save"
                  disabled={!validarContrasena(nueva)} // deshabilita si no cumple
                >
                  Guardar Cambios <span className="btn-lock"></span>
                </button>
              </div>
            </form>

            {mensaje && <p className="mensaje">{mensaje}</p>}
          </div>
        </div>
      </div>

            {mostrarModal && (
            <ModalCerrarSesion 
                alCancelar={() => setMostrarModal(false)} 
                alAceptar={() => {
                localStorage.clear();
                navigate("/login");
                }} 
            />
            )}

    </div>
  );
};

export default CambioContrasena;