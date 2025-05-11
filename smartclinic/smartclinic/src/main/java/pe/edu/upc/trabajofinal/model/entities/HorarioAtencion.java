package pe.edu.upc.trabajofinal.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "horarios_atencion")
public class HorarioAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String diaInicio;

    @Column(nullable = false)
    private String diaFin;

    @Column(nullable = false)
    private String horaInicio;

    @Column(nullable = false)
    private String horaFin;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users doctor;

    public HorarioAtencion() {}
}