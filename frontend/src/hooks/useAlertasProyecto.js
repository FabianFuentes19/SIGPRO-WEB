import { useState, useEffect } from 'react';

/**
 * Hook para consultar las alertas de un proyecto
 * @param {number} proyectoId - ID del proyecto
 * @param {number} intervalo - Intervalo de polling en ms (default 5 minutos)
 * @returns {object} { alerta, cargando, error }
 */
export function useAlertasProyecto(proyectoId, intervalo = 300000) {
  const [alerta, setAlerta] = useState(null);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState(null);

  const consultarAlerta = async () => {
    if (!proyectoId) return;

    setCargando(true);
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/proyectos/${proyectoId}/alertas`, {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        // Si tiene estrutura de AlertaDTO (con tipo y mensaje), es una alerta
        if (data.tipo) {
          setAlerta(data);
        } else {
          // Sin alerta
          setAlerta(null);
        }
        setError(null);
      } else {
        setError("Error al consultar alertas");
      }
    } catch (err) {
      console.error("Error consultando alertas:", err);
      setError(err.message);
    } finally {
      setCargando(false);
    }
  };

  // Consultar alerta al montar y cuando cambia proyectoId
  useEffect(() => {
    consultarAlerta();

    // Polling cada X ms
    const timer = setInterval(consultarAlerta, intervalo);

    return () => clearInterval(timer);
  }, [proyectoId, intervalo]);

  return { alerta, cargando, error, consultarAlerta };
}
