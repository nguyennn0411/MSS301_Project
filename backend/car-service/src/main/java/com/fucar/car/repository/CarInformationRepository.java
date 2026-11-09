package com.fucar.car.repository;

import com.fucar.car.entity.CarInformation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;

public interface CarInformationRepository extends JpaRepository<CarInformation, Long> {
    List<CarInformation> findByCarStatusTrue();

    Optional<CarInformation> findByCarName(String carName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CarInformation c where c.carId = :id")
    Optional<CarInformation> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select distinct r.carId from CarReservation r
            where r.status in ('RESERVED', 'CONFIRMED')
              and r.startDate <= :endDate and r.endDate >= :startDate
            """)
    List<Long> findUnavailableCarIds(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
}
