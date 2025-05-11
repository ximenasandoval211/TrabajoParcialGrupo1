package pe.edu.upc.trabajofinal.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.trabajofinal.dtos.CitaMedicaDTO;
import pe.edu.upc.trabajofinal.model.entities.CitaMedica;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.security.model.UserDetailsImpl;
import pe.edu.upc.trabajofinal.servicesinterfaces.CitaMedicaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/paciente/citas", produces = MediaType.APPLICATION_JSON_VALUE)
public class CitaMedicaController {

    private final CitaMedicaService citaService;
    private final UserRepository userRepository;

    public CitaMedicaController(CitaMedicaService citaService, UserRepository userRepository) {
        this.citaService = citaService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> agendarCita(@RequestBody CitaMedicaDTO dto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole().getRoleType() != RoleTypes.PACIENTE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los pacientes pueden agendar citas");
        }

        CitaMedica cita = citaService.agendarCita(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cita);
    }

    @GetMapping
    public ResponseEntity<?> verMisCitas(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<CitaMedica> citas = citaService.obtenerCitasDePaciente(userDetails.getId());
        return ResponseEntity.ok(citas);
    }

    @DeleteMapping("/{citaId}")
    public ResponseEntity<String> cancelarCita(@PathVariable Long citaId) {
        try {
            citaService.cancelarCita(citaId);
            return ResponseEntity.ok("Cita cancelada exitosamente.");
        } catch (IllegalArgumentException e) {
            log.error("Error al cancelar la cita: " + e.getMessage());
            return ResponseEntity.badRequest().body("No se puede cancelar la cita: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al cancelar la cita: ", e);
            return ResponseEntity.status(500).body("Error inesperado al cancelar la cita.");
        }
    }
}
