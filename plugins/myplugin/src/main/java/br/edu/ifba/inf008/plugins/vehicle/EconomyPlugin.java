package br.edu.ifba.inf008.plugins.vehicle;

import br.edu.ifba.inf008.interfaces.IVehiclePlugin;

public class EconomyPlugin implements IVehiclePlugin {

    @Override
    public String getType() {
        return "ECONOMY";
    }

    @Override
    public double calculatePrice(double basePrice, int days) {
        return basePrice * days;
    }
}