package com.smoothstack.lms.identityprovider.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smoothstack.lms.identityprovider.model.JwtRequest;
import com.smoothstack.lms.identityprovider.model.JwtResponse;
import com.smoothstack.lms.identityprovider.model.Role;
import com.smoothstack.lms.identityprovider.model.User;
import com.smoothstack.lms.identityprovider.service.JwtUserDetailsService;
import com.smoothstack.lms.identityprovider.service.RoleService;
import com.smoothstack.lms.identityprovider.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Autowired
	private RoleService roleService;

	@PostMapping(value = "/users")
	@Consumes({ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Void> registerUser(@Valid @RequestBody User newUser,
			@RequestHeader("Authorization") String requestTokenHeader) {
		try {
			if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
				return ResponseEntity.badRequest().build();
			}
			String jwt = requestTokenHeader.substring(7);
			String username = jwtTokenUtil.getUsernameFromToken(jwt);
			if (username == null) {
				return ResponseEntity.notFound().build();
			}
			User user = userDetailsService.loadByUsernameNoPassword(username);
			if (!jwtTokenUtil.validateToken(jwt, user)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			if (newUser.getUserRoleSet() == null || newUser.getUserRoleSet().isEmpty())
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

			if ((user.getUserRoleSet().contains(new Role("ADMIN")))
					|| (user.getUserRoleSet().contains(new Role("LIBRARIAN"))
							&& !newUser.getUserRoleSet().contains(new Role("ADMIN")))) {
				User tempUser = new User();
				tempUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
				tempUser.setUsername(newUser.getUsername());
				newUser.getUserRoleSet().forEach(role -> {
					if (role.getRoleId() != 0)
						tempUser.getUserRoleSet().add(roleService.findById(role.getRoleId()));
					else
						tempUser.getUserRoleSet().add(role);

				});
				userDetailsService.saveUser(tempUser);
				return ResponseEntity.status(HttpStatus.CREATED).build();
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (IllegalArgumentException | SignatureException | MalformedJwtException | ExpiredJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@DeleteMapping(value = "/users/{usernameToDelete}")
	public ResponseEntity<Void> deleteUser(@PathVariable String usernameToDelete,
			@RequestHeader("Authorization") String requestTokenHeader) {
		try {
			if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
				return ResponseEntity.badRequest().build();
			}
			String jwt = requestTokenHeader.substring(7);
			String username = jwtTokenUtil.getUsernameFromToken(jwt);
			if (username == null) {
				return ResponseEntity.notFound().build();
			}
			User user = userDetailsService.loadByUsernameNoPassword(username);
			if (!jwtTokenUtil.validateToken(jwt, user)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			if (user.getUserRoleSet().contains(new Role("Admin")) || username.equals(usernameToDelete)) {
				userDetailsService.deleteUser(userDetailsService.loadByUsernameNoPassword(usernameToDelete));
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (IllegalArgumentException | ExpiredJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping(value = "/login")
	@Consumes({ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (DisabledException | LockedException | BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		final UserDetails userDetails = userDetailsService
				.loadByUsernameNoPassword(authenticationRequest.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}

	@GetMapping(value = "/userdetails")
	@Produces({ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Object> getUser(HttpServletRequest request) {
		try {
			final String requestTokenHeader = request.getHeader("Proxy-Authorization");
			String jwt = null;
			String username = null;
			if (requestTokenHeader == null || !requestTokenHeader.startsWith("JWT ")) {
				return ResponseEntity.badRequest().build();
			}
			jwt = requestTokenHeader.substring(4);
			username = jwtTokenUtil.getUsernameFromToken(jwt);
			if (username == null) {
				return ResponseEntity.notFound().build();
			}
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (!jwtTokenUtil.validateToken(jwt, userDetails)) {
				return ResponseEntity.badRequest().build();
			}
			return ResponseEntity.ok(userDetails);
		} catch (IllegalArgumentException | SignatureException | MalformedJwtException | ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

}