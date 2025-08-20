package com.example.FinalProject.AccountModule;

import com.example.FinalProject.Exception.AccountNotFoundException;
import com.example.FinalProject.UserModule.User;
import com.example.FinalProject.UserModule.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Account createAccount(String username, Account account) {
        if (account.getUser() == null) {
            User user = findUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }
            account.setUser(user);
        }
        return accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    public Account getByAccountNumber(String number) {
        Account acc = accountRepository.findByAccountNumber(number);
        if (acc == null) {
            throw new AccountNotFoundException("Account not found with number: " + number);
        }
        return acc;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // New method to get accounts by specific user
    public List<Account> getAllAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Cannot delete. Account not found with ID: " + id);
        }
        accountRepository.deleteById(id);
    }

    public User findUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElse(null);
    }
}