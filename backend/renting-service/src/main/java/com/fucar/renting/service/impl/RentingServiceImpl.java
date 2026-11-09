package com.fucar.renting.service.impl;

import com.fucar.renting.client.CarClient;
import com.fucar.renting.client.CustomerClient;
import com.fucar.renting.dto.request.CreateRentingRequest;
import com.fucar.renting.dto.request.CompleteRentingRequest;
import com.fucar.renting.dto.request.RentingDetailRequest;
import com.fucar.renting.dto.request.ReserveCarRequest;
import com.fucar.renting.dto.request.UpdateCarStatusRequest;
import com.fucar.renting.dto.response.CarInternalResponse;
import com.fucar.renting.dto.response.CarReservationResponse;
import com.fucar.renting.dto.response.RentingTransactionResponse;
import com.fucar.renting.entity.RentingDetail;
import com.fucar.renting.entity.RentingDetailId;
import com.fucar.renting.entity.RentingTransaction;
import com.fucar.renting.exception.ApiException;
import com.fucar.renting.mapper.RentingMapper;
import com.fucar.renting.repository.RentingTransactionRepository;
import com.fucar.renting.service.RentingService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentingServiceImpl implements RentingService {
    private final RentingTransactionRepository transactionRepository;
    private final CustomerClient customerClient;
    private final CarClient carClient;
    private final RentingMapper rentingMapper;

    public RentingServiceImpl(RentingTransactionRepository transactionRepository,
                              CustomerClient customerClient,
                              CarClient carClient,
                              RentingMapper rentingMapper) {
        this.transactionRepository = transactionRepository;
        this.customerClient = customerClient;
        this.carClient = carClient;
        this.rentingMapper = rentingMapper;
    }

    @Override
    public RentingTransactionResponse create(Long actorId, String actorRole, CreateRentingRequest request) {
        Long customerId = resolveCustomerId(actorId, actorRole, request.customerId());
        if (!Boolean.TRUE.equals(customerClient.existsActiveCustomer(customerId))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Customer does not exist or is disabled");
        }

        validateDuplicateCars(request.details());
        String sagaId = UUID.randomUUID().toString();
        RentingTransaction transaction = RentingTransaction.builder()
                .rentingDate(LocalDate.now())
                .basePrice(BigDecimal.ZERO)
                .lateFee(BigDecimal.ZERO)
                .damageFee(BigDecimal.ZERO)
                .otherFee(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .customerId(customerId)
                .rentingStatus("PENDING")
                .sagaId(sagaId)
                .build();
        transaction = transactionRepository.save(transaction);

        try {
            BigDecimal total = BigDecimal.ZERO;
            List<RentingDetail> details = new ArrayList<>();
            for (RentingDetailRequest detailRequest : request.details()) {
                if (detailRequest.endDate().isBefore(detailRequest.startDate())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "End date must be after or equal to start date");
                }
                CarReservationResponse reservation = carClient.reserve(sagaId,
                        new ReserveCarRequest(detailRequest.carId(), detailRequest.startDate(), detailRequest.endDate()));
                long days = ChronoUnit.DAYS.between(detailRequest.startDate(), detailRequest.endDate()) + 1;
                BigDecimal price = reservation.pricePerDay().multiply(BigDecimal.valueOf(days));
                total = total.add(price);
                details.add(RentingDetail.builder()
                        .id(new RentingDetailId(transaction.getRentingTransactionId(), detailRequest.carId()))
                        .rentingTransaction(transaction)
                        .startDate(detailRequest.startDate()).endDate(detailRequest.endDate())
                        .price(price).build());
            }
            transaction.setBasePrice(total);
            transaction.setTotalPrice(total);
            transaction.getRentingDetails().addAll(details);
            transactionRepository.saveAndFlush(transaction);
            return rentingMapper.toResponse(transaction);
        } catch (RuntimeException failure) {
            compensateQuietly(sagaId);
            throw failure;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentingTransactionResponse> getMyHistory(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByRentingDateDesc(customerId)
                .stream()
                .map(rentingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentingTransactionResponse> getAll() {
        return transactionRepository.findAll().stream().map(rentingMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RentingTransactionResponse getById(Long id, Long actorId, String actorRole) {
        RentingTransaction transaction = findTransaction(id);
        if (!"ADMIN".equals(actorRole) && !transaction.getCustomerId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only view your own renting transaction");
        }
        return rentingMapper.toResponse(transaction);
    }

    @Override
    public RentingTransactionResponse confirm(Long id) {
        RentingTransaction transaction = findTransaction(id);
        requireStatus(transaction, "PENDING");
        if (transaction.getSagaId() != null) {
            carClient.confirm(transaction.getSagaId());
        }
        transaction.setRentingStatus("CONFIRMED");
        return rentingMapper.toResponse(transaction);
    }

    @Override
    public RentingTransactionResponse reject(Long id, String note) {
        RentingTransaction transaction = findTransaction(id);
        requireStatus(transaction, "PENDING");
        cancelReservations(transaction);
        transaction.setRentingStatus("REJECTED");
        transaction.setAdminNote(note);
        return rentingMapper.toResponse(transaction);
    }

    @Override
    public RentingTransactionResponse deliver(Long id) {
        RentingTransaction transaction = findTransaction(id);
        requireStatus(transaction, "CONFIRMED");
        transaction.setRentingStatus("RENTING");
        return rentingMapper.toResponse(transaction);
    }

    @Override
    public RentingTransactionResponse complete(Long id, CompleteRentingRequest request) {
        RentingTransaction transaction = findTransaction(id);
        requireStatus(transaction, "RENTING");
        BigDecimal basePrice = zeroIfNull(transaction.getBasePrice());
        if (basePrice.signum() == 0) {
            basePrice = transaction.getRentingDetails().stream()
                    .map(RentingDetail::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal lateFee = zeroIfNull(request.lateFee());
        BigDecimal damageFee = zeroIfNull(request.damageFee());
        BigDecimal otherFee = zeroIfNull(request.otherFee());
        transaction.setBasePrice(basePrice);
        transaction.setLateFee(lateFee);
        transaction.setDamageFee(damageFee);
        transaction.setOtherFee(otherFee);
        transaction.setTotalPrice(basePrice.add(lateFee).add(damageFee).add(otherFee));
        transaction.setAdminNote(request.adminNote());
        Set<Long> maintenanceCarIds = request.maintenanceCarIds() == null
                ? Set.of() : Set.copyOf(request.maintenanceCarIds());
        transaction.getRentingDetails().forEach(detail -> carClient.updateOperationalStatus(
                detail.getId().getCarId(),
                new UpdateCarStatusRequest(maintenanceCarIds.contains(detail.getId().getCarId())
                        ? "MAINTENANCE" : "AVAILABLE")));
        transaction.setRentingStatus("COMPLETED");
        return rentingMapper.toResponse(transaction);
    }

    @Override
    public RentingTransactionResponse cancel(Long id, Long actorId, String actorRole, String note) {
        RentingTransaction transaction = findTransaction(id);
        boolean admin = "ADMIN".equals(actorRole);
        if (!admin && !transaction.getCustomerId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only cancel your own renting transaction");
        }
        String status = transaction.getRentingStatus();
        if (!("PENDING".equals(status) || "CONFIRMED".equals(status))) {
            throw new ApiException(HttpStatus.CONFLICT, "Only PENDING or CONFIRMED transactions can be cancelled");
        }
        if (!admin && "CONFIRMED".equals(status)) {
            LocalDate firstStartDate = transaction.getRentingDetails().stream()
                    .map(RentingDetail::getStartDate)
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());
            if (!firstStartDate.isAfter(LocalDate.now())) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "A confirmed transaction cannot be cancelled on or after its pickup date");
            }
        }
        cancelReservations(transaction);
        transaction.setRentingStatus("CANCELLED");
        transaction.setAdminNote(note);
        return rentingMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentingTransactionResponse> report(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "End date must be after or equal start date");
        }
        return transactionRepository.findByRentingStatusAndRentingDateBetweenOrderByTotalPriceDesc(
                        "COMPLETED", startDate, endDate)
                .stream()
                .map(rentingMapper::toResponse)
                .toList();
    }

    private void validateDuplicateCars(List<RentingDetailRequest> details) {
        Set<Long> carIds = new HashSet<>();
        for (RentingDetailRequest detail : details) {
            if (!carIds.add(detail.carId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Duplicate car in one renting transaction");
            }
        }
    }

    private Long resolveCustomerId(Long actorId, String actorRole, Long requestedCustomerId) {
        if ("ADMIN".equals(actorRole)) {
            if (requestedCustomerId == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "customerId is required when Admin creates a rental");
            }
            return requestedCustomerId;
        }
        if (!"CUSTOMER".equals(actorRole)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Role is not allowed to create a rental");
        }
        return actorId;
    }

    private RentingTransaction findTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Renting transaction not found"));
    }

    private void requireStatus(RentingTransaction transaction, String expected) {
        if (!expected.equals(transaction.getRentingStatus())) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Transaction must be " + expected + " for this action");
        }
    }

    private void cancelReservations(RentingTransaction transaction) {
        if (transaction.getSagaId() != null) {
            carClient.cancel(transaction.getSagaId());
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void compensateQuietly(String sagaId) {
        try {
            carClient.compensate(sagaId);
        } catch (RuntimeException ignored) {
            // The saga id makes compensation idempotent and safe to retry.
        }
    }
}
