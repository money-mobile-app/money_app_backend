package com.mobilemoney.mobilemoney.models;

import com.mobilemoney.mobilemoney.models.transaction.Account;
import com.mobilemoney.mobilemoney.models.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name="users")
public class AppUser implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String firstName;
    String lastName;

    @Column(unique = true)
    String phoneNumber;

    String password;

    LocalDateTime createAt;
    LocalDateTime updateAt;

    @Enumerated(EnumType.STRING)
    Role role;

    // Composition relationship with Account
    // if the UserApp PERSIST then Account should be PERSISTED
    //think about this ALL Cascading
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL)
    private Account account;

    // Bidirectional relationship with Transaction
    @OneToMany(mappedBy = "sender",cascade = CascadeType.PERSIST)
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiver",cascade = CascadeType.PERSIST)
    private List<Transaction> receivedTransactions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
