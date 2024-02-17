package com.mobilemoney.mobilemoney.controllers;

import com.mobilemoney.mobilemoney.config.otp.SendMessage;
import com.mobilemoney.mobilemoney.dto.JwtAuthenticationResponse;
import com.mobilemoney.mobilemoney.dto.SignInRequest;
import com.mobilemoney.mobilemoney.dto.SignUpRequest;
import com.mobilemoney.mobilemoney.repositories.UserRepository;
import com.mobilemoney.mobilemoney.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    SendMessage sendMessage= new SendMessage() ;
    @Autowired
    UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public String signup( @RequestBody SignUpRequest request) {
        String phone = request.getPhoneNumber();
        if (userRepository.findByPhoneNumber(phone).isPresent()){
            return "this phone number already have account";
        }else {
            sendMessage.sendMessage(phone);
             return authenticationService.signup(request).getToken();
        }
    }

    @PostMapping("/signing")
    public JwtAuthenticationResponse signing(@RequestBody SignInRequest request) {
        return authenticationService.signing(request);
    }

    @PostMapping("/otpCode")
    public String getOtpCode(@RequestBody String otp){
        if (sendMessage.validateOTP(otp)) {
           return "valid code";
        }else {
            return "invalid code";
        }
    }
}
