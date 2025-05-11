package pe.edu.upc.trabajofinal.security.interfaces;

import jakarta.servlet.http.HttpServletRequest;

public interface BearerTokenService extends TokenService {

    String getBearerTokenFrom(HttpServletRequest token);
    String generateToken(String username, String userId, String role);
}