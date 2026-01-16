package com.parking;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String mode = "normal";
        String dataFile = "parking_data.txt";


        if (args.length > 0) {
            mode = args[0];
        }
        if (args.length > 1) {
            dataFile = args[1];
        }

        System.out.println("=== Parking Management System ===");
        System.out.println("Mode: " + mode);
        System.out.println("Data file: " + dataFile);
        System.out.println();

        try {
            ParkingManager manager = new ParkingManager(dataFile);

            if (mode.equals("demo")) {
                runDemoMode(manager);
            } else {
                runInteractiveMode(manager);
            }

        } catch (FileNotFoundException e) {

            System.err.println("Error: Data file not found - " + e.getMessage());
            System.out.println("Creating new data file...");
            try {
                new File(dataFile).createNewFile();
                ParkingManager manager = new ParkingManager(dataFile);
                runInteractiveMode(manager);
            } catch (IOException ex) {
                System.err.println("Fatal error creating file: " + ex.getMessage());
            }
        } catch (IOException e) {

            System.err.println("Error reading/writing data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runDemoMode(ParkingManager manager) {
        System.out.println("Running in DEMO mode with sample data...\n");

        try {

            manager.addParkingLot(new ParkingLot("LOT001", "Downtown Mall", "123 Main St", 50));
            manager.addParkingLot(new ParkingLot("LOT002", "Airport Parking", "Airport Terminal", 200));
            manager.addParkingLot(new ParkingLot("LOT003", "City Center", "456 Central Ave", 100));


            manager.addParkingSpot(new ParkingSpot("A001", "LOT001", "REGULAR"));
            manager.addParkingSpot(new ParkingSpot("A002", "LOT001", "REGULAR"));
            manager.addParkingSpot(new ParkingSpot("A003", "LOT001", "DISABLED"));
            manager.addParkingSpot(new ParkingSpot("B001", "LOT002", "VIP"));
            manager.addParkingSpot(new ParkingSpot("B002", "LOT002", "REGULAR"));


            manager.createReservation(new Reservation("R001", "A001", "ABC123",
                    "John Doe", "2025-12-05", "2025-12-05", 10.0));
            manager.createReservation(new Reservation("R002", "B001", "XYZ789",
                    "Jane Smith", "2025-12-06", "2025-12-08", 75.0));


            manager.displayAllParkingLots();
            manager.displayAllParkingSpots();
            manager.displayAllReservations();

            manager.saveData();
            System.out.println("\nDemo data saved successfully!");

        } catch (DuplicateEntityException | EntityNotFoundException | SpotUnavailableException e) {
            System.err.println("Demo error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving demo data: " + e.getMessage());
        }
    }

    private static void runInteractiveMode(ParkingManager manager) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Add Parking Lot");
            System.out.println("2. Add Parking Spot");
            System.out.println("3. Create Reservation");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Display All Parking Lots");
            System.out.println("6. Display All Parking Spots");
            System.out.println("7. Display All Reservations");
            System.out.println("8. Search Reservation");
            System.out.println("9. Check Available Spots");
            System.out.println("10. Save and Exit");
            System.out.print("Choose option: ");

            try {
                String input = scanner.nextLine().trim();


                if (!InputValidator.isValidMenuOption(input, 1, 10)) {
                    System.out.println("Invalid option. Please enter a number between 1 and 10.");
                    continue;
                }

                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> addParkingLotInteractive(scanner, manager);
                    case 2 -> addParkingSpotInteractive(scanner, manager);
                    case 3 -> createReservationInteractive(scanner, manager);
                    case 4 -> cancelReservationInteractive(scanner, manager);
                    case 5 -> manager.displayAllParkingLots();
                    case 6 -> manager.displayAllParkingSpots();
                    case 7 -> manager.displayAllReservations();
                    case 8 -> searchReservationInteractive(scanner, manager);
                    case 9 -> checkAvailableSpotsInteractive(scanner, manager);
                    case 10 -> {
                        manager.saveData();
                        System.out.println("Data saved. Goodbye!");
                        running = false;
                    }
                }
            } catch (NumberFormatException e) {

                System.out.println("Error: Please enter a valid number.");
            } catch (DuplicateEntityException | EntityNotFoundException |
                     InvalidInputException | SpotUnavailableException e) {

                System.err.println("Operation failed: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error saving data: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void addParkingLotInteractive(Scanner scanner, ParkingManager manager)
            throws DuplicateEntityException, InvalidInputException {
        System.out.print("Enter Lot ID: ");
        String lotId = scanner.nextLine().trim();
        InputValidator.validateLotId(lotId);

        System.out.print("Enter Lot Name: ");
        String name = scanner.nextLine().trim();
        InputValidator.validateName(name);

        System.out.print("Enter Location: ");
        String location = scanner.nextLine().trim();
        InputValidator.validateName(location);

        System.out.print("Enter Capacity: ");
        String capacityStr = scanner.nextLine().trim();
        InputValidator.validateCapacity(capacityStr);
        int capacity = Integer.parseInt(capacityStr);

        ParkingLot lot = new ParkingLot(lotId, name, location, capacity);
        manager.addParkingLot(lot);
        System.out.println("Parking lot added successfully!");
    }

    private static void addParkingSpotInteractive(Scanner scanner, ParkingManager manager)
            throws DuplicateEntityException, InvalidInputException, EntityNotFoundException {
        System.out.print("Enter Spot Number: ");
        String spotNumber = scanner.nextLine().trim();
        InputValidator.validateSpotNumber(spotNumber);

        System.out.print("Enter Lot ID: ");
        String lotId = scanner.nextLine().trim();
        InputValidator.validateLotId(lotId);

        System.out.print("Enter Spot Type (REGULAR/VIP/DISABLED): ");
        String type = scanner.nextLine().trim().toUpperCase();
        InputValidator.validateSpotType(type);

        ParkingSpot spot = new ParkingSpot(spotNumber, lotId, type);
        manager.addParkingSpot(spot);
        System.out.println("Parking spot added successfully!");
    }

    private static void createReservationInteractive(Scanner scanner, ParkingManager manager)
            throws EntityNotFoundException, SpotUnavailableException, InvalidInputException, DuplicateEntityException {
        System.out.print("Enter Reservation ID: ");
        String resId = scanner.nextLine().trim();
        InputValidator.validateReservationId(resId);

        System.out.print("Enter Spot Number: ");
        String spotNumber = scanner.nextLine().trim();
        InputValidator.validateSpotNumber(spotNumber);

        System.out.print("Enter Vehicle Plate: ");
        String plate = scanner.nextLine().trim();
        InputValidator.validatePlateNumber(plate);

        System.out.print("Enter Customer Name: ");
        String customer = scanner.nextLine().trim();
        InputValidator.validateName(customer);

        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();
        InputValidator.validateDate(startDate);

        System.out.print("Enter End Date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();
        InputValidator.validateDate(endDate);

        System.out.print("Enter Price: ");
        String priceStr = scanner.nextLine().trim();
        InputValidator.validatePrice(priceStr);
        double price = Double.parseDouble(priceStr);

        Reservation reservation = new Reservation(resId, spotNumber, plate,
                customer, startDate, endDate, price);
        manager.createReservation(reservation);
        System.out.println("Reservation created successfully!");
        System.out.println("Total cost: $" + reservation.calculateTotal());
    }

    private static void cancelReservationInteractive(Scanner scanner, ParkingManager manager)
            throws EntityNotFoundException {
        System.out.print("Enter Reservation ID: ");
        String resId = scanner.nextLine().trim();

        manager.cancelReservation(resId);
        System.out.println("Reservation cancelled successfully!");
    }

    private static void searchReservationInteractive(Scanner scanner, ParkingManager manager)
            throws EntityNotFoundException {
        System.out.print("Enter Reservation ID: ");
        String resId = scanner.nextLine().trim();

        Reservation reservation = manager.findReservationById(resId);
        System.out.println("\n" + reservation.getDetailedInfo());
    }

    private static void checkAvailableSpotsInteractive(Scanner scanner, ParkingManager manager)
            throws EntityNotFoundException {
        System.out.print("Enter Lot ID: ");
        String lotId = scanner.nextLine().trim();

        int available = manager.getAvailableSpots(lotId);
        System.out.println("Available spots in " + lotId + ": " + available);
    }
}