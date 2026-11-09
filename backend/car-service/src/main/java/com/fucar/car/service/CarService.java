package com.fucar.car.service;

import com.fucar.car.dto.request.CarRequest;
import com.fucar.car.dto.response.CarInternalResponse;
import com.fucar.car.dto.response.CarResponse;
import java.util.List;
import java.time.LocalDate;

public interface CarService {
    List<CarResponse> getAll();

    List<CarResponse> getAvailable(LocalDate startDate, LocalDate endDate);

    CarResponse getById(Long id);

    CarInternalResponse getInternalById(Long id);

    CarResponse create(CarRequest request);

    CarResponse update(Long id, CarRequest request);

    void updateOperationalStatus(Long id, String operationalStatus);

    void delete(Long id);
}
