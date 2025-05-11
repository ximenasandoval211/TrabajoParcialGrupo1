package pe.edu.upc.trabajofinal.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "especialidad_id", nullable = false)
	private Especialidad especialidad;

	protected Users() {}

	public Users(String name, String username, String password, Role role, Especialidad especialidad) {
		this.name = name;
		this.username = username;
		this.password = password;
		this.role = role;
		this.especialidad = especialidad;
	}
}