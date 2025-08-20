package com.example.FinalProject.AccountModule;

import com.example.FinalProject.UserModule.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
    List<Account> findByUser(User user);
}