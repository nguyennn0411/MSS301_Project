package com.fucar.car.repository;

import com.fucar.car.entity.Manufacturer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    Optional<Manufacturer> findByManufacturerName(String manufacturerName);
}
