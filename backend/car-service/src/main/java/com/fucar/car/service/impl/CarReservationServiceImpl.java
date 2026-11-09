package com.fucar.car.service.impl;

import com.fucar.car.dto.request.ReserveCarRequest;
import com.fucar.car.dto.response.CarReservationResponse;
import com.fucar.car.dto.response.CarScheduleResponse;
import com.fucar.car.entity.CarInformation;
import com.fucar.car.entity.CarReservation;
import com.fucar.car.exception.ApiException;
import com.fucar.car.repository.CarInformationRepository;
import com.fucar.car.repository.CarReservationRepository;
import com.fucar.car.service.CarReservationService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarReservationServiceImpl implements CarReservationService {
    private final CarInformationRepository carRepository;
    private final CarReservationRepository reservationRepository;

    public CarReservationServiceImpl(CarInformationRepository carRepository,
                                     CarReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public CarReservationResponse reserve(String sagaId, ReserveCarRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "End date must be after or equal to start date");
        }
        CarInformation car = carRepository.findByIdForUpdate(request.carId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Car not found"));
        if (!Boolean.TRUE.equals(car.getCarStatus())
                || (car.getOperationalStatus() != null && !"AVAILABLE".equals(car.getOperationalStatus()))) {
            throw new ApiException(HttpStatus.CONFLICT, "Car is not available for renting");
        }
        if (reservationRepository.countOverlapping(request.carId(), request.startDate(), request.endDate()) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Car is already reserved for this period");
        }
        reservationRepository.save(CarReservation.builder()
                .sagaId(sagaId).carId(request.carId())
                .startDate(request.startDate()).endDate(request.endDate())
                .status("RESERVED").build());
        return new CarReservationResponse(sagaId, car.getCarId(), car.getCarRentingPricePerDay(), "RESERVED");
    }

    @Override
    public void confirm(String sagaId) {
        List<CarReservation> reservations = reservationRepository.findBySagaId(sagaId);
        if (reservations.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Saga reservations not found");
        reservations.stream()
                .filter(r -> "RESERVED".equals(r.getStatus()))
                .forEach(r -> r.setStatus("CONFIRMED"));
    }

    @Override
    public void cancel(String sagaId) {
        List<CarReservation> reservations = reservationRepository.findBySagaId(sagaId);
        if (reservations.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Saga reservations not found");
        reservations.stream()
                .filter(r -> "RESERVED".equals(r.getStatus()) || "CONFIRMED".equals(r.getStatus()))
                .forEach(r -> r.setStatus("CANCELLED"));
    }

    @Override
    public void compensate(String sagaId) {
        reservationRepository.deleteBySagaIdAndStatus(sagaId, "RESERVED");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findUnavailableCarIds(LocalDate startDate, LocalDate endDate) {
        return carRepository.findUnavailableCarIds(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarScheduleResponse> getCarSchedule(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Car not found");
        }
        return reservationRepository.findByCarIdOrderByStartDateDesc(carId).stream()
                .map(r -> new CarScheduleResponse(r.getReservationId(), r.getSagaId(), r.getCarId(),
                        r.getStartDate(), r.getEndDate(), r.getStatus()))
                .toList();
    }
}
