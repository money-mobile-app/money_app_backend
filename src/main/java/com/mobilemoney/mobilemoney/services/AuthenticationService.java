package com.mobilemoney.mobilemoney.services;

import com.mobilemoney.mobilemoney.dto.JwtAuthenticationResponse;
import com.mobilemoney.mobilemoney.dto.SignInRequest;
import com.mobilemoney.mobilemoney.dto.SignUpRequest;
import com.mobilemoney.mobilemoney.models.AppUser;
import com.mobilemoney.mobilemoney.models.Role;
import com.mobilemoney.mobilemoney.models.transaction.Account;
import com.mobilemoney.mobilemoney.models.transaction.Currency;
import com.mobilemoney.mobilemoney.repositories.UserRepository;
import com.mobilemoney.mobilemoney.repositories.transactionRepo.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final AccountRepository accountRepository;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = AppUser
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();



        user = userService.save(user);
        createAccount(user);

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    public JwtAuthenticationResponse signing(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));
        var user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public void createAccount(AppUser user){
        var account = Account
                .builder()
                .balance(BigDecimal.valueOf(0))
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .appUser(user)
                .build();

        accountRepository.save(account);
    }
}
