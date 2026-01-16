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
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Autowired
    private ParkingLotRepository lotRepository;

    public List<ParkingSpotDTO> findAll() {
        return spotRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ParkingSpotDTO> findByLotId(String lotId) {
        return spotRepository.findByLotId(lotId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailableSpotDTO> searchAvailable(String lotId, String type) {
        List<ParkingSpot> spots;

        if (type != null && !type.isEmpty()) {
            spots = spotRepository.findAvailableSpotsByLotIdAndType(lotId, type);
        } else {
            spots = spotRepository.findAvailableSpotsByLotId(lotId);
        }

        return spots.stream()
                .map(spot -> {
                    ParkingLot lot = lotRepository.findById(spot.getLotId()).orElse(null);
                    return new AvailableSpotDTO(
                            spot.getSpotNumber(),
                            spot.getLotId(),
                            lot != null ? lot.getName() : "",
                            lot != null ? lot.getLocation() : "",
                            spot.getType()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ParkingSpotDTO create(ParkingSpotRequest request)
            throws DuplicateEntityException, EntityNotFoundException {

        if (spotRepository.existsById(request.getSpotNumber())) {
            throw new DuplicateEntityException("Parking spot already exists: " + request.getSpotNumber());
        }

        ParkingLot lot = lotRepository.findById(request.getLotId())
                .orElseThrow(() -> new EntityNotFoundException("Parking lot not found: " + request.getLotId()));

        if (lot.isFull()) {
            throw new DuplicateEntityException("Parking lot is at full capacity");
        }

        ParkingSpot spot = new ParkingSpot(
                request.getSpotNumber(),
                request.getLotId(),
                request.getType()
        );

        spot = spotRepository.save(spot);
        lot.addSpot(spot.getSpotNumber());
        lotRepository.save(lot);

        return convertToDTO(spot);
    }

    private ParkingSpotDTO convertToDTO(ParkingSpot spot) {
        return new ParkingSpotDTO(
                spot.getSpotNumber(),
                spot.getLotId(),
                spot.getType(),
                spot.isOccupied(),
                spot.getStatus(),
                spot.getCurrentReservationId()
        );
    }
}
