package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailableSpotDTO {
    private String spotNumber;
    private String lotId;
    private String lotName;
    private String location;
    private String type;
}
