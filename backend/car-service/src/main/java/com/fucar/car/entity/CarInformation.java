package com.fucar.car.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "car_information")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    private String carName;
    private String carDescription;
    @Column(length = 1000)
    private String imageUrl;
    private Integer numberOfDoors;
    private Integer seatingCapacity;
    private String fuelType;
    private Integer year;
    private Long mileage;
    private Long manufacturerId;
    private Long supplierId;
    private Boolean carStatus;
    @Builder.Default
    private String operationalStatus = "AVAILABLE";
    private BigDecimal carRentingPricePerDay;
}
