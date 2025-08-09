// package com.example.backend.url_shortener.controller;

// import java.util.Map;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.backend.url_shortener.dto.AuthRequest;
// import com.example.backend.url_shortener.service.AuthService;

// @RestController
// @RequestMapping("/auth")
// public class AuthController {
//     private final AuthService authService;

//     public AuthController(AuthService authService) {
//         this.authService = authService;
//     }

//     @PostMapping("/register")
//     public ResponseEntity<?> register(@RequestBody AuthRequest req) {
//         authService.register(req.getUsername(), req.getPassword());
//         return ResponseEntity.ok(Map.of("message", "Registered successfully"));
//     }

//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody AuthRequest req) {
//         String token = authService.authenticate(req.getUsername(), req.getPassword());
//         return ResponseEntity.ok(Map.of("token", token));
//     }
// }

