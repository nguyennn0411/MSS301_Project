package com.fucar.car.config;

import com.fucar.car.entity.CarInformation;
import com.fucar.car.entity.Manufacturer;
import com.fucar.car.entity.Supplier;
import com.fucar.car.repository.CarInformationRepository;
import com.fucar.car.repository.ManufacturerRepository;
import com.fucar.car.repository.SupplierRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final SupplierRepository supplierRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final CarInformationRepository carRepository;

    public DataInitializer(SupplierRepository supplierRepository,
                           ManufacturerRepository manufacturerRepository,
                           CarInformationRepository carRepository) {
        this.supplierRepository = supplierRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.carRepository = carRepository;
    }

    @Override
    public void run(String... args) {
        Supplier fuSupplier = saveSupplier(
                "FU Car Supplier",
                "Main supplier for university demo data",
                "FPT University, Thu Duc City, Ho Chi Minh City"
        );
        Supplier cityRental = saveSupplier(
                "City Rental Partner",
                "Urban rental partner with compact and sedan cars",
                "District 1, Ho Chi Minh City"
        );
        Supplier premiumDrive = saveSupplier(
                "Premium Drive Vietnam",
                "Premium and family car rental supplier",
                "Cau Giay District, Ha Noi"
        );

        Manufacturer toyota = saveManufacturer("Toyota", "Reliable Japanese cars", "Japan");
        Manufacturer honda = saveManufacturer("Honda", "Efficient city cars", "Japan");
        Manufacturer mazda = saveManufacturer("Mazda", "Modern design and smooth driving", "Japan");
        Manufacturer kia = saveManufacturer("Kia", "Comfortable Korean family cars", "South Korea");
        Manufacturer ford = saveManufacturer("Ford", "Strong SUVs and pickup trucks", "United States");

        saveCar("Toyota Vios", "Compact sedan for city travel",
                commonsImage("Toyota Vios XP150 Facelift Shishi 02 2022-09-20.jpg"), 4, 5, "Gasoline", 2022,
                42500L, toyota, fuSupplier, true, "550000");
        saveCar("Honda City", "Comfortable sedan with good fuel economy",
                commonsImage("Honda City 1.5 S 2023.jpg"), 4, 5, "Gasoline", 2023,
                28300L, honda, cityRental, true, "600000");
        saveCar("Mazda 3", "Stylish sedan for business and family trips",
                commonsImage("2024 Mazda3 (BP) G20 Evolve sedan, Verdun, South Australia 2024-03-07.jpg"), 4, 5, "Gasoline", 2024,
                12500L, mazda, cityRental, true, "700000");
        saveCar("Toyota Corolla Cross", "Hybrid SUV with flexible luggage space",
                commonsImage("Toyota Corolla Cross 1.8 V Hybrid 2024 (1).jpg"), 5, 5, "Hybrid", 2024,
                9800L, toyota, premiumDrive, true, "950000");
        saveCar("Kia Carnival", "Large MPV for group and family travel",
                commonsImage("Kia Carnival 2.2 SX 2023 (1).jpg"), 5, 7, "Diesel", 2023,
                22100L, kia, premiumDrive, true, "1400000");
        saveCar("Ford Everest", "Seven-seat SUV for long-distance trips",
                commonsImage("2022 Ford Everest Sport 2.0L Turbo 4x2.jpg"), 5, 7, "Diesel", 2022,
                47600L, ford, premiumDrive, true, "1300000");
        saveCar("Honda CR-V", "Comfortable SUV with strong safety features",
                commonsImage("2021 Honda CR-V 1.5 Turbo Prestige (front right), Central Surabaya.jpg"), 5, 7, "Gasoline", 2021,
                68400L, honda, fuSupplier, false, "1100000");
        saveCar("Toyota Innova", "Practical MPV for city and airport transfer",
                commonsImage("Toyota Innova 2.8 V 2020.jpg"), 5, 7, "Gasoline", 2020,
                91200L, toyota, fuSupplier, true, "850000");
    }

    private Supplier saveSupplier(String name, String description, String address) {
        Supplier supplier = supplierRepository.findBySupplierName(name).orElseGet(Supplier::new);
        supplier.setSupplierName(name);
        supplier.setSupplierDescription(description);
        supplier.setSupplierAddress(address);
        return supplierRepository.save(supplier);
    }

    private Manufacturer saveManufacturer(String name, String description, String country) {
        Manufacturer manufacturer = manufacturerRepository.findByManufacturerName(name).orElseGet(Manufacturer::new);
        manufacturer.setManufacturerName(name);
        manufacturer.setDescription(description);
        manufacturer.setManufacturerCountry(country);
        return manufacturerRepository.save(manufacturer);
    }

    private void saveCar(String name,
                         String description,
                         String imageUrl,
                         Integer numberOfDoors,
                         Integer seatingCapacity,
                         String fuelType,
                         Integer year,
                         Long mileage,
                         Manufacturer manufacturer,
                         Supplier supplier,
                         Boolean status,
                         String pricePerDay) {
        CarInformation car = carRepository.findByCarName(name).orElseGet(CarInformation::new);
        car.setCarName(name);
        car.setCarDescription(description);
        car.setImageUrl(imageUrl);
        car.setNumberOfDoors(numberOfDoors);
        car.setSeatingCapacity(seatingCapacity);
        car.setFuelType(fuelType);
        car.setYear(year);
        car.setMileage(mileage);
        car.setManufacturerId(manufacturer.getManufacturerId());
        car.setSupplierId(supplier.getSupplierId());
        car.setCarStatus(status);
        car.setOperationalStatus(status ? "AVAILABLE" : "INACTIVE");
        car.setCarRentingPricePerDay(new BigDecimal(pricePerDay));
        carRepository.save(car);
    }

    private String commonsImage(String fileName) {
        return "https://commons.wikimedia.org/wiki/Special:Redirect/file/"
                + fileName.replace(" ", "%20") + "?width=1280";
    }
}
