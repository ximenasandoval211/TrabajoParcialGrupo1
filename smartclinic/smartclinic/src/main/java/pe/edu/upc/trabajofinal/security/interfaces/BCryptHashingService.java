package pe.edu.upc.trabajofinal.security.interfaces;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
