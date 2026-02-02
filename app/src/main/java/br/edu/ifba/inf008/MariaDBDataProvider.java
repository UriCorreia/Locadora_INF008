package br.edu.ifba.inf008;

import br.edu.ifba.inf008.interfaces.IDataProvider;
import br.edu.ifba.inf008.model.Customer;
import br.edu.ifba.inf008.model.Rental;
import br.edu.ifba.inf008.model.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MariaDBDataProvider implements IDataProvider {

    @Override
    public List<Customer> getAllClients() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT first_name, last_name, tax_id FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                Customer c = new Customer(fullName, rs.getString("tax_id"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType type) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.license_plate, v.model, vt.daily_rate, vt.type_name " +
                "FROM vehicles v " +
                "JOIN vehicle_types vt ON v.type_id = vt.type_id " +
                "WHERE vt.type_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getString("license_plate"),
                        rs.getString("model"),
                        rs.getDouble("daily_rate"),
                        rs.getString("type_name")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void saveRental(Rental rental) {
        String sql = "INSERT INTO rentals (" +
                "   customer_id, vehicle_id, rental_type, start_date, scheduled_end_date, " +
                "   pickup_location, base_rate, insurance_fee, total_amount, rental_status" +
                ") " +
                "SELECT " +
                "   (SELECT customer_id FROM customers WHERE tax_id = ?), " +
                "   (SELECT vehicle_id FROM vehicles WHERE license_plate = ?), " +
                "   'DAILY', " +
                "   ?, " +
                "   ?, " +
                "   'Main Office', " +
                "   (SELECT daily_rate FROM vehicle_types vt JOIN vehicles v ON v.type_id = vt.type_id WHERE v.license_plate = ?), " + // 5. Busca taxa base pela placa
                "   0, " +
                "   ?, " +
                "   'ACTIVE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getCustomer().getCpf());
            stmt.setString(2, rental.getVehicle().getId());

            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.plusDays(rental.getDays());

            stmt.setTimestamp(3, Timestamp.valueOf(start));
            stmt.setTimestamp(4, Timestamp.valueOf(end));

            stmt.setString(5, rental.getVehicle().getId());
            stmt.setDouble(6, rental.getTotalPrice());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Rental> getAllRentals() {
        List<Rental> list = new ArrayList<>();

        String sql = "SELECT r.start_date, r.scheduled_end_date, r.total_amount, " +
                "       c.first_name, c.last_name, c.tax_id, " +
                "       v.model, v.license_plate, vt.daily_rate, vt.type_name " +
                "FROM rentals r " +
                "JOIN customers c ON r.customer_id = c.customer_id " +
                "JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                "JOIN vehicle_types vt ON v.type_id = vt.type_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Rental r = new Rental();

                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                Customer c = new Customer(fullName, rs.getString("tax_id"));
                r.setCustomer(c);

                Vehicle v = new Vehicle(
                        rs.getString("license_plate"),
                        rs.getString("model"),
                        rs.getDouble("daily_rate"),
                        rs.getString("type_name")
                );
                r.setVehicle(v);

                // Dados do aluguel
                r.setTotalValue(rs.getDouble("total_amount"));

                Timestamp startTs = rs.getTimestamp("start_date");
                Timestamp endTs = rs.getTimestamp("scheduled_end_date");

                if (startTs != null) {
                    r.setStartDate(startTs.toLocalDateTime());
                }
                if (endTs != null) {
                    r.setEndDate(endTs.toLocalDateTime());
                }

                list.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void saveClient(Customer client) {
        String sql = "INSERT INTO customers (first_name, last_name, tax_id, email, customer_type) VALUES (?, ?, ?, ?, 'INDIVIDUAL')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String[] names = client.getName().split(" ", 2);
            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "";

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, client.getCpf());
            stmt.setString(4, "email_placeholder_" + client.getCpf() + "@example.com");
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void saveVehicle(Vehicle vehicle) {}

    @Override
    public List<Vehicle> getAllVehicles() { return new ArrayList<>(); }

    @Override
    public Vehicle findVehicleByPlate(String plate) { return null; }
}