package co.tullave.rcg.repository;


import co.tullave.rcg.entity.Recharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RechargeRepository 
extends JpaRepository<Recharge,Long>{


    Page<Recharge> findByCardNumber(
            String cardNumber,
            Pageable pageable
    );

}