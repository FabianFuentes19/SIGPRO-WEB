import React from "react";
import { Navigate } from "react-router-dom";

/**
 * Ruta protegida para cualquier usuario autenticado (ADMIN, LIDER, MIEMBRO).
 * Solo verifica que exista un token y un rol en localStorage.
 */
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");
  const rol = localStorage.getItem("rol");

  return token && rol ? children : <Navigate to="/login" />;
};

export default ProtectedRoute;
