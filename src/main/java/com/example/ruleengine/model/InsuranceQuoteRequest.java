package com.example.ruleengine.model;

public class InsuranceQuoteRequest {
    private String driverName;
    private int driverAge;
    private int accidentCount;
    private String vehicleType; // SEDAN, SUV, SPORTS, LUXURY

    public InsuranceQuoteRequest() {
    }

    public InsuranceQuoteRequest(String driverName, int driverAge, int accidentCount, String vehicleType) {
        this.driverName = driverName;
        this.driverAge = driverAge;
        this.accidentCount = accidentCount;
        this.vehicleType = vehicleType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getDriverAge() {
        return driverAge;
    }

    public void setDriverAge(int driverAge) {
        this.driverAge = driverAge;
    }

    public int getAccidentCount() {
        return accidentCount;
    }

    public void setAccidentCount(int accidentCount) {
        this.accidentCount = accidentCount;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}

