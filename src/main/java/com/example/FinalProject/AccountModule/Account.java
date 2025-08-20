package com.example.FinalProject.AccountModule;

import com.example.FinalProject.UserModule.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accId;

    @NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "\\d{10,12}", message = "Account number must be 10 to 12 digits")
    private String accountNumber;

    @NotBlank(message = "Email ID cannot be blank")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Enter valid email ID")
    private String emailId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Account type is required")
    private AccountTypeEnum accountType;

    @NotNull(message = "Balance is required")
    @Min(value = 0, message = "Balance must be non-negative")
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    @Valid
    private Address address;
}