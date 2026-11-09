package com.fucar.renting.config;

import com.fucar.renting.entity.RentingDetail;
import com.fucar.renting.entity.RentingDetailId;
import com.fucar.renting.entity.RentingTransaction;
import com.fucar.renting.repository.RentingTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RentingTransactionRepository transactionRepository;

    public DataInitializer(RentingTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (transactionRepository.count() > 0) {
            return;
        }

        saveTransaction(
                LocalDate.of(2026, 7, 3),
                1L,
                "COMPLETED",
                new DetailSeed(1L, LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 7), "1100000")
        );
        saveTransaction(
                LocalDate.of(2026, 7, 5),
                2L,
                "PENDING",
                new DetailSeed(2L, LocalDate.of(2026, 7, 11), LocalDate.of(2026, 7, 14), "1800000"),
                new DetailSeed(3L, LocalDate.of(2026, 7, 11), LocalDate.of(2026, 7, 13), "1400000")
        );
        saveTransaction(
                LocalDate.of(2026, 7, 8),
                3L,
                "CONFIRMED",
                new DetailSeed(5L, LocalDate.of(2026, 7, 18), LocalDate.of(2026, 7, 21), "4200000")
        );
        saveTransaction(
                LocalDate.of(2026, 6, 20),
                4L,
                "COMPLETED",
                new DetailSeed(8L, LocalDate.of(2026, 6, 22), LocalDate.of(2026, 6, 24), "1700000")
        );
    }

    private void saveTransaction(LocalDate rentingDate,
                                 Long customerId,
                                 String status,
                                 DetailSeed... detailSeeds) {
        RentingTransaction transaction = RentingTransaction.builder()
                .rentingDate(rentingDate)
                .basePrice(BigDecimal.ZERO)
                .lateFee(BigDecimal.ZERO)
                .damageFee(BigDecimal.ZERO)
                .otherFee(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .customerId(customerId)
                .rentingStatus(status)
                .build();
        transaction = transactionRepository.save(transaction);

        BigDecimal total = BigDecimal.ZERO;
        for (DetailSeed detailSeed : detailSeeds) {
            BigDecimal price = new BigDecimal(detailSeed.price());
            total = total.add(price);
            transaction.getRentingDetails().add(RentingDetail.builder()
                    .id(new RentingDetailId(transaction.getRentingTransactionId(), detailSeed.carId()))
                    .rentingTransaction(transaction)
                    .startDate(detailSeed.startDate())
                    .endDate(detailSeed.endDate())
                    .price(price)
                    .build());
        }
        transaction.setBasePrice(total);
        transaction.setTotalPrice(total);
        transactionRepository.save(transaction);
    }

    private record DetailSeed(Long carId, LocalDate startDate, LocalDate endDate, String price) {
    }
}
