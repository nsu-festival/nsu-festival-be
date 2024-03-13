package com.example.nsu_festival.domain.user.repository;

import com.example.nsu_festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
