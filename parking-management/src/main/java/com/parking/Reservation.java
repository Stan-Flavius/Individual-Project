package com.parking;

import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
public class Reservation implements CsvSerializable, Billable {

    @Id
    private String reservationId;

    @Column(nullable = false)
    private String spotNumber;

    @Column(nullable = false)
    private String vehiclePlate;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    @Column(nullable = false)
    private double dailyRate;

    @Column(nullable = false)
    private boolean isActive = true;


    public Reservation() {}

    public Reservation(String reservationId, String spotNumber, String vehiclePlate,
                       String customerName, String startDate, String endDate, double dailyRate) {
        this.reservationId = reservationId;
        this.spotNumber = spotNumber;
        this.vehiclePlate = vehiclePlate;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRate = dailyRate;
        this.isActive = true;
    }


    public String getReservationId() { return reservationId; }
    public String getSpotNumber() { return spotNumber; }
    public String getVehiclePlate() { return vehiclePlate; }
    public String getCustomerName() { return customerName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getDailyRate() { return dailyRate; }
    public boolean isActive() { return isActive; }

    public void cancel() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public String getStatus() {
        return isActive ? "ACTIVE" : "CANCELLED";
    }

    public long getDuration() {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public double calculateTotal() {
        return dailyRate * getDuration();
    }

    @Override
    public double calculateTax() {
        return calculateTotal() * 0.10;
    }

    @Override
    public double calculateGrandTotal() {
        return calculateTotal() + calculateTax();
    }

    public void display() {
        System.out.println("Reservation: " + reservationId + " | Customer: " + customerName);
        System.out.println("  Spot: " + spotNumber + " | Vehicle: " + vehiclePlate);
        System.out.println("  Period: " + startDate + " to " + endDate +
                " (" + getDuration() + " days)");
        System.out.println("  Status: " + getStatus() + " | Total: $" +
                String.format("%.2f", calculateTotal()));
    }

    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Reservation Details ===\n");
        sb.append("Reservation ID: ").append(reservationId).append("\n");
        sb.append("Customer Name: ").append(customerName).append("\n");
        sb.append("Vehicle Plate: ").append(vehiclePlate).append("\n");
        sb.append("Parking Spot: ").append(spotNumber).append("\n");
        sb.append("Start Date: ").append(startDate).append("\n");
        sb.append("End Date: ").append(endDate).append("\n");
        sb.append("Duration: ").append(getDuration()).append(" day(s)\n");
        sb.append("Daily Rate: $").append(String.format("%.2f", dailyRate)).append("\n");
        sb.append("Subtotal: $").append(String.format("%.2f", calculateTotal())).append("\n");
        sb.append("Tax (10%): $").append(String.format("%.2f", calculateTax())).append("\n");
        sb.append("Grand Total: $").append(String.format("%.2f", calculateGrandTotal())).append("\n");
        sb.append("Status: ").append(getStatus()).append("\n");
        return sb.toString();
    }

    @Override
    public String toCsvString() {
        return reservationId + "," + spotNumber + "," + vehiclePlate + "," +
                customerName + "," + startDate + "," + endDate + "," +
                dailyRate + "," + isActive;
    }

    public static Reservation fromCsvString(String line) {
        String[] parts = line.split(",");
        Reservation reservation = new Reservation(
                parts[0], parts[1], parts[2], parts[3],
                parts[4], parts[5], Double.parseDouble(parts[6])
        );
        reservation.isActive = Boolean.parseBoolean(parts[7]);
        return reservation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + reservationId + '\'' +
                ", spot='" + spotNumber + '\'' +
                ", customer='" + customerName + '\'' +
                ", period=" + startDate + " to " + endDate +
                ", status=" + getStatus() +
                '}';
    }
}