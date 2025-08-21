package com.example.FinalProject.TransactionModule;

import com.example.FinalProject.AccountModule.AccountService;
import com.example.FinalProject.Security.JwtUtil;
import com.example.FinalProject.UserModule.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestParam @NotNull(message = "Account ID is required") Long accountId,
            @RequestParam @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") Double amount,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = JwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            Transaction transaction = transactionService.deposit(accountId, amount, description, user);
            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error in deposit: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestParam @NotNull(message = "Account ID is required") Long accountId,
            @RequestParam @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") Double amount,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = JwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            Transaction transaction = transactionService.withdraw(accountId, amount, description, user);
            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error in withdrawal: " + e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestParam @NotNull(message = "From Account ID is required") Long fromAccountId,
            @RequestParam(required = false) Long toAccountId,
            @RequestParam(required = false) String toAccountNumber,
            @RequestParam @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") Double amount,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = JwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            Transaction transaction = transactionService.transfer(fromAccountId, toAccountId, toAccountNumber, amount, description, user);
            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error in transfer: " + e.getMessage());
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long accountId, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = JwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            List<Transaction> transactions = transactionService.getTransactionHistory(accountId, user);
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error retrieving transaction history: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUserTransactions(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = JwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            List<Transaction> transactions = transactionService.getAllUserTransactions(user);
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving transactions: " + e.getMessage());
        }
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!JwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = JwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            Transaction transaction = transactionService.getTransactionById(id, user);
            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            return ResponseEntity.status(404).body("Transaction not found: " + e.getMessage());
        }
    }
}
