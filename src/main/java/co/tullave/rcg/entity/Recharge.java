package co.tullave.rcg.entity;

import co.tullave.rcg.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "recharge")
public class Recharge {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "card_number",
            length = 16,
            nullable = false
    )
    private String cardNumber;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            length = 20,
            nullable = false
    )
    private PaymentMethod paymentMethod;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }


    public String getCardNumber() {
        return cardNumber;
    }


    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }


    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}