package pe.edu.upc.trabajofinal.security.impl;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import pe.edu.upc.trabajofinal.security.model.UsernamePasswordAuthenticationTokenBuilder;
import pe.edu.upc.trabajofinal.security.interfaces.BearerTokenService;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	private final BearerTokenService tokenService;

	@Qualifier("defaultUserDetailsService")
	private final UserDetailsService userDetailsService;

	public JwtRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
		this.tokenService = tokenService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
									@NonNull FilterChain chain)
			throws ServletException, IOException {

		try {
			String token = tokenService.getBearerTokenFrom(request);
			log.info("Extracted Token: {}", token);
			if (token != null && tokenService.validateToken(token)) {
				String email = tokenService.getEmailFromToken(token);
				log.info("Extracted Email: {}", email);
				var userDetails = userDetailsService.loadUserByUsername(email);
				log.info("Loaded UserDetails: {}", userDetails);
				SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
				log.info("Authentication set for user: {}", email);
			} else {
				log.info("Token is not valid");
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication: {}", e.getMessage(), e);
		}
		chain.doFilter(request, response);
	}
}