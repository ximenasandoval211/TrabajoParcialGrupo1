package pe.edu.upc.trabajofinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.trabajofinal.model.entities.Role;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleType(RoleTypes roleType);
    Optional<Role> findByRoleType(RoleTypes roleType);

}