package br.edu.ifba.inf008.plugins.vehicle;
import br.edu.ifba.inf008.interfaces.IVehiclePlugin;

public class CompactPlugin implements IVehiclePlugin {
    @Override
    public String getType() { return "COMPACT"; }
    @Override
    public double calculatePrice(double basePrice, int days) {
        return (basePrice * 1.1) * days; // 10% a mais
    }
}