package com.epicode.Spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epicode.Spring.security.payload.JWTAuthResponse;
import com.epicode.Spring.security.payload.LoginDto;
import com.epicode.Spring.security.payload.RegisterDto;
import com.epicode.Spring.security.payload.UpdateUserDto;
import com.epicode.Spring.security.repository.UserRepository;
import com.epicode.Spring.security.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// Build Login REST API
	@PostMapping(value = { "/login", "/signin" })
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto) {

		JWTAuthResponse jwtAuthResponse = authService.login(loginDto);

		return ResponseEntity.ok(jwtAuthResponse);
	}

	// Build Register REST API
	@PostMapping(value = { "/register", "/signup" })
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
		String response = authService.register(registerDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{username}")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<String> updateUser(@RequestBody UpdateUserDto updateUserDto, @PathVariable String username) {
		String result = authService.updateUser(username, updateUserDto);
		return ResponseEntity.ok(result);
	}

	// JSON inviato dal Client
	/*
	 * { "name": "Giuseppe", "lastname": "Verdi", "username": "giuseppevardi",
	 * "email": "g.verdi@example.com", "password": "qwerty", "roles": ["MODERATOR",
	 * "ADMIN"] }
	 */
}
