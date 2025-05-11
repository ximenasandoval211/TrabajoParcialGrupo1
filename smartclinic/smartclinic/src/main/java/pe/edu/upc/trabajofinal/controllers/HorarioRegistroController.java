package pe.edu.upc.trabajofinal.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.trabajofinal.dtos.HorarioAtencionDTO;
import pe.edu.upc.trabajofinal.model.entities.HorarioAtencion;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.security.model.UserDetailsImpl;
import pe.edu.upc.trabajofinal.servicesinterfaces.HorarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/horarios", produces =  MediaType.APPLICATION_JSON_VALUE)
public class HorarioRegistroController {
    private final HorarioService horarioService;

    private final UserRepository userRepository;

    public HorarioRegistroController(HorarioService horarioService, UserRepository userRepository) {
        this.horarioService = horarioService;
        this.userRepository = userRepository;
    }

    private boolean isDoctor(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Optional<Users> user = userRepository.findById(userDetails.getId());
        return user.isPresent() && user.get().getRole().getRoleType() == RoleTypes.PROFESIONAL_SALUD;
    }

    @PostMapping
    public ResponseEntity<?> crearHorario(@RequestBody HorarioAtencionDTO dto, Authentication authentication) {
        if (!isDoctor(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los doctores pueden crear horarios.");
        }

        Long doctorId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        HorarioAtencion horario = horarioService.registrarHorario(doctorId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(horario);
    }

    @GetMapping
    public ResponseEntity<?> obtenerHorarios(Authentication authentication) {
        if (!isDoctor(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los doctores pueden ver sus horarios.");
        }

        Long doctorId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<HorarioAtencion> horarios = horarioService.obtenerHorariosPorDoctor(doctorId);
        return ResponseEntity.ok(horarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHorario(@PathVariable Long id, @RequestBody HorarioAtencionDTO dto,
                                               Authentication authentication) {
        if (!isDoctor(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado.");
        }

        Long doctorId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        HorarioAtencion actualizado = horarioService.actualizarHorario(id, dto, doctorId);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarHorario(@PathVariable Long id, Authentication authentication) {
        if (!isDoctor(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado.");
        }

        Long doctorId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        horarioService.eliminarHorario(id, doctorId);
        return ResponseEntity.noContent().build();
    }
}
