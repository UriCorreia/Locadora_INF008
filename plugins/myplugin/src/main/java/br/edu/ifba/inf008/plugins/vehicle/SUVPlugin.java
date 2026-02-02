package br.edu.ifba.inf008.plugins.vehicle;

import br.edu.ifba.inf008.interfaces.IVehiclePlugin;

public class SUVPlugin implements IVehiclePlugin {

    @Override
    public String getType() {
        return "SUV";
    }

    @Override
    public double calculatePrice(double basePrice, int days) {
        return (basePrice * 1.3) * days;
    }
}