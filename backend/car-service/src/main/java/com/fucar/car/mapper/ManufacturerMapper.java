package com.fucar.car.mapper;

import com.fucar.car.dto.request.ManufacturerRequest;
import com.fucar.car.dto.response.ManufacturerResponse;
import com.fucar.car.entity.Manufacturer;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {
    public Manufacturer toEntity(ManufacturerRequest request) {
        return Manufacturer.builder()
                .manufacturerName(request.manufacturerName())
                .description(request.description())
                .manufacturerCountry(request.manufacturerCountry())
                .build();
    }

    public ManufacturerResponse toResponse(Manufacturer manufacturer) {
        return new ManufacturerResponse(manufacturer.getManufacturerId(), manufacturer.getManufacturerName(),
                manufacturer.getDescription(), manufacturer.getManufacturerCountry());
    }
}
