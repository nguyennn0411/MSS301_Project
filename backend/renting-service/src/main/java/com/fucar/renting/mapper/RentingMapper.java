package com.fucar.renting.mapper;

import com.fucar.renting.dto.response.RentingDetailResponse;
import com.fucar.renting.dto.response.RentingTransactionResponse;
import com.fucar.renting.entity.RentingDetail;
import com.fucar.renting.entity.RentingTransaction;
import org.springframework.stereotype.Component;

@Component
public class RentingMapper {
    public RentingTransactionResponse toResponse(RentingTransaction transaction) {
        return new RentingTransactionResponse(
                transaction.getRentingTransactionId(),
                transaction.getRentingDate(),
                zeroIfNull(transaction.getBasePrice()),
                zeroIfNull(transaction.getLateFee()),
                zeroIfNull(transaction.getDamageFee()),
                zeroIfNull(transaction.getOtherFee()),
                transaction.getTotalPrice(),
                transaction.getCustomerId(),
                transaction.getRentingStatus(),
                transaction.getSagaId(),
                transaction.getAdminNote(),
                transaction.getRentingDetails().stream().map(this::toDetailResponse).toList()
        );
    }

    private java.math.BigDecimal zeroIfNull(java.math.BigDecimal value) {
        return value == null ? java.math.BigDecimal.ZERO : value;
    }

    private RentingDetailResponse toDetailResponse(RentingDetail detail) {
        return new RentingDetailResponse(
                detail.getId().getCarId(),
                detail.getStartDate(),
                detail.getEndDate(),
                detail.getPrice()
        );
    }
}
