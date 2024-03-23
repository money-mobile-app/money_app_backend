package com.mobilemoney.mobilemoney.controllers.transaction;

import com.mobilemoney.mobilemoney.dto.transaction.CashInCashOutDTO;
import com.mobilemoney.mobilemoney.dto.transaction.TransferRequestDTO;
import com.mobilemoney.mobilemoney.repositories.UserRepository;
import com.mobilemoney.mobilemoney.services.transaction.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping("api/transaction")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    @PostMapping("/charge-my-account")
    public ResponseEntity<String> cashIn(@RequestBody CashInCashOutDTO cashIn){
     return transactionService.chargeAccount(cashIn);
    }

    @PostMapping("/cash-out")
    public ResponseEntity<String> cashOut(@RequestBody CashInCashOutDTO cashOutDto){
        return transactionService.cashOut(cashOutDto);
    }


    @PostMapping("/choose-receiver")
    public boolean chooseReceiver(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    //TODO return type responseEntity
    @PostMapping("/send-money")
    public ResponseEntity<String> send(@RequestBody TransferRequestDTO senderReceiver){
        String receiverPhone = senderReceiver.getReceiverPhone();
        String senderPhone = senderReceiver.getSenderPhone();
        BigDecimal amount = senderReceiver.getAmount();
        return transactionService.transferMoney(senderPhone,receiverPhone,amount);
    }

    @PostMapping("/send-request-notification")
    public String request(@RequestBody TransferRequestDTO request){
        String receiverPhone = request.getReceiverPhone();
        String senderPhone = request.getSenderPhone();
        BigDecimal amount = request.getAmount();
        return "send notification to user";
    }
}
