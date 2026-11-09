package com.fucar.car.repository;

import com.fucar.car.entity.CarReservation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarReservationRepository extends JpaRepository<CarReservation, Long> {
    @Query("""
            select count(r) from CarReservation r
            where r.carId = :carId
              and r.status in ('RESERVED', 'CONFIRMED')
              and r.startDate <= :endDate and r.endDate >= :startDate
            """)
    long countOverlapping(@Param("carId") Long carId,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    List<CarReservation> findBySagaId(String sagaId);

    List<CarReservation> findByCarIdOrderByStartDateDesc(Long carId);

    @Modifying
    void deleteBySagaIdAndStatus(String sagaId, String status);
}
