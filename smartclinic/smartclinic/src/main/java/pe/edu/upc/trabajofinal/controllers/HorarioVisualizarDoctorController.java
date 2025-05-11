package pe.edu.upc.trabajofinal.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.trabajofinal.model.entities.HorarioAtencion;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.security.model.UserDetailsImpl;
import pe.edu.upc.trabajofinal.servicesinterfaces.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/public/horarios")
@RequiredArgsConstructor
public class HorarioVisualizarDoctorController {

    private final HorarioService horarioService;
    private final UserRepository userRepository;

    @GetMapping("/{doctorId}")
    public ResponseEntity<?> verHorarioDoctor(@PathVariable Long doctorId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole().getRoleType() != RoleTypes.PACIENTE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los pacientes pueden ver los horarios");
        }

        List<HorarioAtencion> horario = horarioService.obtenerHorarioPorDoctorId(doctorId);
        if (horario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró horario para este doctor");
        }

        return ResponseEntity.ok(horario.get(0));
    }
}

