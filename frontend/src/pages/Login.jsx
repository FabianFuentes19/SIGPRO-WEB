import React, { useState } from "react";
import logoUtez from "../assets/LOGO_UTEZ.png";
import '../css/Login.css';
import { useNavigate } from "react-router-dom";


function Login() {
  const [user, setUser] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();


  const submit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8082/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ matricula: user, contrasena: password }),
      });

      if (response.ok) {
        const data = await response.json();
        setMessage("Login exitoso");
        console.log("Token recibido:", data.token);

        localStorage.setItem("token", data.token);
        localStorage.setItem("rol", data.rol);

        if (data.rol === "ADMINISTRADOR") {
          navigate("/proyectos");
        } else {
          navigate("/dashboard");
        }
      } else {
        setMessage("Credenciales inválidas");
      }
    } catch (error) {
      console.error("Error:", error);
      setMessage("Error de conexión con el servidor");
    }
  };

  const handleForgotPassword = async () => {
    try {
      const response = await fetch("http://localhost:8082/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ matricula: user }),
      });

      if (response.ok) {
        const data = await response.json();
        setMessage(data.mensaje || "Se envió un correo para restablecer tu contraseña");
      } else {
        setMessage("No se pudo procesar la solicitud");
      }
    } catch (error) {
      console.error("Error:", error);
      setMessage("Error de conexión con el servidor");
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
