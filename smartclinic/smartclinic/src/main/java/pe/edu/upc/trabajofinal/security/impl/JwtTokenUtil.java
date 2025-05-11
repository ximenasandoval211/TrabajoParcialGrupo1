package pe.edu.upc.trabajofinal.security.impl;
import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.security.interfaces.BearerTokenService;

import javax.crypto.SecretKey;


@Service
public class JwtTokenUtil implements BearerTokenService {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private final SecretKey signingKey;

    public JwtTokenUtil(SecretKey signingKey) {
        this.signingKey = signingKey;
    }

    @Value("${jwt.expiration.days}")
    private int expirationDays;

    @Override
    public String generateToken(String username, String userId, String role) {
        LOGGER.debug("Generating token for username: {}", username);
        String token = buildTokenWithDefaultParameters(username, userId, role);
        LOGGER.debug("Generated token: {}", token);
        return token;
    }

    private String buildTokenWithDefaultParameters(String username, String userId, String role) {
        var issuedAt = new Date();
        var expiration = DateUtils.addDays(issuedAt, expirationDays);
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", String.class);
    }

    @Override
    public boolean validateToken(String token) {
        LOGGER.debug("Validating token: {}", token);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            LOGGER.info("Token is valid");
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            LOGGER.info("Received JWT: {}", token);
            return token;
        }
        return null;
    }
}