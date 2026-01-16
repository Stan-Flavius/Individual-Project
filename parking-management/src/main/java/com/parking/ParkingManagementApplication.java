package com.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class ParkingManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingManagementApplication.class, args);

        System.out.println("\n===========================================");
        System.out.println("Parking Management System Started");
        System.out.println("===========================================");
        System.out.println("REST API: http://localhost:8080");
        System.out.println("Socket Server: localhost:8888");
        System.out.println("===========================================\n");
    }
}