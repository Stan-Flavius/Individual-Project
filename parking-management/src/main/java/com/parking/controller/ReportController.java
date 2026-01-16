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
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;


    @GetMapping("/occupancy")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getOccupancyReport(
            @RequestParam String lotId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            OccupancyReport report = reportService.generateOccupancyReport(lotId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}