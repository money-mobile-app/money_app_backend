package com.mobilemoney.mobilemoney.config.otp;

import com.mobilemoney.mobilemoney.dto.CodePinMessage;
import com.mobilemoney.mobilemoney.dto.OtpValidation;
import com.mobilemoney.mobilemoney.models.otp.Otp;
import com.mobilemoney.mobilemoney.repositories.OtpRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SendMessage {
    private   Otp otp;
    private final OtpRepository  otpRepository;


    public void sendMessage(String TO_NUMBER){
        String VONAGE_API_KEY="112e3deb";
        String VONAGE_API_SECRET="b0Y16U8z5oQci0uh";
        String VONAGE_BRAND_NAME = "Vonage APIs";
        VonageClient client = VonageClient.builder()
                .apiKey(VONAGE_API_KEY)
                .apiSecret(VONAGE_API_SECRET).build();

        generateOtp(TO_NUMBER);


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

    public void generateOtp(String to) {
        String pin = generateOtpCode();
        LocalDateTime now = LocalDateTime.now();
        // Add 5 minutes to the current date and time
       LocalDateTime expiredTime = now.plusMinutes(5);
        otp = Otp.builder()
               .pin(pin)
               .expiredTime(expiredTime)
               .toNumber(to)
               .build();

        Optional<Otp> byToNumber = otpRepository.findByToNumber(to);
        //is a code pin already exist for this user delete it to sure that exist one : the last
        byToNumber.ifPresent(otpRepository::delete);

        otpRepository.save(otp);

    }
    private String generateOtpCode(){
        return new DecimalFormat("0000")
                .format(new Random().nextInt(9999));
    }

//

    public CodePinMessage validateOTP(OtpValidation otpValidation){
        String phoneNumber = otpValidation.getPhoneNumber();
        if(!otpRepository.findByToNumber(phoneNumber).isPresent()){
            return CodePinMessage.builder()
                    .pinMessage("Incorrect PIN code")
                    .build();
        }
        Otp otpUser = otpRepository.findByToNumber(phoneNumber).get();
        //pin we store in the database
        String pin = otpUser.getPin();
        //pin send by  user
        String pinSendByUser = otpValidation.getCodePin();

        if(pin.equals(pinSendByUser)){
            if (LocalDateTime.now().isBefore(otpUser.getExpiredTime())){
                return CodePinMessage.builder()
                        .pinMessage("correct PIN code")
                        .build();
            }else {
                return CodePinMessage.builder()
                        .pinMessage("PIN code expired")
                        .build();
            }

        }else {
            return CodePinMessage.builder()
                    .pinMessage("Incorrect PIN code")
                    .build();
//            return "Incorrect PIN code";
        }
    }
}
