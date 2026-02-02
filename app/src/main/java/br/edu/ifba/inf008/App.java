package br.edu.ifba.inf008;

import br.edu.ifba.inf008.interfaces.IPricePlugin;
import br.edu.ifba.inf008.shell.Core;
import br.edu.ifba.inf008.model.Customer;
import br.edu.ifba.inf008.model.Rental;
import br.edu.ifba.inf008.model.Vehicle;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        TabPane rootTabPane = new TabPane();
        VBox mainLayout = new VBox(menuBar, rootTabPane);

        Core core = new Core(rootTabPane, menuBar);

        System.out.println(" >>> CARREGANDO PLUGINS... ");
        core.getPluginController().init();
        core.getPluginController().startPlugins();

        createRentalTab(rootTabPane, core);

        Scene scene = new Scene(mainLayout, 1024, 768);
        primaryStage.setTitle("Locadora de Veículos - INF008");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createRentalTab(TabPane tabPane, Core core) {

        MariaDBDataProvider dataProvider = core.getDataProvider();

        Label lblCliente = new Label("Selecione o Cliente:");
        ComboBox<Customer> cmbCustomers = new ComboBox<>();

        try {
            cmbCustomers.getItems().addAll(dataProvider.getAllCustomers());
        } catch (Exception ex) {
            System.err.println("Erro ao buscar clientes: " + ex.getMessage());
        }

        Label lblTipo = new Label("Tipo de Veículo:");
        ComboBox<Vehicle.VehicleType> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(Vehicle.VehicleType.values());

        Button btnBuscar = new Button("Buscar Veículos Disponíveis");
        ListView<Vehicle> listVehicles = new ListView<>();
        Label lblResultado = new Label();

        btnBuscar.setOnAction(e -> {
            Vehicle.VehicleType tipo = cmbType.getValue();
            if (tipo != null) {
                try {
                    List<Vehicle> veiculos = dataProvider.getVehiclesByType(tipo);
                    listVehicles.getItems().setAll(veiculos);
                    if(veiculos.isEmpty()) lblResultado.setText("Nenhum veículo disponível.");
                    else lblResultado.setText(veiculos.size() + " veículos encontrados.");
                } catch (Exception ex) {
                    lblResultado.setText("Erro no banco.");
                    ex.printStackTrace();
                }
            }
        });

        Button btnAlugar = new Button("ALUGAR VEÍCULO");

        btnAlugar.setOnAction(e -> {
            Customer cliente = cmbCustomers.getValue();
            Vehicle veiculo = listVehicles.getSelectionModel().getSelectedItem();

            if (cliente != null && veiculo != null) {

                Rental aluguel = new Rental();
                aluguel.setCustomer(cliente);
                aluguel.setVehicle(veiculo);
                aluguel.setStartDate(LocalDateTime.now());
                aluguel.setEndDate(LocalDateTime.now().plusDays(3));


                var pluginController = core.getPluginController();

                br.edu.ifba.inf008.interfaces.IVehiclePlugin vehiclePlugin = pluginController.getVehiclePlugin(veiculo.getType().toString());

                BigDecimal valorFinal;

                if (vehiclePlugin != null) {
                    System.out.println("Plugin encontrado para: " + veiculo.getType());

                    double preco = vehiclePlugin.calculatePrice(veiculo.getDailyPrice(), (int) aluguel.getDays());
                    valorFinal = BigDecimal.valueOf(preco);
                } else {
                    System.out.println("Plugin NÃO encontrado para: " + veiculo.getType() + ". Usando preço padrão.");

                    valorFinal = BigDecimal.valueOf(veiculo.getDailyPrice() * aluguel.getDays());
                }


                aluguel.setTotalValue(valorFinal.doubleValue());


                try {
                    dataProvider.saveRental(aluguel);
                    String msg = String.format("Aluguel salvo! Valor Total: R$ %.2f", valorFinal);
                    new Alert(Alert.AlertType.INFORMATION, msg).show();
                    btnBuscar.fire();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Erro ao salvar aluguel.").show();
                    ex.printStackTrace();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Selecione Cliente e Veículo.").show();
            }
        });

        VBox rentalLayout = new VBox(10);
        rentalLayout.setPadding(new Insets(15));
        rentalLayout.getChildren().addAll(
                lblCliente, cmbCustomers,
                lblTipo, cmbType,
                btnBuscar,
                new Label("Veículos Disponíveis:"), listVehicles,
                btnAlugar, lblResultado
        );

        Tab rentalTab = new Tab("Nova Locação");
        rentalTab.setContent(rentalLayout);
        rentalTab.setClosable(false);
        tabPane.getTabs().add(rentalTab);
    }

    public static void main(String[] args) {
        launch(args);
    }
}