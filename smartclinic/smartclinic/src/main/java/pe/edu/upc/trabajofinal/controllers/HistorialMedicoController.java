package pe.edu.upc.trabajofinal.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.trabajofinal.dtos.HistorialMedicoDTO;
import pe.edu.upc.trabajofinal.security.model.UserDetailsImpl;
import pe.edu.upc.trabajofinal.servicesinterfaces.HistorialMedicoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
@RequiredArgsConstructor
public class HistorialMedicoController {

    private final HistorialMedicoService historialMedicoService;

    @PostMapping
    public ResponseEntity<HistorialMedicoDTO> registrarHistorial(@RequestBody HistorialMedicoDTO historialMedicoDTO, Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Long userId = userDetails.getId();
        String role = userDetails.getRole();

        if (!role.equals("PROFESIONAL_SALUD")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long pacienteId = historialMedicoDTO.getPacienteId();
        if (pacienteId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        HistorialMedicoDTO nuevoHistorial = historialMedicoService.registrarHistorial(userId, historialMedicoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHistorial);
    }


    @PutMapping("/actualizar/{historialId}")
    public ResponseEntity<String> actualizarHistorial(
            @PathVariable Long historialId,
            @RequestBody HistorialMedicoDTO historialDTO) {
        historialMedicoService.actualizarHistorial(historialId, historialDTO);
        return ResponseEntity.ok("Historial médico actualizado exitosamente.");
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<HistorialMedicoDTO>> listarPorDoctor(Authentication authentication) {
        UserDetailsImpl doctor = (UserDetailsImpl) authentication.getPrincipal();

        if (!doctor.getRole().equals("PROFESIONAL_SALUD")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<HistorialMedicoDTO> historiales = historialMedicoService.listarPorDoctor(doctor.getId());
        return ResponseEntity.ok(historiales);
    }

    @GetMapping("/paciente")
    public ResponseEntity<List<HistorialMedicoDTO>> listarPorPaciente(Authentication authentication) {
        UserDetailsImpl paciente = (UserDetailsImpl) authentication.getPrincipal();

        if (!paciente.getRole().equals("PACIENTE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<HistorialMedicoDTO> historiales = historialMedicoService.listarPorPaciente(paciente.getId());
        return ResponseEntity.ok(historiales);
    }
}
