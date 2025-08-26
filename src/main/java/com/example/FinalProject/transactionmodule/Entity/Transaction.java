package com.example.FinalProject.transactionmodule.Entity;

import com.example.FinalProject.accountmodule.Entity.Account;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @NotNull(message = "From account is required")
    @ManyToOne
    @JoinColumn(name = "from_account_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account fromAccount;


    @ManyToOne
    @JoinColumn(name = "to_account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account toAccount;

    @Column(nullable = false)
    private Double balanceAfterTransaction;

    @Column(nullable = false)
    private String status;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
        if (status == null ) {
            status = "SUCCESS";
        }
    }
}
