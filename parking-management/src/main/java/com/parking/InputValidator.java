package com.parking;


public class InputValidator {

    public static boolean isValidMenuOption(String input, int min, int max) {
        try {
            int num = Integer.parseInt(input);
            return num >= min && num <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void validateLotId(String id) throws InvalidInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException("Lot ID cannot be empty");
        }
        if (!id.matches("^LOT\\d{3,}$")) {
            throw new InvalidInputException("Lot ID must start with 'LOT' followed by at least 3 digits (e.g., LOT001)");
        }
    }

    public static void validateSpotNumber(String spotNumber) throws InvalidInputException {
        if (spotNumber == null || spotNumber.trim().isEmpty()) {
            throw new InvalidInputException("Spot number cannot be empty");
        }
        if (!spotNumber.matches("^[A-Z]\\d{3}$")) {
            throw new InvalidInputException("Spot number must be 1 uppercase letter followed by 3 digits (e.g., A001)");
        }
    }

    public static void validateReservationId(String id) throws InvalidInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException("Reservation ID cannot be empty");
        }
        if (!id.matches("^R\\d{3,}$")) {
            throw new InvalidInputException("Reservation ID must start with 'R' followed by at least 3 digits (e.g., R001)");
        }
    }

    public static void validatePlateNumber(String plate) throws InvalidInputException {
        if (plate == null || plate.trim().isEmpty()) {
            throw new InvalidInputException("Vehicle plate number cannot be empty");
        }
        if (!plate.matches("^[A-Z0-9]{3,10}$")) {
            throw new InvalidInputException("Plate number must be 3-10 alphanumeric characters (e.g., ABC123)");
        }
    }

    public static void validateName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Name cannot be empty");
        }
        if (name.length() < 2) {
            throw new InvalidInputException("Name must be at least 2 characters long");
        }
    }

    public static void validateSpotType(String type) throws InvalidInputException {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidInputException("Spot type cannot be empty");
        }
        if (!type.matches("^(REGULAR|VIP|DISABLED)$")) {
            throw new InvalidInputException("Spot type must be REGULAR, VIP, or DISABLED");
        }
    }

    public static void validateCapacity(String capacityStr) throws InvalidInputException {
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity < 1 || capacity > 1000) {
                throw new InvalidInputException("Capacity must be between 1 and 1000");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Capacity must be a valid number");
        }
    }

    public static void validatePrice(String priceStr) throws InvalidInputException {
        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0 || price > 10000) {
                throw new InvalidInputException("Price must be between 0 and 10000");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Price must be a valid number");
        }
    }

    public static void validateDate(String date) throws InvalidInputException {
        if (date == null || date.trim().isEmpty()) {
            throw new InvalidInputException("Date cannot be empty");
        }
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new InvalidInputException("Date must be in format YYYY-MM-DD (e.g., 2025-12-31)");
        }


        try {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (year < 2020 || year > 2050) {
                throw new InvalidInputException("Year must be between 2020 and 2050");
            }
            if (month < 1 || month > 12) {
                throw new InvalidInputException("Month must be between 1 and 12");
            }
            if (day < 1 || day > 31) {
                throw new InvalidInputException("Day must be between 1 and 31");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid date format");
        }
    }
}