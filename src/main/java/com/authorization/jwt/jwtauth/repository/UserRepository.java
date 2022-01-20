package com.authorization.jwt.jwtauth.repository;

import com.authorization.jwt.jwtauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
}
