package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private String lotId;
    private String type;
    private String startDate;
    private String endDate;
}