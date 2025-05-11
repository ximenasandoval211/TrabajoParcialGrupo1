package pe.edu.upc.trabajofinal.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.dtos.HistorialMedicoDTO;
import pe.edu.upc.trabajofinal.model.entities.HistorialMedico;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.repositories.HistorialMedicoRepository;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.servicesinterfaces.HistorialMedicoService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialMedicoServiceImpl implements HistorialMedicoService {

    private final UserRepository userRepository;
    private final HistorialMedicoRepository historialMedicoRepository;

    public HistorialMedicoServiceImpl(UserRepository userRepository,
                                      HistorialMedicoRepository historialMedicoRepository) {
        this.userRepository = userRepository;
        this.historialMedicoRepository = historialMedicoRepository;
    }

    @Override
    public HistorialMedicoDTO registrarHistorial(Long doctorId, HistorialMedicoDTO dto) {
        Users doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        Users paciente = userRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        LocalDate fechaRegistro;
        try {
            fechaRegistro = LocalDate.parse(dto.getFechaRegistro());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Usa yyyy-MM-dd.");
        }

        HistorialMedico historial = new HistorialMedico();
        historial.setDoctor(doctor);
        historial.setPaciente(paciente);
        historial.setDescripcion(dto.getDescripcion());
        historial.setDiagnostico(dto.getDiagnostico());
        historial.setFechaRegistro(fechaRegistro.toString());

        historialMedicoRepository.save(historial);
        return dto;
    }

    @Override
    public void actualizarHistorial(Long historialId, HistorialMedicoDTO dto) {
        HistorialMedico historial = historialMedicoRepository.findById(historialId)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado"));

        Users paciente = userRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        LocalDate fechaRegistro;
        try {
            fechaRegistro = LocalDate.parse(dto.getFechaRegistro());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Usa yyyy-MM-dd.");
        }

        historial.setPaciente(paciente);
        historial.setDescripcion(dto.getDescripcion());
        historial.setDiagnostico(dto.getDiagnostico());
        historial.setFechaRegistro(fechaRegistro.toString());

        historialMedicoRepository.save(historial);
    }

    @Override
    public List<HistorialMedicoDTO> listarPorDoctor(Long doctorId) {
        Users doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        List<HistorialMedico> historiales = historialMedicoRepository.findByDoctor(doctor);

        return historiales.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HistorialMedicoDTO> listarPorPaciente(Long pacienteId) {
        Users paciente = userRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        List<HistorialMedico> historiales = historialMedicoRepository.findByPaciente(paciente);

        return historiales.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    private HistorialMedicoDTO mapToDTO(HistorialMedico historial) {
        HistorialMedicoDTO dto = new HistorialMedicoDTO();
        dto.setDescripcion(historial.getDescripcion());
        dto.setDiagnostico(historial.getDiagnostico());
        dto.setFechaRegistro(historial.getFechaRegistro());
        dto.setPacienteId(historial.getPaciente().getId());
        return dto;
    }
}
