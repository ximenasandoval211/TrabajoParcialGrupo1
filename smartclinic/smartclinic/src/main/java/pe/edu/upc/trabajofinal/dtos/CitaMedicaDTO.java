package pe.edu.upc.trabajofinal.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitaMedicaDTO {
    private Long doctorId;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String motivo;
}
