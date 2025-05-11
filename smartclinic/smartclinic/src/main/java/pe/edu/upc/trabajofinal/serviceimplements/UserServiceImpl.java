package pe.edu.upc.trabajofinal.serviceimplements;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.dtos.SignInDTO;
import pe.edu.upc.trabajofinal.dtos.SignUpDTO;
import pe.edu.upc.trabajofinal.model.entities.Especialidad;
import pe.edu.upc.trabajofinal.model.entities.Role;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.repositories.EspecialidadRepository;
import pe.edu.upc.trabajofinal.repositories.RoleRepository;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.security.interfaces.HashingService;
import pe.edu.upc.trabajofinal.security.interfaces.TokenService;
import pe.edu.upc.trabajofinal.servicesinterfaces.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final HashingService hashingService;
    private final TokenService tokenService;
    private final EspecialidadRepository especialidadRepository;

    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository, HashingService hashingService,
                           TokenService tokenService, EspecialidadRepository especialidadRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.especialidadRepository = especialidadRepository;
    }

    @Override
    public Optional<Users> handle(SignUpDTO signUpDTO) {

        if (signUpDTO.role() == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        var roleType = signUpDTO.role();
        Optional<Role> role = roleRepository.findByRoleType(RoleTypes.valueOf(roleType));

        var especialidadType = signUpDTO.especialidad();
        Optional<Especialidad> especialidad = especialidadRepository.findByEspecialidadType(EspecialidadTypes.valueOf(especialidadType));

        if(signUpDTO.name() == null || signUpDTO.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if(signUpDTO.username() == null || signUpDTO.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        if(signUpDTO.password() == null || signUpDTO.password().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        var user = new Users(signUpDTO.name(), signUpDTO.username(), hashingService.encode(signUpDTO.password()), role.get(), especialidad.get());
        var createdUser = userRepository.save(user);

        return Optional.of(createdUser);
    }

    public Optional<ImmutablePair<Users, String>> handle(SignInDTO signInDTO) {

        var user = userRepository.findByUsername(signInDTO.username()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        RoleTypes roleType = user.getRole().getRoleType();

        if (!hashingService.matches(signInDTO.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        else {
            var token = tokenService.generateToken(user.getUsername(), String.valueOf(user.getId()), roleType.name());
            return Optional.of(ImmutablePair.of(user, token));
        }
    }
    @Override
    public Optional<Users> updatePassword(Long userId, String newPassword) {
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String encryptedPassword = hashingService.encode(newPassword);
        user.setPassword(encryptedPassword);
        var updatedUser = userRepository.save(user);
        return Optional.of(updatedUser);
    }

    @Override
    public Optional<Users> updateName(Long userId, String newName) {
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(newName);
        var updatedUser = userRepository.save(user);
        return Optional.of(updatedUser);
    }

    @Override
    public Optional<Users> updateUsername(Long userId, String newUsername) {
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setUsername(newUsername);
        var updatedUser = userRepository.save(user);
        return Optional.of(updatedUser);
    }
}