package com.authorization.jwt.jwtauth.controller;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.authorization.jwt.jwtauth.domain.Role;
import com.authorization.jwt.jwtauth.domain.RoleToUserForm;
import com.authorization.jwt.jwtauth.domain.User;
import com.authorization.jwt.jwtauth.service.UserService;
import com.authorization.jwt.jwtauth.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username){
        return ResponseEntity.ok().body(userService.getUser(username));
    }

    @PostMapping("/users")
    public ResponseEntity<User> saveUser(@RequestBody User user){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/roles").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/users/roles")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if(!(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))){
            throw new RuntimeException("Refresh token is missing");
        } 

        try{
            String refreshToken = authorizationHeader.substring("Bearer ".length());
            DecodedJWT decodedJWT = JwtUtils.decode(refreshToken);
            String username = decodedJWT.getSubject();
            User user = userService.getUser(username);
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            String accessToken = generateJTWToken(user, 300000, request.getRequestURL().toString(), "roles", algorithm);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return ResponseEntity.ok(tokens);   
        }catch(Exception ex){
            response.setHeader("error", ex.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", ex.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            return ResponseEntity.badRequest().body(error);   
        }
    }

    private String generateJTWToken(User user, int milliseconds, String issuer, String claim, Algorithm algorithm){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + milliseconds))
                .withIssuer(issuer)
                .withClaim(claim, getClaimValues(user))
                .sign(algorithm);
    }

    private List<String> getClaimValues(User user){
        return user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toList());
    }
}
