package pe.edu.upc.trabajofinal.servicesinterfaces;

import pe.edu.upc.trabajofinal.dtos.HistorialMedicoDTO;

import java.util.List;

public interface HistorialMedicoService {

    HistorialMedicoDTO registrarHistorial(Long doctorId, HistorialMedicoDTO dto);

    void actualizarHistorial(Long historialId, HistorialMedicoDTO dto);

    List<HistorialMedicoDTO> listarPorDoctor(Long doctorId);
    List<HistorialMedicoDTO> listarPorPaciente(Long pacienteId);
}
