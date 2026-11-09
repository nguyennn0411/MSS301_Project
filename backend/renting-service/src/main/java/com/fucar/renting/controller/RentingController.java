package com.fucar.renting.controller;

import com.fucar.renting.dto.request.CreateRentingRequest;
import com.fucar.renting.dto.request.CompleteRentingRequest;
import com.fucar.renting.dto.request.RentingActionRequest;
import com.fucar.renting.dto.response.RentingTransactionResponse;
import com.fucar.renting.service.RentingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentings")
public class RentingController {
    private final RentingService rentingService;

    public RentingController(RentingService rentingService) {
        this.rentingService = rentingService;
    }

    @PostMapping
    public ResponseEntity<RentingTransactionResponse> create(@RequestHeader("X-User-Id") Long actorId,
                                                            @RequestHeader("X-User-Role") String actorRole,
                                                            @Valid @RequestBody CreateRentingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentingService.create(actorId, actorRole, request));
    }

    @GetMapping("/my-history")
    public List<RentingTransactionResponse> getMyHistory(@RequestHeader("X-User-Id") Long customerId) {
        return rentingService.getMyHistory(customerId);
    }

    @GetMapping
    public List<RentingTransactionResponse> getAll() {
        return rentingService.getAll();
    }

    @GetMapping("/{id}")
    public RentingTransactionResponse getById(@PathVariable Long id,
                                              @RequestHeader("X-User-Id") Long actorId,
                                              @RequestHeader("X-User-Role") String actorRole) {
        return rentingService.getById(id, actorId, actorRole);
    }

    @PatchMapping("/{id}/confirm")
    public RentingTransactionResponse confirm(@PathVariable Long id) {
        return rentingService.confirm(id);
    }

    @PatchMapping("/{id}/reject")
    public RentingTransactionResponse reject(@PathVariable Long id,
                                             @RequestBody(required = false) RentingActionRequest request) {
        return rentingService.reject(id, request == null ? null : request.note());
    }

    @PatchMapping("/{id}/deliver")
    public RentingTransactionResponse deliver(@PathVariable Long id) {
        return rentingService.deliver(id);
    }

    @PatchMapping("/{id}/complete")
    public RentingTransactionResponse complete(@PathVariable Long id,
                                               @Valid @RequestBody CompleteRentingRequest request) {
        return rentingService.complete(id, request);
    }

    @PatchMapping("/{id}/cancel")
    public RentingTransactionResponse cancel(@PathVariable Long id,
                                             @RequestHeader("X-User-Id") Long actorId,
                                             @RequestHeader("X-User-Role") String actorRole,
                                             @RequestBody(required = false) RentingActionRequest request) {
        return rentingService.cancel(id, actorId, actorRole, request == null ? null : request.note());
    }

    @GetMapping("/report")
    public List<RentingTransactionResponse> report(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return rentingService.report(startDate, endDate);
    }
}
