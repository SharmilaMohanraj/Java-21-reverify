package com.enterprise.hr.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwt; public JwtAuthenticationFilter(JwtService jwt) { this.jwt=jwt; }
 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
  String header=request.getHeader(HttpHeaders.AUTHORIZATION);
  if(header!=null && header.startsWith("Bearer ")) try {
   String token=header.substring(7); String subject=jwt.subject(token); String role=jwt.role(token);
   if (role != null && (role.equals("ROLE_HR") || role.equals("ROLE_MANAGER") || role.equals("ROLE_EMPLOYEE"))) {
    var authentication=new UsernamePasswordAuthenticationToken(subject, null, List.of(new SimpleGrantedAuthority(role)));
    SecurityContextHolder.getContext().setAuthentication(authentication);
   }
  } catch (JwtException | IllegalArgumentException ignored) { SecurityContextHolder.clearContext(); }
  chain.doFilter(request,response);
 }
}
