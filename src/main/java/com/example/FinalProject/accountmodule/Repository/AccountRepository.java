package com.example.FinalProject.accountmodule.Repository;

import com.example.FinalProject.usermodule.Entity.User;
import com.example.FinalProject.accountmodule.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
    List<Account> findByUser(User user);
}