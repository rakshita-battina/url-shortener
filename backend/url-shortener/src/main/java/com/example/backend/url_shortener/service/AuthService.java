// package com.example.backend.url_shortener.service;

// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.example.backend.url_shortener.model.User;
// import com.example.backend.url_shortener.repository.UserRepository;

// @Service
// public class AuthService {
//     private final UserRepository userRepo;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtService jwtService;

//     public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
//         this.userRepo = userRepo;
//         this.passwordEncoder = passwordEncoder;
//         this.jwtService = jwtService;
//     }

//     public void register(String username, String password) {
//         if (userRepo.findByUsername(username).isPresent()) {
//             throw new RuntimeException("Username already taken");
//         }

//         User user = new User();
//         user.setUsername(username);
//         user.setPassword(passwordEncoder.encode(password));
//         user.setRole("USER");

//         userRepo.save(user);
//     }

//     public String authenticate(String username, String password) {
//     User user = userRepo.findByUsername(username)
//         .orElseThrow(() -> new RuntimeException("User not found"));

//     System.out.println("Incoming password: " + password);
//     System.out.println("Stored hash: " + user.getPassword());

//     boolean match = passwordEncoder.matches(password, user.getPassword());
//     System.out.println("Match? " + match);

//     if (!match) throw new RuntimeException("Invalid credentials");

//     return jwtService.generateToken(user.getUsername(), user.getRole());
// }

// }

