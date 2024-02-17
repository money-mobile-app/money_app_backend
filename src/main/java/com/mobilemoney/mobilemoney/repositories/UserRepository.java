package com.mobilemoney.mobilemoney.repositories;

import com.mobilemoney.mobilemoney.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser,Long> {
  Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
