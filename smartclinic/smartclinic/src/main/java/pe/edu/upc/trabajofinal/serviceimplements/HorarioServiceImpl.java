package pe.edu.upc.trabajofinal.serviceimplements;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.dtos.HorarioAtencionDTO;
import pe.edu.upc.trabajofinal.model.entities.HorarioAtencion;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.repositories.HorariosRepository;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.servicesinterfaces.HorarioService;

import java.util.List;

@Service
public class HorarioServiceImpl implements HorarioService {

    private final HorariosRepository horarioRepository;

    private final UserRepository userRepository;

    public HorarioServiceImpl(HorariosRepository horarioRepository, UserRepository userRepository) {
        this.horarioRepository = horarioRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public HorarioAtencion registrarHorario(Long doctorId, HorarioAtencionDTO dto) {
        Users doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        boolean yaTieneHorario = horarioRepository.existsByDoctorId(doctorId);
        if (yaTieneHorario) {
            throw new IllegalStateException("Ya existe un horario registrado para este doctor.");
        }

        HorarioAtencion horario = new HorarioAtencion();
        horario.setDiaInicio(dto.getDiaInicio().toUpperCase());
        horario.setDiaFin(dto.getDiaFin().toUpperCase());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        horario.setDoctor(doctor);

        return horarioRepository.save(horario);
    }

    @Override
    public List<HorarioAtencion> obtenerHorarioPorDoctorId(Long doctorId) {
        return horarioRepository.findByDoctorId(doctorId);
    }



    @Override
    public List<HorarioAtencion> obtenerHorariosPorDoctor(Long doctorId) {
        return horarioRepository.findByDoctorId(doctorId);
    }

    @Override
    @Transactional
    public HorarioAtencion actualizarHorario(Long horarioId, HorarioAtencionDTO dto, Long doctorId) {
        HorarioAtencion horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!horario.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("No autorizado para modificar este horario.");
        }

        horario.setDiaInicio(dto.getDiaInicio().toUpperCase());
        horario.setDiaFin(dto.getDiaFin().toUpperCase());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());

        return horarioRepository.save(horario);
    }

    @Override
    @Transactional
    public void eliminarHorario(Long horarioId, Long doctorId) {
        HorarioAtencion horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!horario.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("No autorizado para eliminar este horario.");
        }

        horarioRepository.delete(horario);
    }
}