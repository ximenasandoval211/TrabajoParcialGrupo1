package pe.edu.upc.trabajofinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.trabajofinal.model.entities.Especialidad;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByUsername(String username);
	@Query("SELECT u FROM Users u WHERE (u.name LIKE %:nombre% OR u.especialidad = :especialidad) AND u.role.roleType = :roleType")
	List<Users> findDoctoresByNombreOrEspecialidad(@Param("nombre") String nombre, @Param("especialidad") Especialidad especialidad, @Param("roleType") RoleTypes roleType);


}