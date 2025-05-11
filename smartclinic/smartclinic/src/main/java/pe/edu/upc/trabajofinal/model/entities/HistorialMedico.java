package pe.edu.upc.trabajofinal.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@AllArgsConstructor
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Users paciente;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Users doctor;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String diagnostico;

    @Column(nullable = false)
    private String fechaRegistro;

    public HistorialMedico() {}
}
