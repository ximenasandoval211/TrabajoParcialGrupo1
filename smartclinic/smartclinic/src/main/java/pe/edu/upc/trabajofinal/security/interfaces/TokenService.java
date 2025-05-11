package pe.edu.upc.trabajofinal.security.interfaces;

public interface TokenService {

    String generateToken(String email, String userId, String role);

    String getEmailFromToken(String token);

    String getUserIdFromToken(String token);
    boolean validateToken(String token);

}

