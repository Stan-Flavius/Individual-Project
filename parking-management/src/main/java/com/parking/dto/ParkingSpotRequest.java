package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotRequest {
    @NotBlank(message = "Spot number is required")
    @Pattern(regexp = "^[A-Z]\\d{3}$", message = "Spot number must be 1 uppercase letter + 3 digits (e.g., A001)")
    private String spotNumber;

    @NotBlank(message = "Lot ID is required")
    private String lotId;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(REGULAR|VIP|DISABLED)$", message = "Type must be REGULAR, VIP, or DISABLED")
    private String type;
}