package com.fucar.car.service.impl;

import com.fucar.car.dto.request.CarRequest;
import com.fucar.car.dto.response.CarInternalResponse;
import com.fucar.car.dto.response.CarResponse;
import com.fucar.car.entity.CarInformation;
import com.fucar.car.exception.ApiException;
import com.fucar.car.mapper.CarMapper;
import com.fucar.car.repository.CarInformationRepository;
import com.fucar.car.repository.ManufacturerRepository;
import com.fucar.car.repository.SupplierRepository;
import com.fucar.car.service.CarService;
import java.util.List;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarServiceImpl implements CarService {
    private final CarInformationRepository carRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final SupplierRepository supplierRepository;
    private final CarMapper carMapper;
    private final CarReservationServiceImpl reservationService;

    public CarServiceImpl(CarInformationRepository carRepository,
                          ManufacturerRepository manufacturerRepository,
                          SupplierRepository supplierRepository,
                          CarMapper carMapper,
                          CarReservationServiceImpl reservationService) {
        this.carRepository = carRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.supplierRepository = supplierRepository;
        this.carMapper = carMapper;
        this.reservationService = reservationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponse> getAll() {
        return carRepository.findAll().stream().map(carMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponse> getAvailable(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return carRepository.findByCarStatusTrue().stream().map(carMapper::toResponse).toList();
        }
        if (endDate.isBefore(startDate)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "End date must be after or equal to start date");
        }
        Set<Long> unavailable = Set.copyOf(reservationService.findUnavailableCarIds(startDate, endDate));
        return carRepository.findByCarStatusTrue().stream()
                .filter(car -> !unavailable.contains(car.getCarId()))
                .map(carMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponse getById(Long id) {
        return carMapper.toResponse(findCar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CarInternalResponse getInternalById(Long id) {
        return carMapper.toInternalResponse(findCar(id));
    }

    @Override
    public CarResponse create(CarRequest request) {
        validateReferences(request.manufacturerId(), request.supplierId());
        validateOperationalStatus(request.operationalStatus());
        return carMapper.toResponse(carRepository.save(carMapper.toEntity(request)));
    }

    @Override
    public CarResponse update(Long id, CarRequest request) {
        validateReferences(request.manufacturerId(), request.supplierId());
        validateOperationalStatus(request.operationalStatus());
        CarInformation car = findCar(id);
        car.setCarName(request.carName());
        car.setCarDescription(request.carDescription());
        car.setImageUrl(request.imageUrl());
        car.setNumberOfDoors(request.numberOfDoors());
        car.setSeatingCapacity(request.seatingCapacity());
        car.setFuelType(request.fuelType());
        car.setYear(request.year());
        car.setMileage(request.mileage());
        car.setManufacturerId(request.manufacturerId());
        car.setSupplierId(request.supplierId());
        String operationalStatus = resolveOperationalStatus(request.operationalStatus(), request.carStatus());
        car.setOperationalStatus(operationalStatus);
        car.setCarStatus("AVAILABLE".equals(operationalStatus));
        car.setCarRentingPricePerDay(request.carRentingPricePerDay());
        return carMapper.toResponse(car);
    }

    @Override
    public void delete(Long id) {
        carRepository.delete(findCar(id));
    }

    @Override
    public void updateOperationalStatus(Long id, String operationalStatus) {
        validateOperationalStatus(operationalStatus);
        String normalized = operationalStatus.toUpperCase();
        CarInformation car = findCar(id);
        car.setOperationalStatus(normalized);
        car.setCarStatus("AVAILABLE".equals(normalized));
    }

    private CarInformation findCar(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    private void validateReferences(Long manufacturerId, Long supplierId) {
        if (!manufacturerRepository.existsById(manufacturerId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Manufacturer does not exist");
        }
        if (!supplierRepository.existsById(supplierId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Supplier does not exist");
        }
    }

    private void validateOperationalStatus(String status) {
        if (status != null && !status.isBlank()
                && !Set.of("AVAILABLE", "MAINTENANCE", "INACTIVE").contains(status.toUpperCase())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Operational status must be AVAILABLE, MAINTENANCE or INACTIVE");
        }
    }

    private String resolveOperationalStatus(String status, Boolean active) {
        if (active != null) {
            if (active) return "AVAILABLE";
            return "MAINTENANCE".equalsIgnoreCase(status) ? "MAINTENANCE" : "INACTIVE";
        }
        if (status != null && !status.isBlank()) return status.toUpperCase();
        return "AVAILABLE";
    }
}
