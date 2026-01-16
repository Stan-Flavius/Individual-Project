package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@AllArgsConstructor
public class ReservationDTO {
    private String reservationId;
    private String spotNumber;
    private String vehiclePlate;
    private String customerName;
    private String startDate;
    private String endDate;
    private double dailyRate;
    private long duration;
    private double total;
    private double tax;
    private double grandTotal;
    private String status;
}