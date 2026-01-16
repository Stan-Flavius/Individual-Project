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
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByCustomerName(String customerName);
    List<Reservation> findByVehiclePlate(String vehiclePlate);
    List<Reservation> findByIsActive(boolean isActive);
    List<Reservation> findBySpotNumber(String spotNumber);

    @Query("SELECT r FROM Reservation r WHERE r.startDate >= :startDate AND r.endDate <= :endDate")
    List<Reservation> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.spotNumber = :spotNumber AND r.isActive = true")
    long countActiveReservationsBySpot(@Param("spotNumber") String spotNumber);
}