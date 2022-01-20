package com.authorization.jwt.jwtauth.repository;

import com.authorization.jwt.jwtauth.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByName(String name);
}
