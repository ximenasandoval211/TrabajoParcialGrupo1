package pe.edu.upc.trabajofinal.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.trabajofinal.dtos.SignUpDTO;
import pe.edu.upc.trabajofinal.dtos.UpdateNameDTO;
import pe.edu.upc.trabajofinal.dtos.UserPasswordDTO;
import pe.edu.upc.trabajofinal.dtos.UserUsernameDTO;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;
import pe.edu.upc.trabajofinal.repositories.EspecialidadRepository;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.security.model.UserDetailsImpl;
import pe.edu.upc.trabajofinal.servicesinterfaces.UserService;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EspecialidadRepository especialidadRepository;

    public UserController(UserRepository userRepository, UserService userService, EspecialidadRepository especialidadRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.especialidadRepository = especialidadRepository;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDTO signUpDTO, HttpServletRequest request) {

        try {
            String password = signUpDTO.password();

            String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
            Pattern pattern = Pattern.compile(passwordRegex);

            if (!pattern.matcher(password).matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Password must be at least 8 characters long, and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
            }

            Optional<Users> users = userRepository.findByUsername(signUpDTO.username());
            if (users.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("User with this username already exists");
            }

            String role = signUpDTO.role();
            String specialty = signUpDTO.especialidad();

            if (role.equalsIgnoreCase("PACIENTE")) {
                if (!specialty.equalsIgnoreCase("NO_APLICA")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Pacientes solo pueden tener la especialidad 'NO_APLICA'.");
                }
            } else if (role.equalsIgnoreCase("PROFESIONAL_SALUD")) {
                if (specialty.equalsIgnoreCase("NO_APLICA")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Profesionales de salud no pueden tener la especialidad 'NO_APLICA'.");
                }

                boolean exists = especialidadRepository.existsByEspecialidadType(EspecialidadTypes.valueOf(specialty));
                if (!exists) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Especialidad inválida para profesional de salud.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Rol inválido. Solo se permiten 'PACIENTE' o 'PROFESIONAL_SALUD'.");
            }


            var user = userService.handle(signUpDTO);
            if (user.isPresent()) {
                log.info("User created successfully: {}", user.get());
                userRepository.save(user.get());
                request.getSession().setAttribute("userId", user.get().getId().toString());

                return ResponseEntity.status(HttpStatus.CREATED).body(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("User could not be created");
            }

        } catch (Exception e) {
            log.error("Error creating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error creating user");
        }
    }


    @GetMapping("/me")
    public ResponseEntity<Users> getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return userRepository.findByUsername(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/update-name")
    public ResponseEntity<?> updateName(@RequestBody UpdateNameDTO updateNameDTO, Authentication authentication,  HttpServletRequest servletRequest) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

       String newName = updateNameDTO.getNewName();
       Optional<Users> user = userService.updateName(userId, newName);

        if (user.isPresent()) {
            log.info("User name updated successfully: {}", user.get());
            servletRequest.getSession().invalidate();
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("User name could not be updated");
        }
    }

    @PatchMapping("/update-username")
    public ResponseEntity<?> updateUsername(@RequestBody UserUsernameDTO updateUsernameDTO, Authentication authentication, HttpServletRequest servletRequest) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        String newUsername = updateUsernameDTO.getUsername();
        Optional<Users> user = userService.updateUsername(userId, newUsername);

        if (user.isPresent()) {
            log.info("User username updated successfully: {}", user.get());
            servletRequest.getSession().invalidate();
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("User username could not be updated");
        }
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UserPasswordDTO updatePassDTO, Authentication authentication, HttpServletRequest servletRequest) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        String newPassword = updatePassDTO.getPassword();
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        if (!pattern.matcher(newPassword).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Password must be at least 8 characters long, and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."));
        }

        Optional<Users> user = userService.updatePassword(userId, newPassword);

        if (user.isPresent()) {
            log.info("User password updated successfully: {}", user.get());
            servletRequest.getSession().invalidate();
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("User password could not be updated");
        }
    }
}