package com.authorization.jwt.jwtauth.service;

import java.util.List;
import com.authorization.jwt.jwtauth.domain.Role;
import com.authorization.jwt.jwtauth.domain.User;
import com.authorization.jwt.jwtauth.repository.RoleRepository;
import com.authorization.jwt.jwtauth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service 
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User saveUser(User user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Role saveRole(Role role) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public User getUser(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<User> getUsers() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
