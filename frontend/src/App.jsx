import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import AdminRoute from "./components/AdminRoute";
import DashProyectos from "./pages/DashProyectos";
import DashLideres from "./pages/DashLideres";
import DashboardLider from "./components/Dashboards/DashboardLider";
import LiderRoute from "./components/LiderRoute";

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirigir la raíz a login */}
        <Route path="/" element={<Navigate to="/login" />} />

        <Route path="/login" element={<Login />} />

        {/* Ruta protegida */}
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

        <Route path="/dashboard" element={<Navigate to="/login" />} />
        
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
