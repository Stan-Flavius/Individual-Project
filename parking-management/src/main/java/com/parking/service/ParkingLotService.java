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
public class ParkingLotService {

    @Autowired
    private ParkingLotRepository lotRepository;

    @Autowired
    private ParkingSpotRepository spotRepository;

    public List<ParkingLotDTO> findAll() {
        return lotRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ParkingLotDTO findById(String lotId) throws EntityNotFoundException {
        ParkingLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Parking lot not found: " + lotId));
        return convertToDTO(lot);
    }

    @Transactional
    public ParkingLotDTO create(ParkingLotRequest request) throws DuplicateEntityException {
        if (lotRepository.existsById(request.getLotId())) {
            throw new DuplicateEntityException("Parking lot already exists: " + request.getLotId());
        }

        ParkingLot lot = new ParkingLot(
                request.getLotId(),
                request.getName(),
                request.getLocation(),
                request.getCapacity()
        );

        lot = lotRepository.save(lot);
        return convertToDTO(lot);
    }

    @Transactional
    public ParkingLotDTO update(String lotId, ParkingLotRequest request) throws EntityNotFoundException {
        ParkingLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Parking lot not found: " + lotId));


        lot = new ParkingLot(
                lot.getLotId(),
                request.getName(),
                request.getLocation(),
                request.getCapacity()
        );
        lot.getSpotNumbers().addAll(lotRepository.findById(lotId).get().getSpotNumbers());

        lot = lotRepository.save(lot);
        return convertToDTO(lot);
    }

    @Transactional
    public void delete(String lotId) throws EntityNotFoundException {
        if (!lotRepository.existsById(lotId)) {
            throw new EntityNotFoundException("Parking lot not found: " + lotId);
        }
        lotRepository.deleteById(lotId);
    }

    private ParkingLotDTO convertToDTO(ParkingLot lot) {
        return new ParkingLotDTO(
                lot.getLotId(),
                lot.getName(),
                lot.getLocation(),
                lot.getCapacity(),
                lot.getCurrentOccupancy(),
                lot.getAvailableCapacity()
        );
    }
}