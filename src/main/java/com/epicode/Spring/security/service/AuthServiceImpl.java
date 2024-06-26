package com.epicode.Spring.security.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.epicode.Spring.security.entity.ERole;
import com.epicode.Spring.security.entity.Role;
import com.epicode.Spring.security.entity.User;
import com.epicode.Spring.security.exception.MyAPIException;
import com.epicode.Spring.security.exception.UserNotFoundException;
import com.epicode.Spring.security.payload.JWTAuthResponse;
import com.epicode.Spring.security.payload.LoginDto;
import com.epicode.Spring.security.payload.RegisterDto;
import com.epicode.Spring.security.payload.UpdateUserDto;
import com.epicode.Spring.security.repository.RoleRepository;
import com.epicode.Spring.security.repository.UserRepository;
import com.epicode.Spring.security.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private JwtTokenProvider jwtTokenProvider;

	public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public JWTAuthResponse login(LoginDto loginDto) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.generateToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();

		// Crea un nuovo oggetto JWTAuthResponse e imposta il token e l'ID dell'utente
		JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		jwtAuthResponse.setUsername(loginDto.getUsername());

		// Aggiungi l'ID dell'utente se lo hai disponibile

		return jwtAuthResponse;
	}

	@Override
	public String register(RegisterDto registerDto) {

		// add check for username exists in database
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			throw new MyAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
		}

		// add check for email exists in database
		if (userRepository.existsByEmail(registerDto.getEmail())) {
			throw new MyAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
		}

		User user = new User();
		user.setName(registerDto.getName());
		user.setUsername(registerDto.getUsername());
		user.setEmail(registerDto.getEmail());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

		Set<Role> roles = new HashSet<>();

		if (registerDto.getRoles() != null) {
			registerDto.getRoles().forEach(role -> {
				Role userRole = roleRepository.findByRoleName(getRole(role)).get();
				roles.add(userRole);
			});
		} else {
			Role userRole = roleRepository.findByRoleName(ERole.ROLE_USER).get();
			roles.add(userRole);
		}

		user.setRoles(roles);
		System.out.println(user);
		userRepository.save(user);

		return "User registered successfully!.";
	}

	@Override
	public String updateUser(String username, UpdateUserDto updateUserDto) {
		Optional<User> userOptional = userRepository.findByUsername(username); // Cerca l'utente per username
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setName(updateUserDto.getName());
			user.setUsername(updateUserDto.getUsername());
			user.setEmail(updateUserDto.getEmail());
			user.setPassword(passwordEncoder.encode(updateUserDto.getPassword())); // Codifica la nuova password

			userRepository.save(user);

			return "User updated successfully!";
		} else {
			throw new UserNotFoundException(username);
		}
	}

	public ERole getRole(String role) {
		if (role.equals("ADMIN"))
			return ERole.ROLE_ADMIN;
		else if (role.equals("MODERATOR"))
			return ERole.ROLE_MODERATOR;
		else
			return ERole.ROLE_USER;
	}

}
