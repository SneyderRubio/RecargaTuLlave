package co.tullave.rcg.dto;


import co.tullave.rcg.enums.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record RechargeRequest(

        @NotBlank(message = "Card number is required")
        @Pattern(
                regexp = "^[0-9]{16}$",
                message = "Card number must contain 16 digits"
        )
        String cardNumber,


        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "2000",
                message = "Minimum amount is 2000"
        )
        @DecimalMax(
                value = "200000",
                message = "Maximum amount is 200000"
        )
        BigDecimal amount,


        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod

){
}