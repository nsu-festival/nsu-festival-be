package com.example.nsu_festival.domain.user.repository;

import com.example.nsu_festival.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
  
    Optional<User> findByNickName(String nickName);

    @Query("select u.role from User u where u.email = :email")
    String findRoleByEmail(String email);
}
