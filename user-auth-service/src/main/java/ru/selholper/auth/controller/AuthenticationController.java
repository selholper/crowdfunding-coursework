package ru.selholper.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.selholper.auth.dto.UserDTO;
import ru.selholper.auth.model.User;
import ru.selholper.auth.model.repository.UserRepository;
import ru.selholper.auth.model.service.CustomUserDetailsService;
import ru.selholper.auth.request.LoginRequest;
import ru.selholper.auth.request.RegisterRequest;
import ru.selholper.auth.response.LoginResponse;
import ru.selholper.auth.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

	@Autowired
	private final AuthenticationManager authenticationManager;

	@Autowired
	private final CustomUserDetailsService userDetailsService;

	@Autowired
	private final JwtUtil jwtUtil;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final ObjectMapper objectMapper;

	@Autowired
	public AuthenticationController(AuthenticationManager authenticationManager,
			CustomUserDetailsService userDetailsService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
			UserRepository userRepository, ObjectMapper objectMapper) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody String rawBody) throws Exception {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        LoginRequest loginRequest = mapper.readValue(rawBody, LoginRequest.class);
	        authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));	 
	        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
			final String token = jwtUtil.generateToken(userDetails);
			return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername()));
	    } catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		} catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody String rawBody) throws Exception {
		RegisterRequest registerRequest = objectMapper.readValue(rawBody, RegisterRequest.class);
		// Check if user already exists
		if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("User already exists");
		}
		if (userRepository.findByEmail(registerRequest.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("Email already exists");
		}

		// Create new user
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEmail(registerRequest.getEmail());
		userDetailsService.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	@GetMapping("/user")
	public ResponseEntity<?> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("User not found"));

			UserDTO userDTO = UserDTO.fromEntity(user);
			return ResponseEntity.ok(userDTO);
		}

		return ResponseEntity.status(401).body("User not authenticated");
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(Authentication authentication) {
		// Optionally, you can log the logout action
		if (authentication != null) {
			String username = authentication.getName();
			log.info("Logging out user: {}", username);
		}

		// Clear the security context
		SecurityContextHolder.clearContext();

		return ResponseEntity.ok().body("Logged out successfully");
	}
}
