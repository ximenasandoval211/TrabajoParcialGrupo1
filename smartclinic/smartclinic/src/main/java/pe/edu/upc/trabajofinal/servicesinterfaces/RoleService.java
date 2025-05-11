package pe.edu.upc.trabajofinal.servicesinterfaces;

import pe.edu.upc.trabajofinal.servicesinterfaces.commands.SeedRoleTypeCommand;

public interface RoleService {

    void handle(SeedRoleTypeCommand command);
}
