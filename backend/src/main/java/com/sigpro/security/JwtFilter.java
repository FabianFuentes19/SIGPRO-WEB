package com.sigpro.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path != null && path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // busca el encabezado llamado Authorization
        final String authHeader = request.getHeader("Authorization");
        String matricula = null;
        String jwt = null;

        // verifica que el encabezado exista
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                //obtiene la matricula desde el token
                matricula = jwtUtil.extractMatricula(jwt);
            } catch (Exception e){
                logger.error("Token inválido o expirado");
            }
        }

        if (matricula != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // verifica que el token sea valido
            if (jwtUtil.validarToken(jwt, matricula)) {
                String rol = jwtUtil.extractRol(jwt); // este método lo agregas en JwtUtil

                // crea la autoridad con el rol
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(matricula, null, authorities);

                // permite al usuario ver las rutas protegidas
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // permite que la petición llegue al controlador
        chain.doFilter(request, response);

    }
}
