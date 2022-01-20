package com.authorization.jwt.jwtauth.service;

import com.authorization.jwt.jwtauth.domain.User;
import java.util.List;
import com.authorization.jwt.jwtauth.domain.Role;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
