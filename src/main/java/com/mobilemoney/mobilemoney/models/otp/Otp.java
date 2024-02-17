package com.mobilemoney.mobilemoney.models.otp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="otpCode")
public class Otp {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private  String pin;
    private  LocalDateTime expiredTime;
    private String toNumber;



}

