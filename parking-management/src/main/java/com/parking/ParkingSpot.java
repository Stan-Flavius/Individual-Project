package com.parking;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_spots")
public class ParkingSpot implements CsvSerializable {

    @Id
    private String spotNumber;

    @Column(nullable = false)
    private String lotId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean isOccupied = false;

    @Column
    private String currentReservationId;


    public ParkingSpot() {}

    public ParkingSpot(String spotNumber, String lotId, String type) {
        this.spotNumber = spotNumber;
        this.lotId = lotId;
        this.type = type;
        this.isOccupied = false;
        this.currentReservationId = null;
    }


    public String getSpotNumber() { return spotNumber; }
    public String getLotId() { return lotId; }
    public String getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public String getCurrentReservationId() { return currentReservationId; }

    public void occupy(String reservationId) throws SpotUnavailableException {
        if (isOccupied) {
            throw new SpotUnavailableException("Spot " + spotNumber + " is already occupied");
        }
        this.isOccupied = true;
        this.currentReservationId = reservationId;
    }

    public void vacate() {
        this.isOccupied = false;
        this.currentReservationId = null;
    }

    public String getStatus() {
        return isOccupied ? "OCCUPIED" : "AVAILABLE";
    }

    public void display() {
        System.out.println("Spot: " + spotNumber + " | Lot: " + lotId +
                " | Type: " + type + " | Status: " + getStatus());
    }

    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Spot Number: ").append(spotNumber).append("\n");
        sb.append("Parking Lot: ").append(lotId).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Status: ").append(getStatus()).append("\n");
        if (isOccupied) {
            sb.append("Current Reservation: ").append(currentReservationId).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toCsvString() {
        return spotNumber + "," + lotId + "," + type + "," +
                isOccupied + "," + (currentReservationId != null ? currentReservationId : "");
    }

    public static ParkingSpot fromCsvString(String line) {
        String[] parts = line.split(",");
        ParkingSpot spot = new ParkingSpot(parts[0], parts[1], parts[2]);
        spot.isOccupied = Boolean.parseBoolean(parts[3]);
        if (parts.length > 4 && !parts[4].isEmpty()) {
            spot.currentReservationId = parts[4];
        }
        return spot;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotNumber='" + spotNumber + '\'' +
                ", lotId='" + lotId + '\'' +
                ", type='" + type + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}