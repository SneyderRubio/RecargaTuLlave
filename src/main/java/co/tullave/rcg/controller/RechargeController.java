package co.tullave.rcg.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.tullave.rcg.dto.ApiResponse;
import co.tullave.rcg.dto.RechargeRequest;
import co.tullave.rcg.dto.RechargeResponse;
import co.tullave.rcg.service.RechargeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RechargeController {

    private final RechargeService service;

    public RechargeController(RechargeService service) {
        this.service = service;
    }


    @PostMapping("/recharges")
    public ResponseEntity<ApiResponse<RechargeResponse>> create(
            @Valid @RequestBody RechargeRequest request
    ) {

        RechargeResponse recharge =
                service.create(request);

        ApiResponse<RechargeResponse> response =
                ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Recharge created successfully",
                        recharge
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/getRecharges")
    public ResponseEntity<ApiResponse<Page<RechargeResponse>>> findAll(
            @RequestParam(required = false)
            String cardNumber,

            Pageable pageable
    ) {

        Page<RechargeResponse> recharges =
                service.findAll(
                        cardNumber,
                        pageable
                );

        ApiResponse<Page<RechargeResponse>> response =
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Recharges retrieved successfully",
                        recharges
                );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/recharges/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity
                .noContent()
                .header(
                        "X-Message",
                        "Recharge deleted successfully"
                )
                .build();
    }
}