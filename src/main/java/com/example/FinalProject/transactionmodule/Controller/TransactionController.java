package com.example.FinalProject.transactionmodule.Controller;

import com.example.FinalProject.transactionmodule.Entity.Transaction;
import com.example.FinalProject.transactionmodule.Service.TransactionService;
import com.example.FinalProject.accountmodule.Service.AccountService;
import com.example.FinalProject.authenticationutility.AuthenticationUtility;
import com.example.FinalProject.usermodule.Entity.User;
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

    @Autowired
    private AuthenticationUtility authenticationUtility;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestParam @NotNull(message = "Account ID is required") Long accountId,
            @RequestParam @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") Double amount,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {

        ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            Transaction transaction = transactionService.deposit(accountId, amount, description, user);
            return ResponseEntity.ok(transaction);

    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestParam @NotNull(message = "Account ID is required") Long accountId,
            @RequestParam @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") Double amount,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {

        ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            Transaction transaction = transactionService.withdraw(accountId, amount, description, user);
            return ResponseEntity.ok(transaction);

    }
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestParam @NotNull(message = "From Account ID is required") Long fromAccountId,
            @RequestParam(required = false) Long toAccountId,
            @RequestParam(required = false) String toAccountNumber,
            @RequestParam @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") Double amount,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {

        ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            Transaction transaction = transactionService.transfer(fromAccountId, toAccountId, toAccountNumber, amount, description, user);
            return ResponseEntity.ok(transaction);

    }
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long accountId, HttpServletRequest request) {
        ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            List<Transaction> transactions = transactionService.getTransactionHistory(accountId, user);
            return ResponseEntity.ok(transactions);

    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUserTransactions(HttpServletRequest request) {
        ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            List<Transaction> transactions = transactionService.getAllUserTransactions(user);
            return ResponseEntity.ok(transactions);

    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllTransactions(HttpServletRequest request) {
        ResponseEntity<?> authResponse = authenticationUtility.validateAdminUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            List<Transaction> allTransactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(allTransactions);

    }
    @GetMapping("/transaction/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<?> authResponse = authenticationUtility.validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }


            User user = (User) authResponse.getBody();
            Transaction transaction = transactionService.getTransactionById(id, user);
            return ResponseEntity.ok(transaction);

    }
}
