package co.tullave.rcg.service.impl;

import co.tullave.rcg.dto.RechargeRequest;
import co.tullave.rcg.dto.RechargeResponse;
import co.tullave.rcg.entity.Recharge;
import co.tullave.rcg.exception.ResourceNotFoundException;
import co.tullave.rcg.mapper.RechargeMapper;
import co.tullave.rcg.repository.RechargeRepository;
import co.tullave.rcg.service.RechargeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RechargeServiceImpl implements RechargeService {

    private static final Logger log = LoggerFactory.getLogger(RechargeServiceImpl.class);

    private final RechargeRepository repository;

    public RechargeServiceImpl(RechargeRepository repository) {
        this.repository = repository;
    }

    @Override
    public RechargeResponse create(RechargeRequest request) {
        log.info("Creating recharge with payment method {}", request.paymentMethod());

        Recharge recharge = RechargeMapper.toEntity(request);
        Recharge saved = repository.save(recharge);

        log.info("Recharge created successfully with id {}", saved.getId());
        return RechargeMapper.toResponse(saved);
    }

    @Override
    public Page<RechargeResponse> findAll(String cardNumber, Pageable pageable) {
        log.debug(
                "Retrieving recharges. cardNumberFilter={}, page={}, size={}",
                cardNumber != null && !cardNumber.isBlank(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Recharge> result = (cardNumber != null && !cardNumber.isBlank())
                ? repository.findByCardNumber(cardNumber, pageable)
                : repository.findAll(pageable);

        return result.map(RechargeMapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting recharge with id {}", id);

        if (!repository.existsById(id)) {
            log.warn("Recharge with id {} was not found", id);
            throw new ResourceNotFoundException("Recharge not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("Recharge with id {} deleted successfully", id);
    }
}
