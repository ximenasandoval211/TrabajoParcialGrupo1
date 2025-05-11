package pe.edu.upc.trabajofinal.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HorarioAtencionDTO {

    private String diaInicio;
    private String diaFin;
    private String horaInicio;
    private String horaFin;
}
