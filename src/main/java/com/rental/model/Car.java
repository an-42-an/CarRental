package com.rental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    String id;
    String model;
    double dailyRate;
    boolean available = true;
    @Column(length = 1000000) // Large column for Base64 image
    String image; // Base64 encoded image
    public Car() {}
    public Car(String id, String model, double dailyRate) {
        this.id = id;
        this.model = model;
        this.dailyRate = dailyRate;
    }
    
    public String getId() { return id; }
    public String getModel() { return model; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}