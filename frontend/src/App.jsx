import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import AdminRoute from "./components/AdminRoute";
import DashProyectos from "./pages/DashProyectos";
import DashLideres from "./pages/DashLideres";
import DashboardLider from "./components/Dashboards/DashboardLider";
import LiderRoute from "./components/LiderRoute";

import RecuperarContrasena from "./components/RecuperarContrasena";
import RestablecerContrasena from "./components/RestablecerContrasena";

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirigir la raíz a login */}
        <Route path="/" element={<Navigate to="/login" />} />

        <Route path="/login" element={<Login />} />

        {/* Recuperación de contraseña rutas temporales */}
        <Route path="/recuperar-contraseña" element={<RecuperarContrasena />} />
        <Route path="/restablecer-contraseña" element={<RestablecerContrasena />} />

        <Route path="/proyectos" element={
          <AdminRoute>
            <DashProyectos />
          </AdminRoute>
        } />

        <Route path="/lideres" element={
          <AdminRoute>
            <DashLideres />
          </AdminRoute>
        } />

        <Route path="/dashboard" element={<Navigate to="/dashboard-lider" />} />
        
        <Route path="/dashboard-lider" element={
          <LiderRoute>
            <DashboardLider />
          </LiderRoute>
        } />
      </Routes>
    </Router>
  );
}

export default App;
