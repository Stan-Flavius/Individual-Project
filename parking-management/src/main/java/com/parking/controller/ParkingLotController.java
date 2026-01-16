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
@RequestMapping("/parking-lots")
@CrossOrigin(origins = "*")
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;


    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAllParkingLots() {
        List<ParkingLotDTO> lots = parkingLotService.findAll();
        return ResponseEntity.ok(lots);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getParkingLotById(@PathVariable String id) {
        try {
            ParkingLotDTO lot = parkingLotService.findById(id);
            return ResponseEntity.ok(lot);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createParkingLot(@Valid @RequestBody ParkingLotRequest request) {
        try {
            ParkingLotDTO lot = parkingLotService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(lot);
        } catch (DuplicateEntityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateParkingLot(@PathVariable String id,
                                              @Valid @RequestBody ParkingLotRequest request) {
        try {
            ParkingLotDTO lot = parkingLotService.update(id, request);
            return ResponseEntity.ok(lot);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteParkingLot(@PathVariable String id) {
        try {
            parkingLotService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
