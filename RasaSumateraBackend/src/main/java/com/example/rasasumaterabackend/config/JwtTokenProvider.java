package com.example.rasasumaterabackend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Kunci rahasia minimal 256-bit untuk proses enkripsi JWT Hs256
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Masa berlaku token disetel 24 Jam
    private final long jwtExpirationInMs = 86400000;

    // Membuat token JWT berdasarkan Email pengguna yang sukses login
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    // Ekstraksi email kembali dari dalam token JWT
    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Validasi token yang dikirimkan oleh JavaFX Header
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Token bisa saja kadaluwarsa, tidak valid, atau struktur rusak
            return false;
        }
    }
}