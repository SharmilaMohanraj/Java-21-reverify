package com.enterprise.hr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwt, ObjectMapper mapper) throws Exception {
  http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a
   .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll().anyRequest().authenticated())
   .exceptionHandling(e->e.authenticationEntryPoint((r,p,x)->write(mapper,p,401,"Unauthorized",x.getMessage(),r.getRequestURI())).accessDeniedHandler((r,p,x)->write(mapper,p,403,"Forbidden",x.getMessage(),r.getRequestURI())))
   .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
 private static void write(ObjectMapper mapper, HttpServletResponse response, int status, String error, String message, String path) throws java.io.IOException { response.setStatus(status); response.setContentType(MediaType.APPLICATION_JSON_VALUE); var body=new LinkedHashMap<String,Object>(); body.put("timestamp",Instant.now().toString()); body.put("status",status); body.put("error",error); body.put("message",message); body.put("path",path); mapper.writeValue(response.getOutputStream(),body); }
}
