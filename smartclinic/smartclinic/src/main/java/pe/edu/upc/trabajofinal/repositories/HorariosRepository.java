package pe.edu.upc.trabajofinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.trabajofinal.model.entities.HorarioAtencion;
import pe.edu.upc.trabajofinal.model.entities.Users;

import java.util.List;
import java.util.Optional;

public interface HorariosRepository extends JpaRepository<HorarioAtencion, Long> {

    List<HorarioAtencion> findByDoctorId(Long doctorId);
    Optional<HorarioAtencion> findByDoctor(Users doctor);
    boolean existsByDoctorId(Long doctorId);}
