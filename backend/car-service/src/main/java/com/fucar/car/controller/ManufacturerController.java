package com.fucar.car.controller;

import com.fucar.car.dto.request.ManufacturerRequest;
import com.fucar.car.dto.response.ManufacturerResponse;
import com.fucar.car.service.ManufacturerService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/manufacturers")
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @GetMapping
    public List<ManufacturerResponse> getAll() {
        return manufacturerService.getAll();
    }

    @GetMapping("/{id}")
    public ManufacturerResponse getById(@PathVariable Long id) {
        return manufacturerService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ManufacturerResponse> create(@Valid @RequestBody ManufacturerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manufacturerService.create(request));
    }

    @PutMapping("/{id}")
    public ManufacturerResponse update(@PathVariable Long id, @Valid @RequestBody ManufacturerRequest request) {
        return manufacturerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        manufacturerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
