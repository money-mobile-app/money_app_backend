package com.mobilemoney.mobilemoney.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Builder
@Data
public class CashInCashOutDTO {
    String phone ;
    BigDecimal amount;
}
