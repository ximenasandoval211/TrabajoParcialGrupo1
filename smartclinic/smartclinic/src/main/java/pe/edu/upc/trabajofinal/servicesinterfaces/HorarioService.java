package pe.edu.upc.trabajofinal.servicesinterfaces;

import pe.edu.upc.trabajofinal.dtos.HorarioAtencionDTO;
import pe.edu.upc.trabajofinal.model.entities.HorarioAtencion;

import java.util.List;
import java.util.Optional;

public interface HorarioService {
    HorarioAtencion registrarHorario(Long doctorId, HorarioAtencionDTO dto);
    List<HorarioAtencion> obtenerHorariosPorDoctor(Long doctorId);
    HorarioAtencion actualizarHorario(Long horarioId, HorarioAtencionDTO dto, Long doctorId);
    void eliminarHorario(Long horarioId, Long doctorId);
    List<HorarioAtencion> obtenerHorarioPorDoctorId(Long doctorId);
}
