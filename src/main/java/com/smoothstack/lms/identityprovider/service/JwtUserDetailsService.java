package com.smoothstack.lms.identityprovider.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smoothstack.lms.identityprovider.model.User;
import com.smoothstack.lms.identityprovider.repository.RoleRepository;
import com.smoothstack.lms.identityprovider.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findOneByUsername(username);
		UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
		userBuilder.password(user.getPassword());
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getUserRoleSet().size());
		user.getUserRoleSet().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
		});
		userBuilder.authorities(authorities);
		return userBuilder.build();
	}

	public User loadByUsernameNoPassword(String username) {
		User user = userRepository.findOneByUsername(username);
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setUserId(user.getUserId());
		newUser.setUserRoleSet(user.getUserRoleSet());
		newUser.setPassword(null);
		return newUser;
	}

	@Transactional
	public void saveUser(User user) {
		user.getUserRoleSet().forEach(role -> {
			if (role != null && role.getRoleId() != 0 && !entityManager.contains(role)) {
				roleRepository.save(role);
			}
		});
		userRepository.save(user);
		user.getUserRoleSet().forEach(role -> {
			role.getRoleUserSet().add(user);
			role.getRoleUserSet().forEach(newUser -> {
			});
			roleRepository.save(role);
		});
	}

	@Transactional
	public void deleteUser(User user) {
		userRepository.delete(user);
	}

}