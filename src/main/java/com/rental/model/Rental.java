package com.rental.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    String id;
    Long customerId;
    String carId;
    int days;
    double totalCost;
    @Temporal(TemporalType.TIMESTAMP)
    Date startDate = new Date();
    
    public Rental() {}
    public Rental(String id, Long customerId, String carId, int days, double totalCost) {
        this.id = id;
        this.customerId = customerId;
        this.carId = carId;
        this.days = days;
        this.totalCost = totalCost;
    }
    
    public String getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public String getCarId() { return carId; }
    public int getDays() { return days; }
    public double getTotalCost() { return totalCost; }
    public Date getStartDate() { return startDate; }
}