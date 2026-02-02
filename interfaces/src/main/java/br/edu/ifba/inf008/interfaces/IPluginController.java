package br.edu.ifba.inf008.interfaces;

public interface IPluginController {
    boolean init();
    void startPlugins();
    IPricePlugin getPricePlugin();
    IVehiclePlugin getVehiclePlugin(String type);
}