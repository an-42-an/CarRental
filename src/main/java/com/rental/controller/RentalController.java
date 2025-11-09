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
    
    // Login endpoint
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        
        // Check for admin
        if (username.equals("admin") && password.equals("password")) {
            response.put("success", true);
            response.put("role", "admin");
            response.put("message", "Admin login successful");
            return response;
        }
        
        // Check for customer
        Optional<Customer> customer = custRepo.findByUsernameAndPassword(username, password);
        if (customer.isPresent()) {
            response.put("success", true);
            response.put("role", "customer");
            response.put("customerId", customer.get().getId());
            response.put("name", customer.get().getName());
            response.put("message", "Customer login successful");
            return response;
        }
        
        response.put("success", false);
        response.put("message", "Invalid credentials");
        return response;
    }
    
    // Update customer endpoint
    @PostMapping("/customers")
        public Customer addCustomer(@RequestParam String name, 
                                    @RequestParam String phone,
                                    @RequestParam String username,
                                    @RequestParam String password) {
            Customer customer = new Customer(name, phone, username, password);
            return custRepo.save(customer);
        }
        
        // ... rest of your existing methods (getCars, rentCar, etc.)
    @GetMapping("/cars")
    public List<Car> getAvailableCars() {
        return carRepo.findAll().stream().filter(Car::isAvailable).toList();
    }
    
    @PostMapping("/cars")
    public Car addCar(@RequestBody Map<String, String> carData) {
        String id = carData.get("id");
        String model = carData.get("model");
        double dailyRate = Double.parseDouble(carData.get("dailyRate"));
        String image = carData.get("image");
        
        Car car = new Car(id, model, dailyRate);
        if (image != null && !image.isEmpty()) {
            car.setImage(image);
        }
        return carRepo.save(car);
    }
    
    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return custRepo.findAll();
    }
    
    @PostMapping("/rentals")
    public Map<String, Object> rentCar(@RequestParam Long customerId, @RequestParam String carId, @RequestParam int days) {
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