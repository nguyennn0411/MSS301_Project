package com.fucar.car.service;

import com.fucar.car.dto.request.SupplierRequest;
import com.fucar.car.dto.response.SupplierResponse;
import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAll();

    SupplierResponse getById(Long id);

    SupplierResponse create(SupplierRequest request);

    SupplierResponse update(Long id, SupplierRequest request);

    void delete(Long id);
}
