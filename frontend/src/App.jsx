<<<<<<< HEAD
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
=======
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
>>>>>>> 3.3.1_front_proyectos
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
<<<<<<< HEAD
        {/* Redirigir la raíz a login */}
        <Route path="/" element={<Navigate to="/login" />} />

        <Route path="/login" element={<Login />} />
=======
        
        <Route path="/" element={<Login />} />
>>>>>>> 3.3.1_front_proyectos

        {/* Recuperación de contraseña rutas temporales */}
        <Route path="/recuperar-contraseña" element={<RecuperarContrasena />} />
        <Route path="/restablecer-contraseña" element={<RestablecerContrasena />} />

<<<<<<< HEAD
        <Route path="/lideres" element={
          <AdminRoute>
            <DashLideres />
          </AdminRoute>
        } />

        <Route path="/dashboard" element={<Navigate to="/login" />} />
        
        <Route path="/dashboard-lider" element={
          <LiderRoute>
            <DashboardLider />
          </LiderRoute>
        } />
=======
        
>>>>>>> 3.3.1_front_proyectos
      </Routes>
    </Router>
  );
}

export default App;
