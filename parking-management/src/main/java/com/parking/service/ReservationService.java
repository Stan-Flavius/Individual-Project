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
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ParkingSpotRepository spotRepository;

    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO findById(String reservationId) throws EntityNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found: " + reservationId));
        return convertToDTO(reservation);
    }

    public List<ReservationDTO> findByCustomerName(String customerName) {
        return reservationRepository.findByCustomerName(customerName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDTO create(ReservationRequest request)
            throws DuplicateEntityException, EntityNotFoundException, SpotUnavailableException {

        if (reservationRepository.existsById(request.getReservationId())) {
            throw new DuplicateEntityException("Reservation already exists: " + request.getReservationId());
        }

        ParkingSpot spot = spotRepository.findById(request.getSpotNumber())
                .orElseThrow(() -> new EntityNotFoundException("Parking spot not found: " + request.getSpotNumber()));

        Reservation reservation = new Reservation(
                request.getReservationId(),
                request.getSpotNumber(),
                request.getVehiclePlate(),
                request.getCustomerName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDailyRate()
        );

        spot.occupy(reservation.getReservationId());
        spotRepository.save(spot);

        reservation = reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }


    @Async
    public CompletableFuture<ReservationDTO> createAsync(ReservationRequest request)
            throws DuplicateEntityException, EntityNotFoundException, SpotUnavailableException {

        ReservationDTO result = create(request);
        return CompletableFuture.completedFuture(result);
    }

    @Transactional
    public void cancel(String reservationId) throws EntityNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found: " + reservationId));

        ParkingSpot spot = spotRepository.findById(reservation.getSpotNumber()).orElse(null);
        if (spot != null) {
            spot.vacate();
            spotRepository.save(spot);
        }

        reservation.cancel();
        reservationRepository.save(reservation);
    }

    private ReservationDTO convertToDTO(Reservation res) {
        return new ReservationDTO(
                res.getReservationId(),
                res.getSpotNumber(),
                res.getVehiclePlate(),
                res.getCustomerName(),
                res.getStartDate(),
                res.getEndDate(),
                res.getDailyRate(),
                res.getDuration(),
                res.calculateTotal(),
                res.calculateTax(),
                res.calculateGrandTotal(),
                res.getStatus()
        );
    }
}