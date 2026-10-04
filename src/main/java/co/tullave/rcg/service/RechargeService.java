package co.tullave.rcg.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import co.tullave.rcg.dto.RechargeRequest;
import co.tullave.rcg.dto.RechargeResponse;

public interface RechargeService {

    RechargeResponse create(RechargeRequest request);

    Page<RechargeResponse> findAll(
            String cardNumber,
            Pageable pageable
    );

    void delete(Long id);
}