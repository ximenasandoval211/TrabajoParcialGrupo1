package pe.edu.upc.trabajofinal.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RoleTypes roleType;

	public Role() {}

	public Role(RoleTypes roleType) {
		this.roleType = roleType;
	}

}