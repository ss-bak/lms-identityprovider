package com.smoothstack.lms.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smoothstack.lms.auth.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
