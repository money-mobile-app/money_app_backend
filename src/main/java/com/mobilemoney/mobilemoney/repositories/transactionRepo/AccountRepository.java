package com.mobilemoney.mobilemoney.repositories.transactionRepo;

import com.mobilemoney.mobilemoney.models.AppUser;
import com.mobilemoney.mobilemoney.models.transaction.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByAppUser(AppUser user);
}
