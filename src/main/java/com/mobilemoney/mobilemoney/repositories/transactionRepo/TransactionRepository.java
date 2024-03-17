package com.mobilemoney.mobilemoney.repositories.transactionRepo;

import com.mobilemoney.mobilemoney.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
