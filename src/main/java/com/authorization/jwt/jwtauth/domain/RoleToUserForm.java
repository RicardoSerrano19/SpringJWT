package com.authorization.jwt.jwtauth.domain;

import lombok.Data;

@Data
public class RoleToUserForm{
    private String username;
    private String roleName;
}
