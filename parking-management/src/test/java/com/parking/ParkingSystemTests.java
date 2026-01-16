package com.parking;

import com.parking.InvalidInputException.*;       // Adjust if your classes are in subpackages
import com.parking.SpotUnavailableException.*;
import com.parking.DuplicateEntityException.*;
import com.parking.EntityNotFoundException.*;    // Import your custom exceptions
import com.parking.service.*;      // Import services if needed
import org.junit.jupiter.api.Test; // JUnit 5 Test annotation
import static org.junit.jupiter.api.Assertions.*; // JUnit 5 Assertions

/**
 * Requirement 2 (2p): Unit tests
 * Test constructors and functionality using JUnit 5
 */
public class ParkingSystemTests {

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    public void testParkingLotConstructor() {
        ParkingLot lot = new ParkingLot("LOT001", "Downtown", "123 Main St", 100);

        assertEquals("LOT001", lot.getLotId());
        assertEquals("Downtown", lot.getName());
        assertEquals("123 Main St", lot.getLocation());
        assertEquals(100, lot.getCapacity());
        assertEquals(0, lot.getCurrentOccupancy());
        assertNotNull(lot.getSpotNumbers());
    }

    @Test
    public void testParkingSpotConstructor() {
        ParkingSpot spot = new ParkingSpot("A001", "LOT001", "REGULAR");

        assertEquals("A001", spot.getSpotNumber());
        assertEquals("LOT001", spot.getLotId());
        assertEquals("REGULAR", spot.getType());
        assertFalse(spot.isOccupied());
        assertNull(spot.getCurrentReservationId());
        assertEquals("AVAILABLE", spot.getStatus());
    }

    @Test
    public void testReservationConstructor() {
        Reservation res = new Reservation(
                "R001", "A001", "ABC123", "John Doe",
                "2025-12-01", "2025-12-05", 10.0
        );

        assertEquals("R001", res.getReservationId());
        assertEquals("A001", res.getSpotNumber());
        assertEquals("ABC123", res.getVehiclePlate());
        assertEquals("John Doe", res.getCustomerName());
        assertEquals("2025-12-01", res.getStartDate());
        assertEquals("2025-12-05", res.getEndDate());
        assertEquals(10.0, res.getDailyRate(), 0.01);
        assertTrue(res.isActive());
        assertEquals("ACTIVE", res.getStatus());
    }

    // ========== FUNCTIONAL TESTS ==========

    @Test
    public void testParkingSpotOccupy() throws SpotUnavailableException {
        ParkingSpot spot = new ParkingSpot("A001", "LOT001", "REGULAR");

        assertFalse(spot.isOccupied());

        spot.occupy("R001");

        assertTrue(spot.isOccupied());
        assertEquals("OCCUPIED", spot.getStatus());
        assertEquals("R001", spot.getCurrentReservationId());
    }

    @Test
    public void testParkingSpotVacate() throws SpotUnavailableException {
        ParkingSpot spot = new ParkingSpot("A001", "LOT001", "REGULAR");
        spot.occupy("R001");

        assertTrue(spot.isOccupied());

        spot.vacate();

        assertFalse(spot.isOccupied());
        assertEquals("AVAILABLE", spot.getStatus());
        assertNull(spot.getCurrentReservationId());
    }

    @Test
    public void testDoubleOccupyThrowsException() throws SpotUnavailableException {
        ParkingSpot spot = new ParkingSpot("A001", "LOT001", "REGULAR");
        spot.occupy("R001");

        // JUnit 5 way to check for exceptions:
        assertThrows(SpotUnavailableException.class, () -> {
            spot.occupy("R002"); // Should throw exception
        });
    }

    @Test
    public void testReservationCalculations() {
        Reservation res = new Reservation(
                "R001", "A001", "ABC123", "John Doe",
                "2025-12-01", "2025-12-05", 20.0
        );

        // Duration: 5 days (inclusive)
        assertEquals(5, res.getDuration());

        // Total = 20.0 * 5 = 100.0
        assertEquals(100.0, res.calculateTotal(), 0.01);

        // Tax = 10% of total = 10.0
        assertEquals(10.0, res.calculateTax(), 0.01);

        // Grand total = 110.0
        assertEquals(110.0, res.calculateGrandTotal(), 0.01);
    }

    @Test
    public void testReservationCancellation() {
        Reservation res = new Reservation(
                "R001", "A001", "ABC123", "John Doe",
                "2025-12-01", "2025-12-05", 10.0
        );

        assertTrue(res.isActive());
        assertEquals("ACTIVE", res.getStatus());

        res.cancel();

        assertFalse(res.isActive());
        assertEquals("CANCELLED", res.getStatus());
    }

    @Test
    public void testParkingLotCapacityManagement() {
        ParkingLot lot = new ParkingLot("LOT001", "Downtown", "123 Main St", 3);

        assertEquals(3, lot.getAvailableCapacity());
        assertFalse(lot.isFull());
        assertEquals(0, lot.getCurrentOccupancy());

        lot.addSpot("A001");
        assertEquals(2, lot.getAvailableCapacity());
        assertEquals(1, lot.getCurrentOccupancy());

        lot.addSpot("A002");
        lot.addSpot("A003");

        assertEquals(0, lot.getAvailableCapacity());
        assertTrue(lot.isFull());
        assertEquals(3, lot.getCurrentOccupancy());

        lot.removeSpot("A001");

        assertEquals(1, lot.getAvailableCapacity());
        assertFalse(lot.isFull());
        assertEquals(2, lot.getCurrentOccupancy());
    }

    @Test
    public void testInputValidatorLotId() {
        assertDoesNotThrow(() -> InputValidator.validateLotId("LOT001"));

        Exception exception1 = assertThrows(InvalidInputException.class, () -> {
            InputValidator.validateLotId("INVALID");
        });
        assertTrue(exception1.getMessage().contains("LOT"));

        Exception exception2 = assertThrows(InvalidInputException.class, () -> {
            InputValidator.validateLotId("LOT12"); // Too short
        });
        assertTrue(exception2.getMessage().contains("3 digits"));
    }

    @Test
    public void testInputValidatorSpotNumber() {
        assertDoesNotThrow(() -> InputValidator.validateSpotNumber("A001"));

        Exception exception1 = assertThrows(InvalidInputException.class, () -> {
            InputValidator.validateSpotNumber("123");
        });
        assertTrue(exception1.getMessage().contains("uppercase letter"));

        Exception exception2 = assertThrows(InvalidInputException.class, () -> {
            InputValidator.validateSpotNumber("AB01");
        });
        assertTrue(exception2.getMessage().contains("1 uppercase letter"));
    }

    @Test
    public void testCsvSerialization() {
        ParkingLot lot = new ParkingLot("LOT001", "Downtown", "123 Main St", 50);
        lot.addSpot("A001");
        lot.addSpot("A002");

        String csv = lot.toCsvString();

        assertTrue(csv.contains("LOT001"));
        assertTrue(csv.contains("Downtown"));
        assertTrue(csv.contains("A001"));

        ParkingLot reconstructed = ParkingLot.fromCsvString(csv);

        assertEquals(lot.getLotId(), reconstructed.getLotId());
        assertEquals(lot.getName(), reconstructed.getName());
        assertEquals(lot.getLocation(), reconstructed.getLocation());
        assertEquals(2, reconstructed.getSpotNumbers().size());
    }

    @Test
    public void testReservationDurationCalculation() {
        // Same day reservation
        Reservation res1 = new Reservation(
                "R001", "A001", "ABC123", "John",
                "2025-12-05", "2025-12-05", 10.0
        );
        assertEquals(1, res1.getDuration());

        // Multi-day reservation
        Reservation res2 = new Reservation(
                "R002", "A002", "XYZ789", "Jane",
                "2025-12-01", "2025-12-10", 15.0
        );
        assertEquals(10, res2.getDuration());
    }

    @Test
    public void testBillableInterface() {
        Reservation res = new Reservation(
                "R001", "A001", "ABC123", "John Doe",
                "2025-12-01", "2025-12-03", 50.0
        );

        // Test Billable interface methods
        assertTrue(res instanceof Billable);

        double total = res.calculateTotal();
        double tax = res.calculateTax();
        double grandTotal = res.calculateGrandTotal();

        assertEquals(150.0, total, 0.01); // 50 * 3
        assertEquals(15.0, tax, 0.01);     // 10% of 150
        assertEquals(165.0, grandTotal, 0.01); // 150 + 15
    }
}