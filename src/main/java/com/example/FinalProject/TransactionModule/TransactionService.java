package com.example.FinalProject.TransactionModule;

import com.example.FinalProject.AccountModule.Account;
import com.example.FinalProject.AccountModule.AccountRepository;
import com.example.FinalProject.Exception.AccountNotFoundException;
import com.example.FinalProject.Exception.InsufficientBalanceException;
import com.example.FinalProject.Exception.TransactionNotFoundException;
import com.example.FinalProject.UserModule.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Transaction deposit(Long accountId, Double amount, String description, User authenticatedUser) {
        try {
            Account account = getAccountAndVerifyOwnership(accountId, authenticatedUser);

            Transaction transaction = Transaction.builder().transactionType(TransactionType.DEPOSIT).amount(amount).description(description != null ? description : "Money Deposit").fromAccount(account).toAccount(null).balanceAfterTransaction(account.getBalance() + amount).status("SUCCESS").build();
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);

            return transactionRepository.save(transaction);

        } catch (Exception e) {
            Account account = accountRepository.findById(accountId).orElse(null);
            if (account != null) {
                Transaction failedTransaction = Transaction.builder().transactionType(TransactionType.DEPOSIT).build();
                transactionRepository.save(failedTransaction);
            }
            throw new RuntimeException("Deposit failed: " + e.getMessage());
        }
    }

    @Transactional
    public Transaction withdraw(Long accountId, Double amount, String description, User authenticatedUser) {
        try {
            Account account = getAccountAndVerifyOwnership(accountId, authenticatedUser);

            if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance. Available balance: " + account.getBalance());
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
            Account account = accountRepository.findById(accountId).orElse(null);
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

                transactionRepository.save(failedTransaction);
            }

            throw new RuntimeException("Withdrawal failed: " + e.getMessage());
        }
    }


    @Transactional
    public Transaction transfer(Long fromAccountId, Long toAccountId, String toAccountNumber, Double amount, String description, User authenticatedUser) {
        try {
            Account fromAccount = getAccountAndVerifyOwnership(fromAccountId, authenticatedUser);

            Account toAccount = null;
            if (toAccountId != null) {
                toAccount = accountRepository.findById(toAccountId)
                        .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));
            } else if (toAccountNumber != null) {
                toAccount = accountRepository.findByAccountNumber(toAccountNumber);
                if (toAccount == null) {
                    throw new AccountNotFoundException("Destination account not found with number: " + toAccountNumber);
                }
            } else {
                throw new RuntimeException("Either destination account ID or account number is required");
            }

            if (fromAccount.getBalance() < amount) {
                throw new InsufficientBalanceException("Insufficient balance. Available balance: " + fromAccount.getBalance());
            }

            Transaction transaction = Transaction.builder().transactionType(TransactionType.TRANSFER).amount(amount).description(description != null ? description : "Money Transfer to " + toAccount.getAccountNumber()).fromAccount(fromAccount).toAccount(toAccount).balanceAfterTransaction(fromAccount.getBalance() - amount).status("SUCCESS").build();
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            return transactionRepository.save(transaction);

        } catch (Exception e) {
            Account fromAccount = accountRepository.findById(fromAccountId).orElse(null);
            if (fromAccount != null) {
                Transaction failedTransaction = Transaction.builder().transactionType(TransactionType.TRANSFER).amount(amount).description("Failed: " + e.getMessage()).fromAccount(fromAccount).toAccount(null).balanceAfterTransaction(fromAccount.getBalance()).status("FAILED").build();
                transactionRepository.save(failedTransaction);
            }
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactionHistory(Long accountId, User authenticatedUser) {
        Account account = getAccountAndVerifyOwnership(accountId, authenticatedUser);
        return transactionRepository.findAllTransactionsByAccount(account);
    }

    public List<Transaction> getAllUserTransactions(User authenticatedUser) {
        List<Account> userAccounts = accountRepository.findByUser(authenticatedUser);
        return userAccounts.stream().flatMap(account -> transactionRepository.findAllTransactionsByAccount(account).stream()).sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate())).collect(java.util.stream.Collectors.toList());
    }

    public Transaction getTransactionById(Long id, User authenticatedUser) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + id));

        if (!transaction.getFromAccount().getUser().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("Access denied: Transaction does not belong to the authenticated user");
        }
        return transaction;
    }

    private Account getAccountAndVerifyOwnership(Long accountId, User authenticatedUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("Access denied: Account does not belong to the authenticated user");
        }

        return account;
    }
}