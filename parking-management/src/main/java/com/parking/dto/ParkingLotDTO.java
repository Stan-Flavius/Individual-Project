package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;


@Data
@AllArgsConstructor
public class ParkingLotDTO {
    private String lotId;
    private String name;
    private String location;
    private int capacity;
    private int currentOccupancy;
    private int availableCapacity;
}