package com.authorization.jwt.jwtauth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.authorization.jwt.jwtauth.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if(request.getServletPath().equals("/login")){
            filterChain.doFilter(request, response);
            return;
        } 
            
        
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if(!(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))){
            filterChain.doFilter(request, response);
            return;
        } 

        try{
            String token = authorizationHeader.substring("Bearer ".length());
            DecodedJWT jwt = JwtUtils.decode(token);
            UsernamePasswordAuthenticationToken authenticationToken = JwtUtils.generateAuthenticationToken(jwt);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
            filterChain.doFilter(request, response);
           
            }catch(Exception ex){
                log.error("Error logging in", ex.getMessage());
                response.setHeader("error", ex.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", ex.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
    }
    
}
