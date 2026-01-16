package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
public class OccupancyReport {
    private String lotId;
    private String lotName;
    private int totalSpots;
    private int occupiedSpots;
    private int availableSpots;
    private double occupancyRate;
    private int totalReservations;
    private double totalRevenue;
}