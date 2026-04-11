package com.sigpro.service;

import com.sigpro.dto.PagoRequestDTO;
import com.sigpro.dto.PagoResponseDTO;
import com.sigpro.dto.PagoMapper;
import com.sigpro.dto.VoucherDTO;
import com.sigpro.model.Pago;
import com.sigpro.model.Proyecto;
import com.sigpro.model.Usuario;
import com.sigpro.repository.PagoRepository;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

    @Transactional
    public PagoResponseDTO registrarPago(@Valid PagoRequestDTO dto, Authentication auth) {
        validarRol(auth,"ROLE_LIDER");

        Usuario usuario = usuarioRepository.findByMatricula(dto.getMatriculaUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        //valida si hay presupuesto disponible
        if(proyecto.getPresupuesto() == null){
            throw new IllegalArgumentException("El proyecto no tiene presupuesto disponible");
        }

        //valida si el monto no excede del presupuesto
        if(proyecto.getPresupuesto().compareTo(dto.getMonto()) < 0){
            throw new IllegalArgumentException("El monto excede del presupuesto disponible");
        }

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setProyecto(proyecto);
        pago.setMonto(dto.getMonto());
        pago.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());

        Pago guardado = pagoRepository.save(pago);

        //actualiza presupuesto del proyecto
        proyecto.setPresupuesto(proyecto.getPresupuesto().subtract(dto.getMonto()));
        proyectoRepository.save(proyecto);

        return PagoMapper.toResponseDto(guardado);
    }

    public List<PagoResponseDTO> consultarMisPagos(Authentication auth) {
        // validar que sea líder o miembro
        validarMultiplesRoles(auth, "ROLE_LIDER", "ROLE_MIEMBRO");

        String matricula = (String) auth.getPrincipal();
        return pagoRepository.findByUsuarioMatricula(matricula).stream()
                .map(PagoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PagoResponseDTO> consultarPagosMiembro(String matriculaMiembro, Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        String matriculaLider = (String) auth.getPrincipal();
        // verifica existencia de lider
        Usuario lider = usuarioRepository.findByMatricula(matriculaLider)
                .orElseThrow(() -> new IllegalArgumentException("Líder no encontrado"));

        // valida si tiene un proyecto asignado
        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("El líder no tiene un proyecto asignado");

        // verifica que el miembro pertenezca al proyecto del líder logueado
        if (!proyectoUsuarioRepository.existsByProyectoIdAndUsuarioMatricula(proyecto.getId(), matriculaMiembro)) {
            throw new SecurityException("No autorizado: El usuario no pertenece a su proyecto");
        }

        return pagoRepository.findByUsuarioMatriculaAndProyectoId(matriculaMiembro, proyecto.getId()).stream()
                .map(PagoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<PagoResponseDTO> consultarPagosProyecto(Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        String matriculaLider = (String) auth.getPrincipal();
        Usuario lider = usuarioRepository.findByMatricula(matriculaLider)
                .orElseThrow(() -> new IllegalArgumentException("Líder no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("El líder no tiene un proyecto asignado");

        return pagoRepository.findByProyectoId(proyecto.getId()).stream()
                .map(PagoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> obtenerHistorialVouchers(String matricula, Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String estado = usuario.getEstado() != null ? usuario.getEstado().trim() : "";
        if (!"ACTIVO".equalsIgnoreCase(estado)) {
            throw new IllegalArgumentException("Cuenta inactiva (" + estado + ")");
        }

        List<Pago> pagos = pagoRepository.findByUsuarioMatricula(matricula);
        List<VoucherDTO> vouchers = new ArrayList<>();

        LocalDate fechaIngreso = usuario.getFechaIngreso();
        if (fechaIngreso == null) {
            fechaIngreso = LocalDate.now();
        }

        LocalDate inicio = fechaIngreso;
        LocalDate hoy = LocalDate.now();
        int contador = 1;

        while (!inicio.isAfter(hoy)) {
            //calcula fin de quincena
            LocalDate fin = (inicio.getDayOfMonth() <= 15)
                    ? inicio.withDayOfMonth(15)
                    : inicio.withDayOfMonth(inicio.lengthOfMonth());

            BigDecimal montoQuincenal = usuario.getSalarioQuincenal() != null 
                    ? usuario.getSalarioQuincenal() 
                    : BigDecimal.ZERO;

            if (vouchers.isEmpty() && montoQuincenal.compareTo(BigDecimal.ZERO) > 0) {
                long diasTrabajados = ChronoUnit.DAYS.between(inicio, fin) + 1;
                // calculo de pago proporcional
                if (diasTrabajados < 15) {
                    montoQuincenal = montoQuincenal.multiply(BigDecimal.valueOf(diasTrabajados))
                            .divide(BigDecimal.valueOf(15), 2, RoundingMode.HALF_UP);
                }
            }

            VoucherDTO v = new VoucherDTO();
            v.setNumeroQuincena(contador++);
            v.setFechaInicio(inicio);
            v.setFechaFin(fin);
            v.setMontoEsperado(montoQuincenal);
            v.setPuesto(usuario.getPuesto());

            final LocalDate pInicio = inicio;
            final LocalDate pFin = fin;

            Optional<Pago> pagoMatch = pagos.stream()
                    .filter(p -> !p.getFecha().isBefore(pInicio) && !p.getFecha().isAfter(pFin))
                    .findFirst();

            if (!hoy.isBefore(fin)) {
                // valida si la quincena ya finalizo
                if (pagoMatch.isPresent()) {
                    Pago p = pagoMatch.get();
                    v.setEstado("PAGADO");
                    v.setPagoId(p.getId());
                    v.setFechaPagoReal(p.getFecha());
                    v.setMontoPagado(p.getMonto());
                } else {
                    v.setEstado("PENDIENTE");
                }
            } else {
                // Qquincena en curso
                v.setEstado("PROGRAMADO");
                v.setFechaPagoReal(null);
            }

            // no se agregan las quincenas en curso
            if (!"PROGRAMADO".equals(v.getEstado())) {
                vouchers.add(v);
            }
            inicio = fin.plusDays(1);
        }

        return vouchers;
    }

    private void validarRol(Authentication auth, String rolEsperado) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rolEsperado))) {
            throw new SecurityException("No autorizado para esta operación");
        }
    }

    private void validarMultiplesRoles(Authentication auth, String... rolesEsperados) {
        boolean tieneRol = false;
        for (String rol : rolesEsperados) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol))) {
                tieneRol = true;
                break;
            }
        }
        if (!tieneRol) {
            throw new SecurityException("No autorizado para esta operación");
        }
    }
}
