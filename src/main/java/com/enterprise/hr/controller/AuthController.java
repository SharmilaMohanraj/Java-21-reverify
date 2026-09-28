package com.enterprise.hr.controller;
import com.enterprise.hr.dto.AuthDtos.*;
import com.enterprise.hr.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController { private final AuthService service; public AuthController(AuthService s) { service=s; }
 @PostMapping("/login") public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) { return ResponseEntity.ok(service.login(request)); }
}
