package com.fucar.renting.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RentingTransactionResponse(
        Long rentingTransactionId,
        LocalDate rentingDate,
        BigDecimal basePrice,
        BigDecimal lateFee,
        BigDecimal damageFee,
        BigDecimal otherFee,
        BigDecimal totalPrice,
        Long customerId,
        String rentingStatus,
        String sagaId,
        String adminNote,
        List<RentingDetailResponse> details
) {
}
