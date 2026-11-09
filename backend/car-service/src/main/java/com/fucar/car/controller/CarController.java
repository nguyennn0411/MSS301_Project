package com.fucar.car.controller;

import com.fucar.car.dto.request.CarRequest;
import com.fucar.car.dto.response.CarResponse;
import com.fucar.car.dto.response.CarScheduleResponse;
import com.fucar.car.service.CarService;
import com.fucar.car.service.CarReservationService;
import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;
    private final CarReservationService reservationService;

    public CarController(CarService carService, CarReservationService reservationService) {
        this.carService = carService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<CarResponse> getAll() {
        return carService.getAll();
    }

    @GetMapping("/available")
    public List<CarResponse> getAvailable(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return carService.getAvailable(startDate, endDate);
    }

    @GetMapping("/{id}")
    public CarResponse getById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @GetMapping("/{id}/schedule")
    public List<CarScheduleResponse> getSchedule(@PathVariable Long id) {
        return reservationService.getCarSchedule(id);
    }

    @PostMapping
    public ResponseEntity<CarResponse> create(@Valid @RequestBody CarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(request));
    }

    @PutMapping("/{id}")
    public CarResponse update(@PathVariable Long id, @Valid @RequestBody CarRequest request) {
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
