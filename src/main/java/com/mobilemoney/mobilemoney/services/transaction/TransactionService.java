package com.mobilemoney.mobilemoney.services.transaction;

import com.mobilemoney.mobilemoney.dto.transaction.CashInCashOutDTO;
import com.mobilemoney.mobilemoney.dto.transaction.TransferRequestDTO;
import com.mobilemoney.mobilemoney.models.AppUser;
import com.mobilemoney.mobilemoney.models.transaction.Account;
import com.mobilemoney.mobilemoney.models.transaction.Currency;
import com.mobilemoney.mobilemoney.models.transaction.Transaction;
import com.mobilemoney.mobilemoney.models.transaction.TransactionType;
import com.mobilemoney.mobilemoney.repositories.UserRepository;
import com.mobilemoney.mobilemoney.repositories.transactionRepo.AccountRepository;
import com.mobilemoney.mobilemoney.repositories.transactionRepo.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransactionService {
    private  final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public String chargeAccount(CashInCashOutDTO chargeMyAccountDto) {
        try {
            BigDecimal amount = chargeMyAccountDto.getAmount();
            String phoneNumber = chargeMyAccountDto.getPhone();
            AppUser user = userRepository.findByPhoneNumber(phoneNumber).get();
            Account account = accountRepository.findByAppUser(user).get();
            BigDecimal balance = account.getBalance();
            BigDecimal newBalance = balance.add(amount);
            account.setUpdatedAt(LocalDateTime.now());
            account.setBalance(newBalance);
            createTransaction(user,user,amount,TransactionType.CHARGE);
            accountRepository.save(account);
            return "successfully";
        }catch (TransactionException ex){
            return "transaction exception";
        }


    }
    @Transactional
    public String transferMoney(String phoneSender,String phoneReceiver,BigDecimal amount){
        try {
            AppUser sender = userRepository.findByPhoneNumber(phoneSender).get();
            AppUser receiver = userRepository.findByPhoneNumber(phoneReceiver).get();
            Account accountSender = accountRepository.findByAppUser(sender).get();
            Account accountReceiver = accountRepository.findByAppUser(receiver).get();
            BigDecimal senderActualAmount = accountSender.getBalance();
            if(senderActualAmount.compareTo(amount) >=0 ){
                BigDecimal senderNewAmount = accountSender.getBalance().subtract(amount);
                BigDecimal receiverNewAmount = accountReceiver.getBalance().add(amount);

                accountSender.setUpdatedAt(LocalDateTime.now());
                accountSender.setBalance(senderNewAmount);
                accountRepository.save(accountSender);

                accountReceiver.setUpdatedAt(LocalDateTime.now());
                accountReceiver.setBalance(receiverNewAmount);
                accountRepository.save(accountReceiver);

                TransactionType transactionType = findTransactionType(sender, receiver, senderNewAmount, senderActualAmount);
                createTransaction(sender,receiver,amount,transactionType);
                return "successfully";
            }else {
                return "balance not enough";
            }
        }catch (TransactionException ex){
                return "transaction Exception";
        }


    }

    public TransactionType findTransactionType(AppUser sender,AppUser receiver,BigDecimal senderNewAmount,BigDecimal senderActualAmount){
        TransactionType transactionType;
        Long senderId = sender.getId();
        Long receiverId = receiver.getId();
        if(senderId.equals(receiverId)){
            int i = senderNewAmount.compareTo(senderActualAmount);
            if(i > 0){
                transactionType = TransactionType.CHARGE;
            }else {
                transactionType = TransactionType.WITHDRAW;
            }
        }else {
            transactionType = TransactionType.SEND;
        }
        return transactionType;
    }

    public void createTransaction(AppUser sender,AppUser receiver,BigDecimal amount,TransactionType transactionType){

        Transaction transaction = Transaction.builder()
                .currency(Currency.USD)
                .transactionType(transactionType)
                .date(LocalDateTime.now())
                .amount(amount)
                .sender(sender)
                .receiver(receiver)
                .build();

        transactionRepository.save(transaction);
    }
@Transactional
    public String cashOut(CashInCashOutDTO cashOutDto) {
        try{
            BigDecimal amount = cashOutDto.getAmount();
            String phoneNumber = cashOutDto.getPhone();
            AppUser user = userRepository.findByPhoneNumber(phoneNumber).get();
            Account account = accountRepository.findByAppUser(user).get();
            BigDecimal userActualAmount = account.getBalance();
            if(userActualAmount.compareTo(amount) >= 0){
                BigDecimal newBalance = userActualAmount.subtract(amount);
                account.setUpdatedAt(LocalDateTime.now());
                account.setBalance(newBalance);
                accountRepository.save(account);
                createTransaction(user,user,amount,TransactionType.WITHDRAW);
                return "successfully";
            }else {
                return "balance not enough";
            }

        }catch (TransactionException ex){
            return "transaction exception";
        }

    }

    @Transactional
    public void sendRequest(TransferRequestDTO transferRequestDTO){
        String phoneSender = transferRequestDTO.getSenderPhone();
        String phoneReceiver = transferRequestDTO.getReceiverPhone();
        BigDecimal amount = transferRequestDTO.getAmount();
        AppUser sender = userRepository.findByPhoneNumber(phoneSender).get();
        AppUser receiver = userRepository.findByPhoneNumber(phoneReceiver).get();
        createTransaction(sender,receiver,amount,TransactionType.REQUEST);
        //TODO send notification to receiver

    }
}
