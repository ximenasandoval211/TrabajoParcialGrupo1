package pe.edu.upc.trabajofinal.serviceimplements;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.model.entities.Especialidad;
import pe.edu.upc.trabajofinal.model.valueobjects.EspecialidadTypes;
import pe.edu.upc.trabajofinal.repositories.EspecialidadRepository;
import pe.edu.upc.trabajofinal.servicesinterfaces.EspecialidadService;
import pe.edu.upc.trabajofinal.servicesinterfaces.commands.SeedEspecialidadTypeCommand;

import java.util.Arrays;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @PostConstruct
    public void init(){handle(new SeedEspecialidadTypeCommand());}

    @Override
    public void handle(SeedEspecialidadTypeCommand command) {
        Arrays.stream(EspecialidadTypes.values()).forEach(especialidadTypes -> {
            if (!especialidadRepository.existsByEspecialidadType(especialidadTypes)) {
                especialidadRepository.save(new Especialidad(especialidadTypes));
            }
        });
    }
}
