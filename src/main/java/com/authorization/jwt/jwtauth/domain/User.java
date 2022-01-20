package com.authorization.jwt.jwtauth.domain;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

public class User {
    private Long id;
    private String name;
    private String username;
    private String password;
    private Collection<Role> roles = new ArrayList<>();
}
