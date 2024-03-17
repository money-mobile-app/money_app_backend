package com.mobilemoney.mobilemoney.repositories;

import com.mobilemoney.mobilemoney.models.AppUser;
import com.mobilemoney.mobilemoney.models.otp.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {

    Optional<Otp> findByToNumber(String phoneNumber);
}
