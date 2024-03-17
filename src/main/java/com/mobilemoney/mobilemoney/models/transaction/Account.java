package com.mobilemoney.mobilemoney.models.transaction;

import com.mobilemoney.mobilemoney.models.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    //bidirectional relationship with userApp why? if we have the user we can find the account
    // if we have the account we can find user
    // the owner of relationship (user account ) have the foreign key app_user_id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Composition relationship with AppUser
    //FK app_user_id
    @OneToOne
    @JoinColumn(name = "app_user_id", unique = true)
    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal balance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}