package pe.edu.upc.trabajofinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.trabajofinal.model.entities.CitaMedica;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.EstadoCita;

import java.util.List;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {
    List<CitaMedica> findByPacienteId(Long pacienteId);

    boolean existsByDoctorAndFechaAndHoraInicioAndHoraFinAndEstadoNot(
            Users doctor,
            String fecha,
            String horaInicio,
            String horaFin,
            EstadoCita estadoExcluido
    );
}


