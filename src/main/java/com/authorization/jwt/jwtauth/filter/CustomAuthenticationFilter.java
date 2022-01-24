package com.authorization.jwt.jwtauth.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private final AuthenticationManager authenticationManager;

    @Autowired
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
       String username = request.getParameter("username");
       String password = request.getParameter("password");
       log.info("Username is : {} and password is: {}", username, password);
       UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
       return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        User user = (User)authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String accessToken = generateJTWToken(user, 12000, request.getRequestURL().toString(), "roles", algorithm);
        String refreshToken = generateJTWToken(user, 24000, request.getRequestURL().toString(), algorithm);
        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    private String generateJTWToken(User user, int milliseconds, String issuer, String claim, Algorithm algorithm){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + milliseconds))
                .withIssuer(issuer)
                .withClaim(claim, getClaimValues(user))
                .sign(algorithm);
    }

    private String generateJTWToken(User user, int milliseconds, String issuer, Algorithm algorithm){
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + milliseconds))
                .withIssuer(issuer)
                .sign(algorithm);
    }
    
    private List<String> getClaimValues(User user){
        return user.getAuthorities().stream()
            .map(role -> role.getAuthority())
            .collect(Collectors.toList());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        String username = request.getParameter("username");
        String message = failed.getMessage();
        Map<String, String> unsuccesfulMsg = new HashMap<>();
        unsuccesfulMsg.put("username", username);
        unsuccesfulMsg.put("message", message);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), unsuccesfulMsg);
    }
}
