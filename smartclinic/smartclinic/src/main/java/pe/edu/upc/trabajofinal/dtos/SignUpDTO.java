package pe.edu.upc.trabajofinal.dtos;

public record SignUpDTO(String name, String username, String password, String role, String especialidad) {
    public SignUpDTO {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("Especialidad cannot be null or blank");
        }
    }
}
