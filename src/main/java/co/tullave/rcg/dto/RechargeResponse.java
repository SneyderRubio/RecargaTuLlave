package co.tullave.rcg.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.tullave.rcg.enums.PaymentMethod;


public record RechargeResponse(

        Long id,
        String cardNumber,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt

) {}