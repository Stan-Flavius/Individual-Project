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
@RequestMapping("/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable String id) {
        try {
            ReservationDTO reservation = reservationService.findById(id);
            return ResponseEntity.ok(reservation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/history")
    public ResponseEntity<List<ReservationDTO>> getReservationHistory(
            @RequestParam String customerName) {
        List<ReservationDTO> reservations = reservationService.findByCustomerName(customerName);
        return ResponseEntity.ok(reservations);
    }


    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request) {
        try {
            ReservationDTO reservation = reservationService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (SpotUnavailableException | DuplicateEntityException | EntityNotFoundException e) {

            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.internalServerError().body("System Error: " + e.getMessage());
        }
    }


    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<?>> createReservationAsync(
            @Valid @RequestBody ReservationRequest request) {
        try {
            return reservationService.createAsync(request)
                    .<ResponseEntity<?>>thenApply(reservation ->
                            ResponseEntity.status(HttpStatus.CREATED).body(reservation))
                    .exceptionally(ex ->
                            ResponseEntity.badRequest().body("Async Error: " + ex.getMessage()));

        } catch (SpotUnavailableException | DuplicateEntityException | EntityNotFoundException e) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body("Validation Error: " + e.getMessage())
            );
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.internalServerError().body("System Error: " + e.getMessage())
            );
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable String id) {
        try {
            reservationService.cancel(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}