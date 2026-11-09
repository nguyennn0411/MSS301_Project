package com.fucar.renting.repository;

import com.fucar.renting.entity.RentingTransaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentingTransactionRepository extends JpaRepository<RentingTransaction, Long> {
    List<RentingTransaction> findByCustomerIdOrderByRentingDateDesc(Long customerId);

    List<RentingTransaction> findByRentingDateBetweenOrderByTotalPriceDesc(LocalDate startDate, LocalDate endDate);

    List<RentingTransaction> findByRentingStatusAndRentingDateBetweenOrderByTotalPriceDesc(
            String rentingStatus, LocalDate startDate, LocalDate endDate);
}
