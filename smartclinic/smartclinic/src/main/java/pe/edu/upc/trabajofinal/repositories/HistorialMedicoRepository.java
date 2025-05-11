package pe.edu.upc.trabajofinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.trabajofinal.model.entities.HistorialMedico;
import pe.edu.upc.trabajofinal.model.entities.Users;

import java.util.List;

public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    List<HistorialMedico> findByDoctor(Users doctor);

    List<HistorialMedico> findByPaciente(Users paciente);
}
