package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.model.Rental;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

public class RentalHistoryPlugin implements IPlugin {

    @Override
    public boolean init(ICore core) {

        TableView<Rental> table = new TableView<>();


        TableColumn<Rental, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomer().getName()));

        TableColumn<Rental, String> colVeiculo = new TableColumn<>("Veículo");
        colVeiculo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVehicle().getModel()));

        TableColumn<Rental, String> colPlaca = new TableColumn<>("Placa");
        colPlaca.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVehicle().getId()));

        TableColumn<Rental, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> {
            if (data.getValue().getStartDate() != null) {
                return new SimpleStringProperty(data.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("-");
        });

        TableColumn<Rental, Double> colValor = new TableColumn<>("Valor Total");
        colValor.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        table.getColumns().addAll(colCliente, colVeiculo, colPlaca, colData, colValor);


        Button btnRefresh = new Button("🔄 Atualizar Lista");
        btnRefresh.setOnAction(e -> {
            table.getItems().clear();
            if (core.getDataProvider() != null) {
                table.getItems().addAll(core.getDataProvider().getAllRentals());
            }
        });


        VBox vbox = new VBox(10, btnRefresh, table);
        vbox.setPadding(new Insets(10));


        if (core.getDataProvider() != null) {
            table.getItems().addAll(core.getDataProvider().getAllRentals());
        }


        Tab tab = new Tab("Histórico de Locações (att)", vbox);
        core.getUIController().addTab(tab);

        return true;
    }
}