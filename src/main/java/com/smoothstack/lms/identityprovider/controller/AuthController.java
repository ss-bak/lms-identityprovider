package com.smoothstack.lms.identityprovider.controller;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smoothstack.lms.identityprovider.model.JwtRequest;
import com.smoothstack.lms.identityprovider.model.JwtResponse;
import com.smoothstack.lms.identityprovider.model.User;
import com.smoothstack.lms.identityprovider.service.JwtUserDetailsService;
import com.smoothstack.lms.identityprovider.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

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

	@GetMapping(value = "/user")
	@Produces({ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Object> getUser(HttpServletRequest request) {
		try {
			final String requestTokenHeader = request.getHeader("Authorization");
			String jwt = null;
			String username = null;
			if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
				return ResponseEntity.badRequest().build();
			}
			jwt = requestTokenHeader.substring(7);
			username = jwtTokenUtil.getUsernameFromToken(jwt);
			if (username == null) {
				return ResponseEntity.notFound().build();
			}
			User user = userDetailsService.loadByUsernameNoPassword(username);
			if (!jwtTokenUtil.validateToken(jwt, user)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			return ResponseEntity.ok(user);
		} catch (IllegalArgumentException | ExpiredJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}