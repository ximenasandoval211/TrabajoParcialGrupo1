package pe.edu.upc.trabajofinal.servicesinterfaces;

import pe.edu.upc.trabajofinal.dtos.CitaMedicaDTO;
import pe.edu.upc.trabajofinal.model.entities.CitaMedica;

import java.util.List;

public interface CitaMedicaService {

    CitaMedica agendarCita(Long pacienteId, CitaMedicaDTO dto);
    List<CitaMedica> obtenerCitasDePaciente(Long pacienteId);
    void cancelarCita(Long citaId);
}
