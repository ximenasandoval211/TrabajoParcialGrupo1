package pe.edu.upc.trabajofinal.servicesinterfaces;

import pe.edu.upc.trabajofinal.servicesinterfaces.commands.SeedEspecialidadTypeCommand;

public interface EspecialidadService {

    void handle(SeedEspecialidadTypeCommand command);
}
