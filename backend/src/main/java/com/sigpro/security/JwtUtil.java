package com.sigpro.security;

import com.sigpro.dto.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    //no se expone la llave para mayor seguridad
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // genera el token, el cual incluye rol y nombre
    public String generateToken(UsuarioDTO usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRolNombre());
        claims.put("nombre", usuario.getNombreCompleto());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getMatricula())
                .setIssuedAt( new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //extracción de la matricula
    public String extractMatricula(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    // método para validar el token
    public boolean validarToken(String token, String matricula){
        final String extract = extractMatricula(token);
        return (extract.equals(matricula));
    }

}

