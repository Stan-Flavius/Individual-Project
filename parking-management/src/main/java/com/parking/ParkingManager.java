package com.parking;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ParkingManager {
    private final Map<String, ParkingLot> parkingLots;
    private final Map<String, ParkingSpot> parkingSpots;
    private final Map<String, Reservation> reservations;
    private final String dataFile;

    public ParkingManager(String dataFile) throws IOException {
        this.dataFile = dataFile;
        this.parkingLots = new HashMap<>();
        this.parkingSpots = new HashMap<>();
        this.reservations = new HashMap<>();
        loadDataFromFile();
    }


    public void addParkingLot(ParkingLot lot) throws DuplicateEntityException {
        if (parkingLots.containsKey(lot.getLotId())) {
            throw new DuplicateEntityException("Parking lot with ID " + lot.getLotId() + " already exists");
        }
        parkingLots.put(lot.getLotId(), lot);
    }


    public void addParkingSpot(ParkingSpot spot) throws DuplicateEntityException, EntityNotFoundException {
        if (parkingSpots.containsKey(spot.getSpotNumber())) {
            throw new DuplicateEntityException("Parking spot " + spot.getSpotNumber() + " already exists");
        }


        ParkingLot lot = parkingLots.get(spot.getLotId());
        if (lot == null) {
            throw new EntityNotFoundException("Parking lot with ID " + spot.getLotId() + " not found");
        }


        if (lot.isFull()) {
            throw new DuplicateEntityException("Parking lot " + spot.getLotId() + " is at full capacity");
        }

        parkingSpots.put(spot.getSpotNumber(), spot);
        lot.addSpot(spot.getSpotNumber());
    }


    public void createReservation(Reservation reservation)
            throws EntityNotFoundException, SpotUnavailableException, DuplicateEntityException {

        if (reservations.containsKey(reservation.getReservationId())) {
            throw new DuplicateEntityException("Reservation with ID " + reservation.getReservationId() + " already exists");
        }

        ParkingSpot spot = parkingSpots.get(reservation.getSpotNumber());
        if (spot == null) {
            throw new EntityNotFoundException("Parking spot " + reservation.getSpotNumber() + " not found");
        }


        spot.occupy(reservation.getReservationId());
        reservations.put(reservation.getReservationId(), reservation);
    }


    public void cancelReservation(String reservationId) throws EntityNotFoundException {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new EntityNotFoundException("Reservation with ID " + reservationId + " not found");
        }


        ParkingSpot spot = parkingSpots.get(reservation.getSpotNumber());
        if (spot != null) {
            spot.vacate();
        }

        reservation.cancel();
    }


    public Reservation findReservationById(String reservationId) throws EntityNotFoundException {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new EntityNotFoundException("Reservation with ID " + reservationId + " not found");
        }
        return reservation;
    }


    public ParkingLot findParkingLotById(String lotId) throws EntityNotFoundException {
        ParkingLot lot = parkingLots.get(lotId);
        if (lot == null) {
            throw new EntityNotFoundException("Parking lot with ID " + lotId + " not found");
        }
        return lot;
    }


    public int getAvailableSpots(String lotId) throws EntityNotFoundException {
        ParkingLot lot = findParkingLotById(lotId);
        int total = 0;
        int occupied = 0;

        for (ParkingSpot spot : parkingSpots.values()) {
            if (spot.getLotId().equals(lotId)) {
                total++;
                if (spot.isOccupied()) {
                    occupied++;
                }
            }
        }

        return total - occupied;
    }


    public void displayAllParkingLots() {
        System.out.println("\n=== All Parking Lots ===");
        if (parkingLots.isEmpty()) {
            System.out.println("No parking lots registered.");
        } else {
            for (ParkingLot lot : parkingLots.values()) {
                lot.display();
                System.out.println();
            }
        }
    }


    public void displayAllParkingSpots() {
        System.out.println("\n=== All Parking Spots ===");
        if (parkingSpots.isEmpty()) {
            System.out.println("No parking spots registered.");
        } else {
            for (ParkingSpot spot : parkingSpots.values()) {
                spot.display();
            }
        }
    }


    public void displayAllReservations() {
        System.out.println("\n=== All Reservations ===");
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (Reservation reservation : reservations.values()) {
                reservation.display();
                System.out.println();
            }
        }
    }


    private void loadDataFromFile() throws IOException {
        File file = new File(dataFile);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                if (line.equals("[PARKING_LOTS]")) {
                    section = "lots";
                    continue;
                } else if (line.equals("[PARKING_SPOTS]")) {
                    section = "spots";
                    continue;
                } else if (line.equals("[RESERVATIONS]")) {
                    section = "reservations";
                    continue;
                } else if (line.trim().isEmpty()) {
                    continue;
                }

                if (section.equals("lots")) {
                    ParkingLot lot = ParkingLot.fromCsvString(line);
                    parkingLots.put(lot.getLotId(), lot);
                } else if (section.equals("spots")) {
                    ParkingSpot spot = ParkingSpot.fromCsvString(line);
                    parkingSpots.put(spot.getSpotNumber(), spot);
                } else if (section.equals("reservations")) {
                    Reservation reservation = Reservation.fromCsvString(line);
                    reservations.put(reservation.getReservationId(), reservation);
                }
            }
        }
    }


    public void saveData() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            writer.write("[PARKING_LOTS]\n");
            for (ParkingLot lot : parkingLots.values()) {
                writer.write(lot.toCsvString() + "\n");
            }

            writer.write("\n[PARKING_SPOTS]\n");
            for (ParkingSpot spot : parkingSpots.values()) {
                writer.write(spot.toCsvString() + "\n");
            }

            writer.write("\n[RESERVATIONS]\n");
            for (Reservation reservation : reservations.values()) {
                writer.write(reservation.toCsvString() + "\n");
            }
        }
    }
}