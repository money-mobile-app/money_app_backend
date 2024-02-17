package com.mobilemoney.mobilemoney.config.otp;

import com.mobilemoney.mobilemoney.models.otp.Otp;
import com.mobilemoney.mobilemoney.repositories.OtpRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SendMessage {
    Otp otp;
    OtpRepository otpRepository;
    String to;


    public void sendMessage(String TO_NUMBER){
         to = TO_NUMBER;
        String VONAGE_API_KEY="2597b26c";
        String VONAGE_API_SECRET="zucnQ7r30saBINq2";
        String VONAGE_BRAND_NAME = "Vonage APIs";
        VonageClient client = VonageClient.builder()
                .apiKey(VONAGE_API_KEY)
                .apiSecret(VONAGE_API_SECRET).build();

        generateOtp();


        TextMessage message = new TextMessage(VONAGE_BRAND_NAME,
                TO_NUMBER,otp.getPin()
        );

        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }

    public void generateOtp() {
        String pin = generateOtpCode();
        LocalDateTime now = LocalDateTime.now();
        // Add 5 minutes to the current date and time
       LocalDateTime expiredTime = now.plusMinutes(5);
        otp = Otp.builder()
               .pin(pin)
               .expiredTime(expiredTime)
               .toNumber(to)
               .build();
        otpRepository.save(otp);

    }
    private String generateOtpCode(){
        return new DecimalFormat("0000")
                .format(new Random().nextInt(9999));
    }

//

    public boolean validateOTP(String userPinCode){
        if(userPinCode.equals(otp.getPin())){
            if (otp.getExpiredTime().isBefore(LocalDateTime.now())){
                return true;
            }else {
                return false;
            }

        }else {
            return false;
        }
    }
}
