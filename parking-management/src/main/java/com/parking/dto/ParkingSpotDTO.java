package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;


@Data
@AllArgsConstructor
public class ParkingSpotDTO {
    private String spotNumber;
    private String lotId;
    private String type;
    private boolean isOccupied;
    private String status;
    private String currentReservationId;
}
