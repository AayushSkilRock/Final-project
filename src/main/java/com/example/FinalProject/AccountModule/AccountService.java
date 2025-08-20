package com.example.FinalProject.AccountModule;

import com.example.FinalProject.Exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    public Account getByAccountNumber(String number) {
        Account acc = accountRepository.findByAccountNumber(number);
        if (acc == null)
        {
            throw new AccountNotFoundException("Account not found with number: " + number);
        }
        return acc;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Cannot delete. Account not found with ID: " + id);
        }
        accountRepository.deleteById(id);
    }
}

