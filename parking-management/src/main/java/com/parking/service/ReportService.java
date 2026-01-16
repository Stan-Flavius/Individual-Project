package com.parking.service;

import com.parking.*;
import com.parking.dto.*;
import com.parking.repository.*;
import com.parking.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class ReportService {

    @Autowired
    private ParkingLotRepository lotRepository;

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public OccupancyReport generateOccupancyReport(String lotId, String startDate, String endDate)
            throws EntityNotFoundException {

        ParkingLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Parking lot not found: " + lotId));

        List<ParkingSpot> spots = spotRepository.findByLotId(lotId);
        long occupiedCount = spots.stream().filter(ParkingSpot::isOccupied).count();

        List<Reservation> reservations = reservationRepository.findByDateRange(startDate, endDate);
        double totalRevenue = reservations.stream()
                .mapToDouble(Reservation::calculateGrandTotal)
                .sum();

        double occupancyRate = spots.isEmpty() ? 0 : (occupiedCount * 100.0) / spots.size();

        return new OccupancyReport(
                lot.getLotId(),
                lot.getName(),
                spots.size(),
                (int) occupiedCount,
                spots.size() - (int) occupiedCount,
                occupancyRate,
                reservations.size(),
                totalRevenue
        );
    }
}