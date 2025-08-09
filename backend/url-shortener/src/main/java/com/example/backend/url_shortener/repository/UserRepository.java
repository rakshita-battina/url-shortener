package com.example.backend.url_shortener.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.url_shortener.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
