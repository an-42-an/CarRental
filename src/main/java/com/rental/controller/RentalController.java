package com.rental.controller;

import com.rental.model.*;
import com.rental.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RentalController {
    @Autowired
    CarRepository carRepo;
    @Autowired
    CustomerRepository custRepo;
    @Autowired
    RentalRepository rentalRepo;
    
    private int rentalCounter = 1;
    
    @GetMapping("/cars")
    public List<Car> getAvailableCars() {
        return carRepo.findAll().stream().filter(Car::isAvailable).toList();
    }
    
    @PostMapping("/cars")
    public Car addCar(@RequestParam String id, @RequestParam String model, @RequestParam double dailyRate) {
        return carRepo.save(new Car(id, model, dailyRate));
    }
    
    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return custRepo.findAll();
    }
    
    @PostMapping("/customers")
    public Customer addCustomer(@RequestParam String id, @RequestParam String name, @RequestParam String phone) {
        return custRepo.save(new Customer(id, name, phone));
    }
    
    @PostMapping("/rentals")
    public Map<String, Object> rentCar(@RequestParam String customerId, @RequestParam String carId, @RequestParam int days) {
        Map<String, Object> response = new HashMap<>();
        
        if (!custRepo.existsById(customerId)) {
            response.put("error", "Customer not found");
            return response;
        }
        if (!carRepo.existsById(carId)) {
            response.put("error", "Car not found");
            return response;
        }
        
        Car car = carRepo.findById(carId).get();
        if (!car.isAvailable()) {
            response.put("error", "Car not available");
            return response;
        }
        
        double totalCost = car.getDailyRate() * days;
        String rentalId = "R" + rentalCounter++;
        
        car.setAvailable(false);
        carRepo.save(car);
        
        Rental rental = rentalRepo.save(new Rental(rentalId, customerId, carId, days, totalCost));
        response.put("rentalId", rental.getId());
        response.put("totalCost", totalCost);
        response.put("days", days);
        return response;
    }
    
    @DeleteMapping("/rentals/{rentalId}")
    public Map<String, String> returnCar(@PathVariable String rentalId) {
        Map<String, String> response = new HashMap<>();
        
        if (!rentalRepo.existsById(rentalId)) {
            response.put("error", "Rental not found");
            return response;
        }
        
        Rental rental = rentalRepo.findById(rentalId).get();
        Car car = carRepo.findById(rental.getCarId()).get();
        
        car.setAvailable(true);
        carRepo.save(car);
        rentalRepo.deleteById(rentalId);
        
        response.put("message", "Car returned successfully");
        response.put("cost", String.valueOf(rental.getTotalCost()));
        return response;
    }
    
    @GetMapping("/rentals")
    public List<Rental> getActiveRentals() {
        return rentalRepo.findAll();
    }
}