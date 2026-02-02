package br.edu.ifba.inf008.interfaces;


public interface IVehiclePlugin extends IPlugin {
    String getType();
    double calculatePrice(double basePrice, int days);


    @Override
    default boolean init(ICore core) {
        return true;
    }
}