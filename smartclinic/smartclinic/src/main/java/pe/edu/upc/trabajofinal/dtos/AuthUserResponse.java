package pe.edu.upc.trabajofinal.dtos;

public record AuthUserResponse(Long id, String username, String token) {
    public AuthUserResponse {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
    }
}