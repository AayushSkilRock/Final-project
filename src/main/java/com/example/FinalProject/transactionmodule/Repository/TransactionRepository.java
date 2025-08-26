package com.example.FinalProject.transactionmodule.Repository;

import com.example.FinalProject.transactionmodule.Entity.Transaction;
import com.example.FinalProject.transactionmodule.Entity.TransactionType;
import com.example.FinalProject.accountmodule.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account ORDER BY t.transactionDate DESC")
    List<Transaction> findAllTransactionsByAccount(@Param("account") Account account);
    List<Transaction> findByFromAccountOrderByTransactionDateDesc(Account fromAccount);
    List<Transaction> findByFromAccountAndTransactionTypeOrderByTransactionDateDesc(Account fromAccount, TransactionType transactionType);
    List<Transaction> findByStatusOrderByTransactionDateDesc(String status);
}