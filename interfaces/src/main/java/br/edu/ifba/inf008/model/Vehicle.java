package br.edu.ifba.inf008.model;

public class Vehicle {

    public enum VehicleType { ECONOMY, COMPACT, SUV, LUXURY, VAN, ELECTRIC }


    private String id; // Placa
    private String make;
    private String model;
    private double dailyPrice;
    private VehicleType type;

    public Vehicle() {}

    public Vehicle(String plate, String model, double dailyPrice, String typeStr) {
        this.id = plate;
        this.model = model;
        this.dailyPrice = dailyPrice;
        try {
            this.type = VehicleType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            this.type = VehicleType.ECONOMY; // Fallback
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlate() { return id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(double dailyPrice) { this.dailyPrice = dailyPrice; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    @Override
    public String toString() { return model + " (" + id + ")"; }
}