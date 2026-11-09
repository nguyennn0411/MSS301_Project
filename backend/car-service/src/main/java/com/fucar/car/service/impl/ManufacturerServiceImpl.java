package com.fucar.car.service.impl;

import com.fucar.car.dto.request.ManufacturerRequest;
import com.fucar.car.dto.response.ManufacturerResponse;
import com.fucar.car.entity.Manufacturer;
import com.fucar.car.exception.ApiException;
import com.fucar.car.mapper.ManufacturerMapper;
import com.fucar.car.repository.ManufacturerRepository;
import com.fucar.car.service.ManufacturerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ManufacturerServiceImpl implements ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository, ManufacturerMapper manufacturerMapper) {
        this.manufacturerRepository = manufacturerRepository;
        this.manufacturerMapper = manufacturerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManufacturerResponse> getAll() {
        return manufacturerRepository.findAll().stream().map(manufacturerMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ManufacturerResponse getById(Long id) {
        return manufacturerMapper.toResponse(findManufacturer(id));
    }

    @Override
    public ManufacturerResponse create(ManufacturerRequest request) {
        return manufacturerMapper.toResponse(manufacturerRepository.save(manufacturerMapper.toEntity(request)));
    }

    @Override
    public ManufacturerResponse update(Long id, ManufacturerRequest request) {
        Manufacturer manufacturer = findManufacturer(id);
        manufacturer.setManufacturerName(request.manufacturerName());
        manufacturer.setDescription(request.description());
        manufacturer.setManufacturerCountry(request.manufacturerCountry());
        return manufacturerMapper.toResponse(manufacturer);
    }

    @Override
    public void delete(Long id) {
        manufacturerRepository.delete(findManufacturer(id));
    }

    private Manufacturer findManufacturer(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Manufacturer not found"));
    }
}
