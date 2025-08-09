// package com.example.backend.url_shortener.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableMethodSecurity
// public class SecurityConfig {

//     private final JwtFilter jwtFilter;

//     public SecurityConfig(JwtFilter jwtFilter) {
//         this.jwtFilter = jwtFilter;
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         return http
//             // .cors(Customizer.withDefaults()) 
//             // .csrf(csrf -> csrf.disable())
//             .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/auth/**").permitAll()
//                 .requestMatchers("/api/analytics/**").authenticated()
//                 // .requestMatchers("/api/analytics/**").hasRole("ADMIN")
//                 .anyRequest().permitAll()
//             )
//             .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//             .build();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }
