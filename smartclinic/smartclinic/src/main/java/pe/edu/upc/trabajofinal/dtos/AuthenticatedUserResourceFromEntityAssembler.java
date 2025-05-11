package pe.edu.upc.trabajofinal.dtos;

import pe.edu.upc.trabajofinal.model.entities.Users;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthUserResponse toResourceFromEntity(Users user, String token) {
        return new AuthUserResponse(user.getId(), user.getName(), token);
    }
}
