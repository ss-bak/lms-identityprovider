package com.smoothstack.lms.identityprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smoothstack.lms.identityprovider.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
