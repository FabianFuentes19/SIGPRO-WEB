import React, { useState } from "react";
import logoUtez from "../assets/LOGO_UTEZ.png";
import '../css/Login.css';
import { useNavigate } from "react-router-dom";
import { login, forgotPassword } from "../services/api";

function Login() {
  const [user, setUser] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    try {
      const data = await login(user, password);
      setMessage("Login exitoso");
      const rolRecibido = (data.rol || "").toUpperCase();
      console.log("Rol estandarizado:", rolRecibido);

      localStorage.setItem("token", data.token);
      localStorage.setItem("rol", rolRecibido);
      if (data.matricula) localStorage.setItem("matricula", data.matricula);

      if (rolRecibido === "ADMINISTRADOR" || rolRecibido === "ADMIN") {
        navigate("/lideres");
      } else if (rolRecibido === "LIDER") {
        navigate("/dashboard-lider");
      } else {
        navigate("/dashboard");
      }
    } catch (error) {
      console.error("Error:", error);
      setMessage(error.message || "Error de conexión con el servidor");
    }
  };

  const handleForgotPassword = async () => {
    try {
      const data = await forgotPassword(user);
      setMessage(data.mensaje || "Se envió un correo para restablecer tu contraseña");
    } catch (error) {
      console.error("Error:", error);
      setMessage(error.message || "Error de conexión con el servidor");
    }
  };

  return (
    <div className="login-container">
      {/* Columna izquierda */}
      <div className="login-left">
        <img src={logoUtez} alt="Logo UTEZ" />
        <p>Territorio de calidad</p>
      </div>

      {/* Columna derecha */}
      <div className="login-right">
        <h2>Bienvenido</h2>
        <p>Inicia sesión para acceder a tu cuenta.</p>
        <form className="form-box" onSubmit={submit}>
          <div className="mb-3">
            <label className="form-label">Matrícula *</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej. 20243ds067"
              value={user}
              onChange={(e) => setUser(e.target.value)}
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Contraseña *</label>
            <input
              type="password"
              className="form-control"
              placeholder="Contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <div className="forgot-password">
            <a href="#" onClick={handleForgotPassword}>¿Olvidaste tu contraseña?</a>
          </div>

          <button type="submit" className="btn btn-primary w-100">
            Iniciar Sesión
          </button>
          <p className="mt-3">{message}</p>
        </form>
      </div>
    </div>
  );
}

export default Login;
