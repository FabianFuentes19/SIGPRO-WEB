package com.sigpro.service;

import com.sigpro.dto.PagoDTO;
import com.sigpro.dto.PagoMapper;
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
import java.util.List;
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
        String matricula = (String) auth.getPrincipal();
        return pagoRepository.findByUsuarioMatricula(matricula).stream()
                .map(PagoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PagoDTO> consultarPagosMiembro(String matriculaMiembro, Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        String matriculaLider = (String) auth.getPrincipal();
        Usuario lider = usuarioRepository.findByMatricula(matriculaLider)
                .orElseThrow(() -> new IllegalArgumentException("Líder no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("El líder no tiene un proyecto asignado");

        // Verificar que el miembro pertenece al proyecto del líder
        if (!proyectoUsuarioRepository.existsByProyectoIdAndUsuarioMatricula(proyecto.getId(), matriculaMiembro)) {
            throw new SecurityException("No autorizado: El usuario no pertenece a su proyecto");
        }

        return pagoRepository.findByUsuarioMatricula(matriculaMiembro).stream()
                .map(PagoMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validarRol(Authentication auth, String rolEsperado) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rolEsperado))) {
            throw new SecurityException("No autorizado para esta operación");
        }
    }
}
