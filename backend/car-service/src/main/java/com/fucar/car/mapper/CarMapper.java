package com.fucar.car.mapper;

import com.fucar.car.dto.request.CarRequest;
import com.fucar.car.dto.response.CarInternalResponse;
import com.fucar.car.dto.response.CarResponse;
import com.fucar.car.entity.CarInformation;
import com.fucar.car.repository.ManufacturerRepository;
import com.fucar.car.repository.SupplierRepository;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {
    private final ManufacturerRepository manufacturerRepository;
    private final SupplierRepository supplierRepository;

    public CarMapper(ManufacturerRepository manufacturerRepository, SupplierRepository supplierRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.supplierRepository = supplierRepository;
    }

    public CarInformation toEntity(CarRequest request) {
        return CarInformation.builder()
                .carName(request.carName())
                .carDescription(request.carDescription())
                .imageUrl(request.imageUrl())
                .numberOfDoors(request.numberOfDoors())
                .seatingCapacity(request.seatingCapacity())
                .fuelType(request.fuelType())
                .year(request.year())
                .mileage(request.mileage())
                .manufacturerId(request.manufacturerId())
                .supplierId(request.supplierId())
                .operationalStatus(resolveOperationalStatus(request.operationalStatus(), request.carStatus()))
                .carStatus("AVAILABLE".equals(resolveOperationalStatus(request.operationalStatus(), request.carStatus())))
                .carRentingPricePerDay(request.carRentingPricePerDay())
                .build();
    }

    public CarResponse toResponse(CarInformation car) {
        return new CarResponse(car.getCarId(), car.getCarName(), car.getCarDescription(), car.getImageUrl(),
                car.getNumberOfDoors(), car.getSeatingCapacity(), car.getFuelType(), car.getYear(), car.getMileage(),
                car.getManufacturerId(), manufacturerName(car.getManufacturerId()),
                car.getSupplierId(), supplierName(car.getSupplierId()),
                car.getCarStatus(), operationalStatus(car), car.getCarRentingPricePerDay());
    }

    public CarInternalResponse toInternalResponse(CarInformation car) {
        return new CarInternalResponse(car.getCarId(), car.getCarStatus(), car.getCarRentingPricePerDay());
    }

    private String manufacturerName(Long id) {
        return manufacturerRepository.findById(id).map(m -> m.getManufacturerName()).orElse("Unknown");
    }

    private String supplierName(Long id) {
        return supplierRepository.findById(id).map(s -> s.getSupplierName()).orElse("Unknown");
    }

    private String resolveOperationalStatus(String operationalStatus, Boolean active) {
        if (active != null) {
            if (active) return "AVAILABLE";
            return "MAINTENANCE".equalsIgnoreCase(operationalStatus) ? "MAINTENANCE" : "INACTIVE";
        }
        if (operationalStatus != null && !operationalStatus.isBlank()) return operationalStatus.toUpperCase();
        return "AVAILABLE";
    }

    private String operationalStatus(CarInformation car) {
        if (car.getOperationalStatus() != null) return car.getOperationalStatus();
        return Boolean.TRUE.equals(car.getCarStatus()) ? "AVAILABLE" : "INACTIVE";
    }
}
