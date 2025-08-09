// package com.example.backend.url_shortener.config;

// import java.util.List;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.example.backend.url_shortener.repository.UserRepository;
// import com.example.backend.url_shortener.service.JwtService;

// import io.jsonwebtoken.io.IOException;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class JwtFilter extends OncePerRequestFilter {
//     private final JwtService jwtService;
//     private final UserRepository userRepo;

//     public JwtFilter(JwtService jwtService, UserRepository userRepo) {
//         this.jwtService = jwtService;
//         this.userRepo = userRepo;
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//             throws ServletException, IOException, java.io.IOException {

//         String authHeader = request.getHeader("Authorization");
//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             String token = authHeader.substring(7);
//             try {
//                 String username = jwtService.extractUsername(token);
//                 String role = jwtService.extractRole(token);

//                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                         username, null,
//                         List.of(new SimpleGrantedAuthority("ROLE_" + role))
//                 );

//                 SecurityContextHolder.getContext().setAuthentication(auth);
//             } catch (Exception ex) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 return;
//             }
//         }

//         chain.doFilter(request, response);
//     }
// }

