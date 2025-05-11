package pe.edu.upc.trabajofinal.servicesinterfaces;

import org.apache.commons.lang3.tuple.ImmutablePair;
import pe.edu.upc.trabajofinal.dtos.SignInDTO;
import pe.edu.upc.trabajofinal.dtos.SignUpDTO;
import pe.edu.upc.trabajofinal.model.entities.Users;

import java.util.Optional;

public interface UserService {

    Optional<Users> handle(SignUpDTO signUpDTO);
    Optional<ImmutablePair<Users, String>> handle(SignInDTO signInDTO);
    Optional<Users> updatePassword(Long userId, String newPassword);
    Optional<Users> updateName(Long userId, String newName);
    Optional<Users> updateUsername(Long userId, String newUsername);
}
