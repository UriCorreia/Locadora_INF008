package br.edu.ifba.inf008.plugins.report;

import br.edu.ifba.inf008.interfaces.IReportPlugin;
import br.edu.ifba.inf008.model.Rental;
import java.util.List;

public class TotalRevenueReport implements IReportPlugin {

    @Override
    public String getName() {
        return "Faturamento Total";
    }

    @Override
    public String generate(List<Rental> rentals) {
        double total = 0;
        for (Rental r : rentals) {
            total += r.getTotalPrice();
        }
        return "Faturamento total: R$ " + total;
    }
}