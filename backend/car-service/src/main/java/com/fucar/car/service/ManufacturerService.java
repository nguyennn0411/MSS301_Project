package com.fucar.car.service;

import com.fucar.car.dto.request.ManufacturerRequest;
import com.fucar.car.dto.response.ManufacturerResponse;
import java.util.List;

public interface ManufacturerService {
    List<ManufacturerResponse> getAll();

    ManufacturerResponse getById(Long id);

    ManufacturerResponse create(ManufacturerRequest request);

    ManufacturerResponse update(Long id, ManufacturerRequest request);

    void delete(Long id);
}
