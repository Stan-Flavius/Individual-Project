package com.parking;

public interface Billable {
    double calculateTotal();
    double calculateTax();
    double calculateGrandTotal();
}