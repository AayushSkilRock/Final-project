package com.example.FinalProject.usermodule.Repository;

import com.example.FinalProject.usermodule.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

