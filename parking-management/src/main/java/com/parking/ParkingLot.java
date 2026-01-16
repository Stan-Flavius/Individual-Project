package com.parking;

import jakarta.persistence.*;



@Entity
@Table(name = "parking_lots")
public class ParkingLot implements CsvSerializable {

    @Id
    private String lotId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int capacity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lot_spots", joinColumns = @JoinColumn(name = "lot_id"))
    @Column(name = "spot_number")
    private java.util.Set<String> spotNumbers = new java.util.HashSet<>();


    public ParkingLot() {}

    public ParkingLot(String lotId, String name, String location, int capacity) {
        this.lotId = lotId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.spotNumbers = new java.util.HashSet<>();
    }


    public String getLotId() { return lotId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public java.util.Set<String> getSpotNumbers() { return spotNumbers; }

    public void addSpot(String spotNumber) {
        spotNumbers.add(spotNumber);
    }

    public void removeSpot(String spotNumber) {
        spotNumbers.remove(spotNumber);
    }

    public int getCurrentOccupancy() {
        return spotNumbers.size();
    }

    public boolean isFull() {
        return spotNumbers.size() >= capacity;
    }

    public int getAvailableCapacity() {
        return capacity - spotNumbers.size();
    }

    public void display() {
        System.out.println("Parking Lot: " + name + " (" + lotId + ")");
        System.out.println("  Location: " + location);
        System.out.println("  Capacity: " + capacity + " | Current Spots: " +
                spotNumbers.size() + " | Available: " + getAvailableCapacity());
    }

    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lot ID: ").append(lotId).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Location: ").append(location).append("\n");
        sb.append("Total Capacity: ").append(capacity).append("\n");
        sb.append("Current Spots Registered: ").append(spotNumbers.size()).append("\n");
        sb.append("Available Capacity: ").append(getAvailableCapacity()).append("\n");
        if (!spotNumbers.isEmpty()) {
            sb.append("Spot Numbers: ").append(String.join(", ", spotNumbers)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toCsvString() {
        return lotId + "," + name + "," + location + "," + capacity + "," +
                String.join(";", spotNumbers);
    }

    public static ParkingLot fromCsvString(String line) {
        String[] parts = line.split(",");
        ParkingLot lot = new ParkingLot(parts[0], parts[1], parts[2],
                Integer.parseInt(parts[3]));
        if (parts.length > 4 && !parts[4].isEmpty()) {
            String[] spots = parts[4].split(";");
            for (String spot : spots) {
                lot.addSpot(spot);
            }
        }
        return lot;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "lotId='" + lotId + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", spots=" + spotNumbers.size() +
                '}';
    }
}