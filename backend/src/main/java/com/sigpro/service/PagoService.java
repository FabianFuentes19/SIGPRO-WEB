package com.sigpro.service;

import com.sigpro.dto.PagoDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public PagoDTO registrarPago(PagoDTO dto) {
        Usuario usuario = usuarioRepository.findByMatricula(dto.getMatriculaUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setProyecto(proyecto);
        pago.setMonto(dto.getMonto());
        pago.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());

        Pago guardado = pagoRepository.save(pago);

        return PagoMapper.toDto(guardado);
    }

    public List<PagoDTO> consultarMisPagos(Authentication auth) {
        // validar que sea líder o miembro
        validarMultiplesRoles(auth, "ROLE_LIDER", "ROLE_MIEMBRO");

        String matricula = (String) auth.getPrincipal();
        return pagoRepository.findByUsuarioMatricula(matricula).stream()
                .map(PagoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PagoDTO> consultarPagosMiembro(String matriculaMiembro, Authentication auth) {
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
                .map(PagoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PagoDTO> consultarPagosProyecto(Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        String matriculaLider = (String) auth.getPrincipal();
        Usuario lider = usuarioRepository.findByMatricula(matriculaLider)
                .orElseThrow(() -> new IllegalArgumentException("Líder no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("El líder no tiene un proyecto asignado");

        return pagoRepository.findByProyectoId(proyecto.getId()).stream()
                .map(PagoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> obtenerHistorialVouchers(String matricula, Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Pago> pagos = pagoRepository.findByUsuarioMatricula(matricula);
        List<VoucherDTO> vouchers = new ArrayList<>();

        LocalDate fechaIngreso = usuario.getFechaIngreso();
        if (fechaIngreso == null) fechaIngreso = LocalDate.now().minusMonths(1);
        
        LocalDate inicio;
        if (fechaIngreso.getDayOfMonth() <= 15) {
            inicio = fechaIngreso.withDayOfMonth(1);
        } else {
            inicio = fechaIngreso.withDayOfMonth(16);
        }

        LocalDate hoy = LocalDate.now();
        int contador = 1;

        // Iterar quincenas hasta llegar a la quincena actual
        while (!inicio.isAfter(hoy)) {
            LocalDate fin;
            if (inicio.getDayOfMonth() == 1) {
                fin = inicio.withDayOfMonth(15);
            } else {
                fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
            }

            VoucherDTO v = new VoucherDTO();
            v.setNumeroQuincena(contador++);
            v.setFechaInicio(inicio);
            v.setFechaFin(fin);
            v.setMontoEsperado(usuario.getSalarioQuincenal());

            final LocalDate pInicio = inicio;
            final LocalDate pFin = fin;

            Optional<Pago> pagoMatch = pagos.stream()
                    .filter(p -> !p.getFecha().isBefore(pInicio) && !p.getFecha().isAfter(pFin))
                    .findFirst();

            if (pagoMatch.isPresent()) {
                Pago p = pagoMatch.get();
                v.setEstado("PAGADO");
                v.setPagoId(p.getId());
                v.setFechaPagoReal(p.getFecha());
                v.setMontoPagado(p.getMonto());
            } else {
                v.setEstado("PENDIENTE");
            }

            vouchers.add(v);
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
