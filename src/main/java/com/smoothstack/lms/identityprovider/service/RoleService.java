package com.smoothstack.lms.identityprovider.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smoothstack.lms.identityprovider.model.Role;
import com.smoothstack.lms.identityprovider.repository.RoleRepository;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;
	
	public Role findById(Long id) {
		Optional<Role> role = roleRepository.findById(id);
		if (role.isPresent())
			return role.get();
		return null;
	}
}
