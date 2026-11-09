package com.fucar.renting.client;

import com.fucar.renting.dto.response.CarInternalResponse;
import com.fucar.renting.dto.request.ReserveCarRequest;
import com.fucar.renting.dto.request.UpdateCarStatusRequest;
import com.fucar.renting.dto.response.CarReservationResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "car-service")
public interface CarClient {
    @GetMapping("/internal/cars/{id}")
    CarInternalResponse getCar(@PathVariable("id") Long id);

    @PostMapping("/internal/cars/sagas/{sagaId}/reservations")
    CarReservationResponse reserve(@PathVariable("sagaId") String sagaId,
                                   @RequestBody ReserveCarRequest request);

    @PostMapping("/internal/cars/sagas/{sagaId}/confirm")
    void confirm(@PathVariable("sagaId") String sagaId);

    @PostMapping("/internal/cars/sagas/{sagaId}/cancel")
    void cancel(@PathVariable("sagaId") String sagaId);

    @PostMapping("/internal/cars/{id}/operational-status")
    void updateOperationalStatus(@PathVariable("id") Long id, @RequestBody UpdateCarStatusRequest request);

    @DeleteMapping("/internal/cars/sagas/{sagaId}/reservations")
    void compensate(@PathVariable("sagaId") String sagaId);
}
