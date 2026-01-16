package com.parking.dto;

import com.parking.User;
import jakarta.validation.constraints.*;
import lombok.*;


@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
}