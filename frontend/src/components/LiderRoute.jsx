import React from "react";
import { Navigate } from "react-router-dom";

const LiderRoute = ({ children }) => {
  const rol = localStorage.getItem("rol")?.toUpperCase();
  const esLider = rol === "LIDER";
  
  return esLider ? children : <Navigate to="/login" />;
};

export default LiderRoute;
