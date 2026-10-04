package co.tullave.rcg.mapper;


import co.tullave.rcg.dto.RechargeRequest;
import co.tullave.rcg.dto.RechargeResponse;
import co.tullave.rcg.entity.Recharge;


public class RechargeMapper {


    public static Recharge toEntity(
            RechargeRequest request
    ){

        Recharge recharge = new Recharge();

        recharge.setCardNumber(
                request.cardNumber()
        );

        recharge.setAmount(
                request.amount()
        );

        recharge.setPaymentMethod(
                request.paymentMethod()
        );

        return recharge;
    }



    public static RechargeResponse toResponse(
            Recharge recharge
    ){

        return new RechargeResponse(
                recharge.getId(),
                recharge.getCardNumber(),
                recharge.getAmount(),
                recharge.getPaymentMethod(),
                recharge.getCreatedAt()
        );

    }

}