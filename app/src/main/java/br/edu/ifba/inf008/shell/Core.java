package br.edu.ifba.inf008.shell;

import br.edu.ifba.inf008.interfaces.*;
import br.edu.ifba.inf008.MariaDBDataProvider;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;

public class Core extends ICore {

    private MariaDBDataProvider dataProvider; // Tipo específico para ter acesso aos métodos extras
    private IUIController uiController;
    private IPluginController pluginController;

    public Core(TabPane tabPane, MenuBar menuBar) {
        instance = this; // Define a instância estática do pai

        this.dataProvider = new MariaDBDataProvider();
        this.uiController = new UIController(tabPane, menuBar);
        this.pluginController = PluginController.getInstance();
    }

    @Override
    public MariaDBDataProvider getDataProvider() { return dataProvider; }

    @Override
    public IUIController getUIController() { return uiController; }

    @Override
    public IAuthenticationController getAuthenticationController() { return new AuthenticationController(); }

    @Override
    public IIOController getIOController() { return new IOController(); }

    @Override
    public IPluginController getPluginController() { return pluginController; }
}