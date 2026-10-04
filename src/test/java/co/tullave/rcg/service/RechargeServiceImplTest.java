package co.tullave.rcg.service;

import co.tullave.rcg.dto.RechargeRequest;
import co.tullave.rcg.dto.RechargeResponse;
import co.tullave.rcg.entity.Recharge;
import co.tullave.rcg.enums.PaymentMethod;
import co.tullave.rcg.exception.ResourceNotFoundException;
import co.tullave.rcg.repository.RechargeRepository;
import co.tullave.rcg.service.impl.RechargeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RechargeServiceImplTest {

    @Mock
    private RechargeRepository repository;

    private RechargeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RechargeServiceImpl(repository);
    }

    @Test
    void createShouldSaveRecharge() {
        RechargeRequest request = new RechargeRequest(
                "1010000012345678",
                new BigDecimal("50000"),
                PaymentMethod.NEQUI
        );

        when(repository.save(any(Recharge.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RechargeResponse response = service.create(request);

        assertThat(response.cardNumber()).isEqualTo("1010000012345678");
        assertThat(response.amount()).isEqualByComparingTo("50000");
        assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.NEQUI);
        verify(repository).save(any(Recharge.class));
    }

    @Test
    void findAllShouldReturnPagedRecharges() {
        Pageable pageable = PageRequest.of(0, 10);
        Recharge recharge = recharge("1010000012345678");
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(recharge), pageable, 1));

        Page<RechargeResponse> result = service.findAll(null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("1010000012345678");
        verify(repository).findAll(pageable);
    }

    @Test
    void findAllShouldFilterByCardNumber() {
        Pageable pageable = PageRequest.of(0, 10);
        String cardNumber = "1010000012345678";
        when(repository.findByCardNumber(cardNumber, pageable))
                .thenReturn(new PageImpl<>(List.of(recharge(cardNumber)), pageable, 1));

        Page<RechargeResponse> result = service.findAll(cardNumber, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository).findByCardNumber(cardNumber, pageable);
        verify(repository, never()).findAll(pageable);
    }

    @Test
    void deleteShouldDeleteExistingRecharge() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowWhenRechargeDoesNotExist() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recharge not found with id: 99");

        verify(repository, never()).deleteById(99L);
    }

    private Recharge recharge(String cardNumber) {
        Recharge recharge = new Recharge();
        recharge.setCardNumber(cardNumber);
        recharge.setAmount(new BigDecimal("50000"));
        recharge.setPaymentMethod(PaymentMethod.NEQUI);
        return recharge;
    }
}
