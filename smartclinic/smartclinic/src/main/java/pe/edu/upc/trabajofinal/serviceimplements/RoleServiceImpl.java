package pe.edu.upc.trabajofinal.serviceimplements;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.model.entities.Role;
import pe.edu.upc.trabajofinal.model.valueobjects.RoleTypes;
import pe.edu.upc.trabajofinal.repositories.RoleRepository;
import pe.edu.upc.trabajofinal.servicesinterfaces.RoleService;
import pe.edu.upc.trabajofinal.servicesinterfaces.commands.SeedRoleTypeCommand;

import java.util.Arrays;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        handle(new SeedRoleTypeCommand());
    }

    @Override
    public void handle(SeedRoleTypeCommand command) {
        Arrays.stream(RoleTypes.values()).forEach(roleType -> {
            if (!roleRepository.existsByRoleType(roleType)) {
                roleRepository.save(new Role(roleType));
            }
        });    }
}
