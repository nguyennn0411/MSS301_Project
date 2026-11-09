package com.fucar.car.controller;

import com.fucar.car.dto.response.CarInternalResponse;
import com.fucar.car.dto.request.ReserveCarRequest;
import com.fucar.car.dto.request.UpdateCarStatusRequest;
import com.fucar.car.dto.response.CarReservationResponse;
import com.fucar.car.service.CarService;
import com.fucar.car.service.CarReservationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/cars")
public class InternalCarController {
    private final CarService carService;
    private final CarReservationService reservationService;

    public InternalCarController(CarService carService, CarReservationService reservationService) {
        this.carService = carService;
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public CarInternalResponse getById(@PathVariable Long id) {
        return carService.getInternalById(id);
    }

    @PostMapping("/sagas/{sagaId}/reservations")
    public CarReservationResponse reserve(@PathVariable String sagaId,
                                          @Valid @RequestBody ReserveCarRequest request) {
        return reservationService.reserve(sagaId, request);
    }

    @PostMapping("/sagas/{sagaId}/confirm")
    public void confirm(@PathVariable String sagaId) {
        reservationService.confirm(sagaId);
    }

    @PostMapping("/sagas/{sagaId}/cancel")
    public void cancel(@PathVariable String sagaId) {
        reservationService.cancel(sagaId);
    }

    @PostMapping("/{id}/operational-status")
    public void updateOperationalStatus(@PathVariable Long id,
                                        @Valid @RequestBody UpdateCarStatusRequest request) {
        carService.updateOperationalStatus(id, request.operationalStatus());
    }

    @DeleteMapping("/sagas/{sagaId}/reservations")
    public void compensate(@PathVariable String sagaId) {
        reservationService.compensate(sagaId);
    }
}
