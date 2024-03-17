package com.mobilemoney.mobilemoney.controllers;

import com.mobilemoney.mobilemoney.config.otp.SendMessage;
import com.mobilemoney.mobilemoney.dto.*;
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
    @Autowired
    SendMessage sendMessage;
    @Autowired
    UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup( @RequestBody SignUpRequest request) {
        String phone = request.getPhoneNumber();
//        sendMessage.sendMessage(phone);
        return authenticationService.signup(request);

    }

    @PostMapping("/signing")
    public JwtAuthenticationResponse signing(@RequestBody SignInRequest request) {
        return authenticationService.signing(request);
    }

    @PostMapping("/otpCode")
    public CodePinMessage getOtpCode(@RequestBody OtpValidation otpValidation){
        return  sendMessage.validateOTP(otpValidation);
    }

    @PostMapping("/validateNumberPhone-unique")
    public Boolean validateNumberPhone(@RequestBody PhoneNumber pn){
        String phoneNumber = pn.getPhoneNumber();
        boolean present = userRepository.findByPhoneNumber(phoneNumber).isPresent();

        return  !present;
    }
}

