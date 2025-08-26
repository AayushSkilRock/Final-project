package com.example.FinalProject.transactionmodule.Service;

import com.example.FinalProject.exception.AccessDeniedException;
import com.example.FinalProject.transactionmodule.Entity.Transaction;
import com.example.FinalProject.transactionmodule.Repository.TransactionRepository;
import com.example.FinalProject.transactionmodule.Entity.TransactionType;
import com.example.FinalProject.accountmodule.Entity.Account;
import com.example.FinalProject.accountmodule.Repository.AccountRepository;
import com.example.FinalProject.exception.AccountNotFoundException;
import com.example.FinalProject.exception.InsufficientBalanceException;
import com.example.FinalProject.usermodule.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Transaction deposit(Long accountId, Double amount, String description, User authenticatedUser) {
        Account account = null;
        try {
            account = getAccountAndVerifyOwnership(accountId, authenticatedUser);
            Transaction transaction = Transaction.builder()
                    .transactionType(TransactionType.DEPOSIT)
                    .amount(amount)
                    .description(description != null ? description : "Money Deposit")
                    .fromAccount(account)
                    .toAccount(null)
                    .balanceAfterTransaction(account.getBalance() + amount)
                    .status("SUCCESS")
                    .build();

            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);
           return transactionRepository.save(transaction);



        } catch (Exception e) {
            if (account != null) {
                Transaction failedTransaction = Transaction.builder()
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(amount)
                        .description("Failed: " + e.getMessage())
                        .fromAccount(account)
                        .toAccount(null)
                        .balanceAfterTransaction(account != null ? account.getBalance() : 0)
                        .status("FAILED")
                        .build();

               return transactionRepository.save(failedTransaction);
            }

            throw e;

        }

    }

    @Transactional
    public Transaction withdraw(Long accountId, Double amount, String description, User authenticatedUser) {
        Account account = null;
        try {
            account = getAccountAndVerifyOwnership(accountId, authenticatedUser);

            if (account.getBalance() < amount) {
                throw new InsufficientBalanceException("Insufficient balance. Available balance: " + account.getBalance());
            }

            double updatedBalance = account.getBalance() - amount;

            Transaction transaction = Transaction.builder()
                    .transactionType(TransactionType.WITHDRAW)
                    .amount(amount)
                    .description(description != null ? description : "Money Withdrawal")
                    .fromAccount(account)
                    .toAccount(null)
                    .balanceAfterTransaction(updatedBalance)
                    .status("SUCCESS")
                    .build();

            account.setBalance(updatedBalance);
            accountRepository.save(account);
            return transactionRepository.save(transaction);


        } catch (Exception e) {
            if (account != null) {

                Transaction failedTransaction = Transaction.builder()
                        .transactionType(TransactionType.WITHDRAW)
                        .amount(amount)
                        .description("Failed: " + e.getMessage())
                        .fromAccount(account)
                        .toAccount(null)
                        .balanceAfterTransaction(account.getBalance())
                        .status("FAILED")
                        .build();

               return transactionRepository.save(failedTransaction);
            }

            throw e;
        }
    }

    @Transactional
    public Transaction transfer(Long fromAccountId, Long toAccountId, String toAccountNumber, Double amount, String description, User authenticatedUser) {
        Account fromAccount = null;
        Account toAccount = null;

        try {
            fromAccount = getAccountAndVerifyOwnership(fromAccountId, authenticatedUser);

            if (toAccountId != null) {
                toAccount = accountRepository.findById(toAccountId)
                        .orElseThrow(() -> new AccountNotFoundException("Destination account not found with ID: " + toAccountId));
            } else if (toAccountNumber != null) {
                toAccount = accountRepository.findByAccountNumber(toAccountNumber);
                if (toAccount == null) {
                    throw new AccountNotFoundException("Destination account not found with number: " + toAccountNumber);
                }
            } else {
                throw new IllegalArgumentException("Either destination account ID or account number is required");
            }

            if (fromAccount.getBalance() < amount) {
                throw new InsufficientBalanceException("Insufficient balance. Available: " + fromAccount.getBalance());
            }

            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            Transaction transaction = Transaction.builder()
                    .transactionType(TransactionType.TRANSFER)
                    .amount(amount)
                    .description(description != null ? description : "Transfer to " + toAccount.getAccountNumber())
                    .fromAccount(fromAccount)
                    .toAccount(toAccount)
                    .balanceAfterTransaction(fromAccount.getBalance())
                    .status("SUCCESS")
                    .build();

            return transactionRepository.save(transaction);

        } catch (Exception e) {
            if (fromAccount != null) {
                Transaction failedTransaction = Transaction.builder()
                        .transactionType(TransactionType.TRANSFER)
                        .amount(amount)
                        .description("Failed: " + e.getMessage())
                        .fromAccount(fromAccount)
                        .toAccount(toAccount)
                        .balanceAfterTransaction(fromAccount.getBalance())
                        .status("FAILED")
                        .build();

                return transactionRepository.save(failedTransaction);
            }

            throw e;
        }
    }
    private Account getAccountAndVerifyOwnership(Long accountId, User authenticatedUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Access denied: Account does not belong to the authenticated user");
        }

        return account;
    }
    public List<Transaction> getTransactionHistory(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: Account does not belong to the authenticated user");
        }

        return transactionRepository.findAllTransactionsByAccount(account);
    }

    public List<Transaction> getAllUserTransactions(User user) {
        List<Account> accounts = accountRepository.findByUser(user);
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account account : accounts) {
            allTransactions.addAll(transactionRepository.findAllTransactionsByAccount(account));
        }

        return allTransactions;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getFromAccount().getUser().getId().equals(user.getId()) &&
                (transaction.getToAccount() == null || !transaction.getToAccount().getUser().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access denied: Transaction does not belong to the authenticated user");
        }

        return transaction;
    }
}
