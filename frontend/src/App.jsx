import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Proyectos from "./components/Proyectos";
import AdminRoute from "./components/AdminRoute";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />

        {/* Ruta protegida */}
        <Route path="/proyectos" element={
          <AdminRoute>
            <Proyectos />
          </AdminRoute>
        } />

        <Route path="/dashboard" element={<h2>Dashboard</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
