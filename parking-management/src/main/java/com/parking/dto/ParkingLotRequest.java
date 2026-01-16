package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotRequest {
    @NotBlank(message = "Lot ID is required")
    @Pattern(regexp = "^LOT\\d{3,}$", message = "Lot ID must be in format LOT### (e.g., LOT001)")
    private String lotId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 1000, message = "Capacity cannot exceed 1000")
    private int capacity;
}