package com.example.FinalProject.accountmodule.Controller;

import com.example.FinalProject.exception.AccessDeniedException;
import com.example.FinalProject.usermodule.Entity.User;
import com.example.FinalProject.accountmodule.Service.AccountService;
import com.example.FinalProject.accountmodule.Entity.Account;
import com.example.FinalProject.authenticationutility.AuthenticationUtility;
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

    @Autowired
    private AuthenticationUtility authenticationUtility;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account, HttpServletRequest request) {
        try {
            ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
            if (authResponse.getStatusCodeValue() != 200) {
                return authResponse;
            }

            User user = (User) authResponse.getBody();
            account.setUser(user);
            Account createdAccount = accountService.createAccount(user.getUsername(), account);
            return ResponseEntity.ok(createdAccount);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating account: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id, HttpServletRequest request) {
        /*try {*/
            ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
            if (authResponse.getStatusCodeValue() != 200) {
                return authResponse;
            }

            User user = (User) authResponse.getBody();
            Account account = accountService.getAccountById(id);
            if (!account.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied: Account does not belong to the authenticated user");
            }

            return ResponseEntity.ok(account);
       /* } catch (Exception e) {
            return ResponseEntity.status(404).body("Account not found: " + e.getMessage());
        }*/
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<?> getByAccountNumber(@PathVariable String accountNumber, HttpServletRequest request) {
        /*try {*/
            ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
            if (authResponse.getStatusCodeValue() != 200) {
                return authResponse;
            }

            User user = (User) authResponse.getBody();
            Account account = accountService.getByAccountNumber(accountNumber);
            if (!account.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied: Account does not belong to the authenticated user");
            }

            return ResponseEntity.ok(account);
        /*} catch (Exception e) {
            return ResponseEntity.status(404).body("Account not found: " + e.getMessage());
        }*/
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccounts(HttpServletRequest request) {
        /*try {*/
            ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
            if (authResponse.getStatusCodeValue() != 200) {
                return authResponse;
            }

            User user = (User) authResponse.getBody();
            List<Account> userAccounts = accountService.getAllAccountsByUser(user);
            return ResponseEntity.ok(userAccounts);

       /* } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving accounts: " + e.getMessage());
        }*/
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id, HttpServletRequest request) {
        /*try {*/
            ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
            if (authResponse.getStatusCodeValue() != 200) {
                return authResponse;
            }

            User user = (User) authResponse.getBody();
            Account account = accountService.getAccountById(id);
            if (!account.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access denied: Account does not belong to the authenticated user");
            }

            accountService.deleteAccount(id);
            return ResponseEntity.ok("Account deleted successfully");

        /*} catch (Exception e) {
            return ResponseEntity.status(404).body("Error deleting account: " + e.getMessage());
        }*/
    }
}
