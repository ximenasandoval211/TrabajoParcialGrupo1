package pe.edu.upc.trabajofinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.trabajofinal.model.entities.Especialidad;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;

import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    boolean existsByEspecialidadType(EspecialidadTypes especialidadTypes);
    Optional<Especialidad> findByEspecialidadType(EspecialidadTypes especialidadTypes);
}
