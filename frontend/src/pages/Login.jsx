import React, { useState } from "react";
import logoUtez from "../assets/LOGO_UTEZ.png";
import "../css/Login.css";
import { useNavigate, useLocation } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
function Login() {
  const [user, setUser] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [mostrarPassword, setMostrarPassword] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // esto es para la validación de los campos
  const [touchedUser, setTouchedUser] = useState(false);
  const [touchedPassword, setTouchedPassword] = useState(false);
  const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

  // Detectar si venimos de una sesión expirada
  React.useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get("sesionExpirada")) {
      setMessage("Tu sesión ha expirado por seguridad. Por favor, ingresa de nuevo.");
    }
  }, [location]);

  const submit = async (e) => {
    e.preventDefault();

    if (user.trim() === "" || password.trim() === "") {
    setMessage("Los campos matrícula y contraseña no pueden estar vacíos");
    return;
  }

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ matricula: user, contrasena: password }),
      });

      if (response.ok) {
        const data = await response.json();
        const role = (data.rol || "").toUpperCase();
        console.log("Login exitoso. Rol recibido:", role);

        localStorage.setItem("token", data.token);
        localStorage.setItem("rol", role);
        localStorage.setItem("matricula", user);

        if (role === "ADMINISTRADOR" || role === "ADMIN") {
          console.log("Redirigiendo a proyectos (Admin)");
          navigate("/lideres");
        } else {
          console.log("Redirigiendo a dashboard (Lider/Miembro)");
          navigate("/dashboard");
        }
      } else {
        const errorData = await response.json().catch(() => ({}));
        setMessage(errorData.error || "Credenciales inválidas");
      }
    } catch (error) {
      console.error("Error:", error);
      setMessage("Error de conexión con el servidor");
    }
  };

 const handleForgotPassword = (e) => {
  e.preventDefault();
  navigate("/recuperar-contraseña"); 
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
                className={`form-control ${
                  !touchedUser ? "" : user.trim() === "" ? "invalido" : "valido"
                }`}
                placeholder="Ej. 20243ds067"
                value={user}
                onChange={(e) => setUser(e.target.value)}
                onBlur={() => setTouchedUser(true)}
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Contraseña *</label>
            <div className="password-container">
              <input
                    type={mostrarPassword ? "text" : "password"}
                    className={`form-control ${
                      !touchedPassword ? "" : PASSWORD_PATTERN.test(password) ? "valido" : "invalido"
                    }`}
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onBlur={() => setTouchedPassword(true)}
              />
              <div 
                className="password-toggle-icon" 
                onClick={() => setMostrarPassword(!mostrarPassword)}
              >
                {mostrarPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </div>
            </div>
          </div>

          <div className="forgot-password">
  <a href="#" onClick={handleForgotPassword}>¿Olvidaste tu contraseña?</a>
</div>


          <button type="submit" className="btn btn-primary w-100">
            Iniciar Sesión
          </button>
          {message && (
            <p className={`mt-3 ${message.includes("expirado") ? "text-warning bg-dark p-2 rounded text-center" : "text-danger"}`}>
              {message}
            </p>
          )}
        </form>
      </div>
    </div>
  );
}

export default Login;