import React from "react";
import { Navigate } from "react-router-dom";

const AdminRoute = ({ children }) => {
  const rol = localStorage.getItem("rol")?.toUpperCase();
  const esAdmin = rol === "ADMINISTRADOR" || rol === "ADMIN";
  return esAdmin ? children : <Navigate to="/login" />;
};

export default AdminRoute;