package com.epicode.Spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epicode.Spring.security.entity.ERole;
import com.epicode.Spring.security.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
	Optional<Role> findByRoleName(ERole roleName);

}
