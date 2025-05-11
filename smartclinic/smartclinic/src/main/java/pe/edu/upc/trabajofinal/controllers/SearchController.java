package pe.edu.upc.trabajofinal.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.trabajofinal.model.entities.Especialidad;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;
import pe.edu.upc.trabajofinal.repositories.EspecialidadRepository;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.security.model.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController {

    private final UserRepository userRepository;
    private final EspecialidadRepository especialidadRepository;

    public SearchController(UserRepository userRepository, EspecialidadRepository especialidadRepository) {
        this.userRepository = userRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @GetMapping("/doctores")
    public ResponseEntity<?> buscarDoctores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String especialidad,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Optional<Users> currentUser = userRepository.findById(userId);
        if (currentUser.isEmpty() || currentUser.get().getRole().getRoleType() != RoleTypes.PACIENTE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los pacientes pueden buscar doctores.");
        }

        EspecialidadTypes especialidadEnum = null;
        Especialidad especialidadEntity = null;
        if (especialidad != null && !especialidad.isBlank()) {
            try {
                especialidadEnum = EspecialidadTypes.valueOf(especialidad.toUpperCase());
                especialidadEntity = especialidadRepository.findByEspecialidadType(EspecialidadTypes.valueOf(especialidadEnum.name()))
                        .orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada"));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Especialidad no válida: " + especialidad);
            }
        }

        List<Users> doctores = userRepository.findDoctoresByNombreOrEspecialidad(nombre, especialidadEntity, RoleTypes.PROFESIONAL_SALUD);
        return ResponseEntity.ok(doctores);
    }
}