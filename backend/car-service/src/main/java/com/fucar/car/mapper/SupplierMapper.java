package com.fucar.car.mapper;

import com.fucar.car.dto.request.SupplierRequest;
import com.fucar.car.dto.response.SupplierResponse;
import com.fucar.car.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    public Supplier toEntity(SupplierRequest request) {
        return Supplier.builder()
                .supplierName(request.supplierName())
                .supplierDescription(request.supplierDescription())
                .supplierAddress(request.supplierAddress())
                .build();
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(supplier.getSupplierId(), supplier.getSupplierName(),
                supplier.getSupplierDescription(), supplier.getSupplierAddress());
    }
}
