package pe.edu.upc.trabajofinal.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;

@Entity
@Getter
@Setter
@Table(name = "especialidades")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EspecialidadTypes especialidadType;

    public Especialidad() {}

    public Especialidad(EspecialidadTypes especialidadType) {
        this.especialidadType = especialidadType;
    }
}
