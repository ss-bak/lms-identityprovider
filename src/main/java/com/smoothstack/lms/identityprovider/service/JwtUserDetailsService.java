package com.smoothstack.lms.identityprovider.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
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
	public User loadUserByUsername(String username) {
		System.out.println("This might be called?");
		return userRepository.findOneByUsername(username);
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