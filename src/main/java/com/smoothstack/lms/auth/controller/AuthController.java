package com.smoothstack.lms.auth.controller;

import javax.servlet.ServletException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {

	@GetMapping(path = "/login")
	public ResponseEntity<User> test() {
		System.out.println("test");
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/login")
	public ResponseEntity<String> getToken(@RequestBody User user) throws ServletException {

		String jwttoken = "";

		// If the username and password fields are empty -> Throw an exception!
		if (user.getUsername().isEmpty() || user.getPassword().isEmpty())
			return new ResponseEntity<String>("Username or password cannot be empty.", HttpStatus.BAD_REQUEST);

		String name = user.getUsername();
		String password = user.getPassword();

		// If the username and password are not valid -> Thrown an invalid credentials
		// exception!
//		if (!(name.equalsIgnoreCase("Test") && password.equalsIgnoreCase("1234")))
//			return new ResponseEntity<String>("Invalid credentials. Please check the username and password.",
//					HttpStatus.UNAUTHORIZED);
//		else {
//			// Creating JWT using the user credentials.
//			Map<String, Object> claims = new HashMap<String, Object>();
//			claims.put("usr", login.getUsername());
//			claims.put("sub", "Authentication token");
//			claims.put("iss", Iconstants.ISSUER);
//			claims.put("rol", "Administrator, Developer");
//			claims.put("iat", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//			jwttoken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, Iconstants.SECRET_KEY)
//					.compact();
//			System.out.println("Returning the following token to the user= " + jwttoken);
//		}

		return new ResponseEntity<String>(jwttoken, HttpStatus.OK);
	}
}
