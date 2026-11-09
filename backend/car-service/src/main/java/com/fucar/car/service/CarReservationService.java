package com.fucar.car.service;

import com.fucar.car.dto.request.ReserveCarRequest;
import com.fucar.car.dto.response.CarReservationResponse;
import java.time.LocalDate;
import java.util.List;
import com.fucar.car.dto.response.CarScheduleResponse;

public interface CarReservationService {
    CarReservationResponse reserve(String sagaId, ReserveCarRequest request);
    void confirm(String sagaId);
    void cancel(String sagaId);
    void compensate(String sagaId);
    List<Long> findUnavailableCarIds(LocalDate startDate, LocalDate endDate);
    List<CarScheduleResponse> getCarSchedule(Long carId);
}
