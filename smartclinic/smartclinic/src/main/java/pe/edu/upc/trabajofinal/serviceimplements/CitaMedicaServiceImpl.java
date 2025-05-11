package pe.edu.upc.trabajofinal.serviceimplements;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upc.trabajofinal.dtos.CitaMedicaDTO;
import pe.edu.upc.trabajofinal.model.entities.CitaMedica;
import pe.edu.upc.trabajofinal.model.entities.HorarioAtencion;
import pe.edu.upc.trabajofinal.model.entities.Users;
import pe.edu.upc.trabajofinal.model.valueobjects.EstadoCita;
import pe.edu.upc.trabajofinal.repositories.CitaMedicaRepository;
import pe.edu.upc.trabajofinal.repositories.HorariosRepository;
import pe.edu.upc.trabajofinal.repositories.UserRepository;
import pe.edu.upc.trabajofinal.servicesinterfaces.CitaMedicaService;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
public class CitaMedicaServiceImpl implements CitaMedicaService {

    private final UserRepository userRepository;
    private final CitaMedicaRepository citaMedicaRepository;
    private final HorariosRepository horariosRepository;

    public CitaMedicaServiceImpl(UserRepository userRepository, CitaMedicaRepository citaMedicaRepository, HorariosRepository horariosRepository) {
        this.userRepository = userRepository;
        this.citaMedicaRepository = citaMedicaRepository;
        this.horariosRepository = horariosRepository;
    }

    private DayOfWeek traducirDia(String diaEnEspanol) {
        return switch (diaEnEspanol.toUpperCase()) {
            case "LUNES" -> DayOfWeek.MONDAY;
            case "MARTES" -> DayOfWeek.TUESDAY;
            case "MIERCOLES" -> DayOfWeek.WEDNESDAY;
            case "JUEVES" -> DayOfWeek.THURSDAY;
            case "VIERNES" -> DayOfWeek.FRIDAY;
            case "SABADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Día inválido: " + diaEnEspanol);
        };
    }

    @Override
    @Transactional
    public CitaMedica agendarCita(Long pacienteId, CitaMedicaDTO dto) {
        Users paciente = userRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Users doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        HorarioAtencion horario = horariosRepository.findByDoctor(doctor)
                .orElseThrow(() -> new RuntimeException("El doctor no tiene horario definido"));

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(dto.getFecha());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use yyyy-MM-dd");
        }

        ZoneId limaZoneId = ZoneId.of("America/Lima");
        ZonedDateTime fechaActual = ZonedDateTime.now(limaZoneId);

        if (fecha.isBefore(fechaActual.toLocalDate())) {
            throw new IllegalArgumentException("No se puede agendar una cita para una fecha pasada.");
        } else if (fecha.isEqual(fechaActual.toLocalDate()) && LocalTime.parse(dto.getHoraInicio()).isBefore(fechaActual.toLocalTime())) {
            throw new IllegalArgumentException("No se puede agendar una cita para una hora pasada.");
        }

        DayOfWeek diaCita = fecha.getDayOfWeek();
        DayOfWeek diaInicio = traducirDia(horario.getDiaInicio());
        DayOfWeek diaFin = traducirDia(horario.getDiaFin());

        if (diaCita.getValue() < diaInicio.getValue() || diaCita.getValue() > diaFin.getValue()) {
            throw new IllegalArgumentException("La cita debe estar entre " + horario.getDiaInicio() + " y " + horario.getDiaFin());
        }

        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

        if (!Duration.between(horaInicio, horaFin).equals(Duration.ofMinutes(30))) {
            throw new IllegalArgumentException("La cita debe durar exactamente 30 minutos");
        }

        LocalTime horaInicioDoctor = LocalTime.parse(horario.getHoraInicio());
        LocalTime horaFinDoctor = LocalTime.parse(horario.getHoraFin());

        if (horaInicio.isBefore(horaInicioDoctor) || horaFin.isAfter(horaFinDoctor)) {
            throw new IllegalArgumentException("La cita debe estar dentro del horario del doctor: " +
                    horario.getHoraInicio() + " - " + horario.getHoraFin());
        }

        if (Duration.between(horaInicioDoctor, horaInicio).toMinutes() % 30 != 0) {
            throw new IllegalArgumentException("Las citas deben comenzar cada 30 minutos desde " + horario.getHoraInicio());
        }

        boolean yaReservada = citaMedicaRepository.existsByDoctorAndFechaAndHoraInicioAndHoraFinAndEstadoNot(
                doctor,
                dto.getFecha(),
                dto.getHoraInicio(),
                dto.getHoraFin(),
                EstadoCita.CANCELADA
        );
        if (yaReservada) {
            throw new IllegalArgumentException("Ya existe una cita en ese horario con este doctor.");
        }

        CitaMedica cita = new CitaMedica();
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setFecha(fecha.toString());
        cita.setHoraInicio(String.valueOf(horaInicio));
        cita.setHoraFin(String.valueOf(horaFin));
        cita.setMotivo(dto.getMotivo());
        return citaMedicaRepository.save(cita);
    }

    @Override
    @Transactional
    public void cancelarCita(Long citaId) {
        CitaMedica cita = citaMedicaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        LocalDate fechaCita = LocalDate.parse(cita.getFecha());
        LocalTime horaInicioCita = LocalTime.parse(cita.getHoraInicio());

        ZoneId limaZoneId = ZoneId.of("America/Lima");

        ZonedDateTime fechaHoraActual = ZonedDateTime.now(limaZoneId);

        LocalDateTime fechaHoraCitaLocal = LocalDateTime.of(fechaCita, horaInicioCita);
        ZonedDateTime fechaHoraCita = fechaHoraCitaLocal.atZone(limaZoneId);

        Duration diferencia = Duration.between(fechaHoraActual, fechaHoraCita);

        if (diferencia.toHours() < 2) {
            throw new IllegalArgumentException("No se puede cancelar la cita con menos de 2 horas de anticipación.");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        citaMedicaRepository.save(cita);
    }


    @Override
    public List<CitaMedica> obtenerCitasDePaciente(Long pacienteId) {
        Users paciente = userRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return citaMedicaRepository.findByPacienteId(paciente.getId());
    }
}
