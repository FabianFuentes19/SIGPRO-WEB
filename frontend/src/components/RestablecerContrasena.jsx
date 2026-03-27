import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Eye, EyeOff, Loader2, ArrowLeft } from "lucide-react";
import "./RestablecerContrasena.css";

const BASE_URL = "http://localhost:8080";

function RestablecerContraseña() {
  const location = useLocation();
  const navigate = useNavigate();
  const matricula = location.state?.matricula || "";

  const [codigo, setCodigo] = useState(Array(6).fill(""));
  const [nuevaContrasena, setNuevaContrasena] = useState("");
  const [confirmarContrasena, setConfirmarContrasena] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [mensaje, setMensaje] = useState("");
  const [cargando, setCargando] = useState(false);
  const [paso, setPaso] = useState(1); // 1: Código, 2: Nueva Contraseña

  const handleChangeCodigo = (value, index) => {
    if (/^\d?$/.test(value)) {
      const nuevoCodigo = [...codigo];
      nuevoCodigo[index] = value;
      setCodigo(nuevoCodigo);

      if (value && index < 5) {
        const nextInput = document.getElementById(`input-${index + 1}`);
        if (nextInput) nextInput.focus();
      }
    }
  };

  const irAPaso2 = (e) => {
    e.preventDefault();
    const codigoCompleto = codigo.join("");
    if (codigoCompleto.length < 6) {
      setMensaje("Debes ingresar los 6 dígitos del código enviado.");
      return;
    }
    setMensaje("");
    setPaso(2);
  };

  const handleReset = async (e) => {
    e.preventDefault();
    
    if (nuevaContrasena !== confirmarContrasena) {
      setMensaje("Las contraseñas no coinciden.");
      return;
    }

    // Validación mínima coincidente con el backend
    const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;
    if (!PASSWORD_PATTERN.test(nuevaContrasena)) {
      setMensaje("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
      return;
    }

    const tokenFinal = codigo.join("");
    const matriculaFinal = matricula.trim();

    console.log("Iniciando restablecimiento para:", matriculaFinal);
    console.log("Código ingresado:", tokenFinal);
    console.log("Nueva contraseña (longitud):", nuevaContrasena.length);

    setCargando(true);
    setMensaje("");

    try {
      const response = await fetch(`${BASE_URL}/auth/reset-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          matricula: matriculaFinal,
          token: tokenFinal,
          nuevaContrasena: nuevaContrasena
        }),
      });

      let data;
      try {
        data = await response.json();
      } catch (e) {
        data = {};
      }

      if (response.ok) {
        setMensaje("✓ ¡Contraseña restablecida correctamente! Redirigiendo al login...");
        setTimeout(() => {
          navigate("/login");
        }, 2500);
      } else {
        setMensaje(data.error || "Código inválido o error al procesar.");
      }
    } catch (error) {
      console.error("Error en reset:", error);
      setMensaje("Error de conexión con el servidor.");
    } finally {
      setCargando(false);
    }
  };

  if (!matricula) {
    return (
      <div className="codigo-container">
        <h2>Acceso no autorizado</h2>
        <p>Por favor, inicia el proceso de recuperación desde la página de login.</p>
        <button className="btn-verificar" onClick={() => navigate("/login")}>VOLVER AL LOGIN</button>
      </div>
    );
  }

  return (
    <div className="codigo-container">
      {paso === 1 ? (
        <>
          <h2>Verificar Código</h2>
          <p>Ingresa el código de 6 dígitos enviado a tu correo para la matrícula <strong>{matricula}</strong>.</p>
          <form onSubmit={irAPaso2} className="codigo-form">
            <div className="codigo-inputs">
              {codigo.map((valor, index) => (
                <input
                  key={index}
                  id={`input-${index}`}
                  type="text"
                  maxLength="1"
                  value={valor}
                  autoComplete="off"
                  onChange={(e) => handleChangeCodigo(e.target.value, index)}
                  className="codigo-input"
                />
              ))}
            </div>
            <button type="submit" className="btn-verificar">CONTINUAR</button>
          </form>
        </>
      ) : (
        <>
          <h2>Nueva Contraseña</h2>
          <p>Establece tu nueva contraseña de acceso.</p>
          <form onSubmit={handleReset} className="reset-password-form">
            <div className="input-group-reset">
              <label>Nueva Contraseña *</label>
              <div className="password-input-wrapper">
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="Mínimo 8 caracteres"
                  value={nuevaContrasena}
                  onChange={(e) => setNuevaContrasena(e.target.value)}
                  required
                />
                <button type="button" className="btn-toggle-eye" onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            <div className="input-group-reset">
              <label>Confirmar Contraseña *</label>
              <div className="password-input-wrapper">
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="Repite la contraseña"
                  value={confirmarContrasena}
                  onChange={(e) => setConfirmarContrasena(e.target.value)}
                  required
                />
              </div>
            </div>

            <button type="submit" className="btn-verificar" disabled={cargando}>
                {cargando ? <Loader2 className="animate-spin" /> : "RESTABLECER CONTRASEÑA"}
            </button>
            <button type="button" className="btn-back" onClick={() => setPaso(1)}>
               <ArrowLeft size={16} /> Volver al código
            </button>
          </form>
        </>
      )}

      {mensaje && (
        <p className={mensaje.includes("✓") ? "mensaje-exito" : "mensaje-error"}>
          {mensaje}
        </p>
      )}

      <div className="codigo-opciones">
        <a href="/login">← Cancelar y volver al inicio</a>
      </div>
    </div>
  );
}

export default RestablecerContraseña;
