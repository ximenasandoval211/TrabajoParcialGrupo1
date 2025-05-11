package pe.edu.upc.trabajofinal.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.trabajofinal.dtos.AuthUserResponse;
import pe.edu.upc.trabajofinal.dtos.SignInDTO;
import pe.edu.upc.trabajofinal.dtos.AuthenticatedUserResourceFromEntityAssembler;
import pe.edu.upc.trabajofinal.servicesinterfaces.UserService;


@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	private final UserService userService;

	public AuthenticationController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/sign-in")
	public ResponseEntity<AuthUserResponse> signIn(@RequestBody SignInDTO signInDTO) {

        var authenticatedUser = userService.handle(signInDTO);

		if(authenticatedUser.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
		return ResponseEntity.ok(authenticatedUserResource);
	}
}