package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotBlank(message = "Reservation ID is required")
    @Pattern(regexp = "^R\\d{3,}$", message = "Reservation ID must be in format R### (e.g., R001)")
    private String reservationId;

    @NotBlank(message = "Spot number is required")
    private String spotNumber;

    @NotBlank(message = "Vehicle plate is required")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Plate must be 3-10 alphanumeric characters")
    private String vehiclePlate;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Start date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format YYYY-MM-DD")
    private String startDate;

    @NotBlank(message = "End date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format YYYY-MM-DD")
    private String endDate;

    @Min(value = 0, message = "Daily rate must be positive")
    @Max(value = 10000, message = "Daily rate cannot exceed 10000")
    private double dailyRate;
}
