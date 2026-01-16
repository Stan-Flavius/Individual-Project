package com.parking.repository;

import com.parking.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, String> {
    List<ParkingSpot> findByLotId(String lotId);
    List<ParkingSpot> findByType(String type);
    List<ParkingSpot> findByIsOccupied(boolean isOccupied);

    @Query("SELECT ps FROM ParkingSpot ps WHERE ps.lotId = :lotId AND ps.isOccupied = false")
    List<ParkingSpot> findAvailableSpotsByLotId(@Param("lotId") String lotId);

    @Query("SELECT ps FROM ParkingSpot ps WHERE ps.lotId = :lotId AND ps.type = :type AND ps.isOccupied = false")
    List<ParkingSpot> findAvailableSpotsByLotIdAndType(@Param("lotId") String lotId, @Param("type") String type);
}