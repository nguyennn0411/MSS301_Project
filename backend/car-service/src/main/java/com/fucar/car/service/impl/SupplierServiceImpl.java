package com.fucar.car.service.impl;

import com.fucar.car.dto.request.SupplierRequest;
import com.fucar.car.dto.response.SupplierResponse;
import com.fucar.car.entity.Supplier;
import com.fucar.car.exception.ApiException;
import com.fucar.car.mapper.SupplierMapper;
import com.fucar.car.repository.SupplierRepository;
import com.fucar.car.service.SupplierService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAll() {
        return supplierRepository.findAll().stream().map(supplierMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getById(Long id) {
        return supplierMapper.toResponse(findSupplier(id));
    }

    @Override
    public SupplierResponse create(SupplierRequest request) {
        return supplierMapper.toResponse(supplierRepository.save(supplierMapper.toEntity(request)));
    }

    @Override
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = findSupplier(id);
        supplier.setSupplierName(request.supplierName());
        supplier.setSupplierDescription(request.supplierDescription());
        supplier.setSupplierAddress(request.supplierAddress());
        return supplierMapper.toResponse(supplier);
    }

    @Override
    public void delete(Long id) {
        supplierRepository.delete(findSupplier(id));
    }

    private Supplier findSupplier(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Supplier not found"));
    }
}
