package com.smoothstack.lms.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.smoothstack.lms.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	public UserDetails findOneByUsername(String username);

}
