package com.fucar.car.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "car_reservations",
        uniqueConstraints = @UniqueConstraint(name = "uk_saga_car", columnNames = {"saga_id", "car_id"}),
        indexes = @Index(name = "idx_reservation_car_dates", columnList = "car_id,start_date,end_date"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CarReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;
    @Column(name = "saga_id", nullable = false)
    private String sagaId;
    @Column(name = "car_id", nullable = false)
    private Long carId;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private String status;
}
