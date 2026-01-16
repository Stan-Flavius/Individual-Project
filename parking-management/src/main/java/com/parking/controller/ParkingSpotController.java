package com.parking.controller;

import com.parking.*;
import com.parking.dto.*;
import com.parking.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
@RequestMapping("/parking-spots")
@CrossOrigin(origins = "*")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService parkingSpotService;


    @GetMapping
    public ResponseEntity<List<ParkingSpotDTO>> getAllSpots() {
        List<ParkingSpotDTO> spots = parkingSpotService.findAll();
        return ResponseEntity.ok(spots);
    }


    @GetMapping("/search")
    public ResponseEntity<List<AvailableSpotDTO>> searchAvailableSpots(
            @RequestParam String lotId,
            @RequestParam(required = false) String type) {

        List<AvailableSpotDTO> spots = parkingSpotService.searchAvailable(lotId, type);
        return ResponseEntity.ok(spots);
    }


    @GetMapping("/lot/{lotId}")
    public ResponseEntity<List<ParkingSpotDTO>> getSpotsByLot(@PathVariable String lotId) {
        List<ParkingSpotDTO> spots = parkingSpotService.findByLotId(lotId);
        return ResponseEntity.ok(spots);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> createParkingSpot(@Valid @RequestBody ParkingSpotRequest request) {
        try {
            ParkingSpotDTO spot = parkingSpotService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(spot);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}