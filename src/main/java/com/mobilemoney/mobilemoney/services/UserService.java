package com.mobilemoney.mobilemoney.services;

import com.mobilemoney.mobilemoney.models.AppUser;
import com.mobilemoney.mobilemoney.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByPhoneNumber(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public AppUser save(AppUser newUser){
        if(newUser.getId() == null){
            newUser.setCreateAt(LocalDateTime.now());
        }
        newUser.setUpdateAt(LocalDateTime.now());

            //TODO:check if the user exist
        return userRepository.save(newUser);
    }
}
