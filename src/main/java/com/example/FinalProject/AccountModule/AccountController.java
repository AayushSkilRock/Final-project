package com.example.FinalProject.AccountModule;

import com.example.FinalProject.Security.JwtUtil;
import com.example.FinalProject.UserModule.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@Validated
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account, HttpServletRequest request) {
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

            account.setUser(user);
            Account createdAccount = accountService.createAccount(username, account);
            return ResponseEntity.ok(createdAccount);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating account: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id, HttpServletRequest request) {
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

            Account account = accountService.getAccountById(id);

            if (!account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Access denied: Account does not belong to the authenticated user");
            }

            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Account not found: " + e.getMessage());
        }
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<?> getByAccountNumber(@PathVariable String accountNumber, HttpServletRequest request) {
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

            Account account = accountService.getByAccountNumber(accountNumber);
            if (!account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Access denied: Account does not belong to the authenticated user");
            }

            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Account not found: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccounts(HttpServletRequest request) {
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

            List<Account> userAccounts = accountService.getAllAccountsByUser(user);
            return ResponseEntity.ok(userAccounts);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving accounts: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, HttpServletRequest request) {
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

            Account account = accountService.getAccountById(id);
            if (!account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Access denied: Cannot delete account that does not belong to you");
            }

            accountService.deleteAccount(id);
            return ResponseEntity.ok("Account deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error deleting account: " + e.getMessage());
        }
    }
}