import React from "react";
import { Navigate } from "react-router-dom";

const AdminRoute = ({ children }) => {
  const rol = localStorage.getItem("rol");
  return rol === "ADMINISTRADOR" ? children : <Navigate to="/login" />;
};

export default AdminRoute;
