package com.fucar.renting.service;

import com.fucar.renting.dto.request.CreateRentingRequest;
import com.fucar.renting.dto.request.CompleteRentingRequest;
import com.fucar.renting.dto.response.RentingTransactionResponse;
import java.time.LocalDate;
import java.util.List;

public interface RentingService {
    RentingTransactionResponse create(Long actorId, String actorRole, CreateRentingRequest request);

    List<RentingTransactionResponse> getMyHistory(Long customerId);

    List<RentingTransactionResponse> getAll();

    RentingTransactionResponse getById(Long id, Long actorId, String actorRole);

    RentingTransactionResponse confirm(Long id);

    RentingTransactionResponse reject(Long id, String note);

    RentingTransactionResponse deliver(Long id);

    RentingTransactionResponse complete(Long id, CompleteRentingRequest request);

    RentingTransactionResponse cancel(Long id, Long actorId, String actorRole, String note);

    List<RentingTransactionResponse> report(LocalDate startDate, LocalDate endDate);
}
