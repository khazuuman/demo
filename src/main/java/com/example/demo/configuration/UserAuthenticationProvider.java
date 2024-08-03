package com.example.demo.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    @Value("${jwt.signerKey}")
    private String secretKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    private final AdminRepository adminRepository;

    public String createToken(Admin admin) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(admin.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.DAYS).toEpochMilli()))
                .withClaim("username", admin.getUsername())
                .withClaim("password", admin.getPassword())
                .sign(algorithm);
    }

    public Authentication validateToken(String token) {
        System.out.println(token);
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);
            System.out.println(decoded.getSubject());
            Admin user = adminRepository.findByUsername(decoded.getSubject()).orElseThrow(() -> new RuntimeException("User not found"));

            return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        } catch (RuntimeException e) {
            System.out.println("Error validating token: " + e.getMessage());
            throw e;
        }
    }

    public DecodedJWT decodeToken(String token) {
        try {
            return JWT.decode(token);
        } catch (Exception e) {
            System.out.println("Error decoding token: " + e.getMessage());
            return null;
        }
    }

}
