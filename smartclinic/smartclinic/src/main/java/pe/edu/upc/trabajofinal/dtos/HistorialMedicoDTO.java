package pe.edu.upc.trabajofinal.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistorialMedicoDTO {

    private Long pacienteId;
    private String descripcion;
    private String diagnostico;
    private String fechaRegistro;
}
