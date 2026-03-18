import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import AdminRoute from "./components/AdminRoute";
import DashProyectos from "./components/DashProyectos";

import RecuperarContrasena from "./components/RecuperarContrasena";
import RestablecerContrasena from "./components/RestablecerContrasena";

function App() {
  return (
    <Router>
      <Routes>
        
        <Route path="/" element={<Login />} />

        {/* Recuperación de contraseña rutas temporales */}
        <Route path="/recuperar-contraseña" element={<RecuperarContrasena />} />
        <Route path="/restablecer-contraseña" element={<RestablecerContrasena />} />

        
      </Routes>
    </Router>
  );
}

export default App;
