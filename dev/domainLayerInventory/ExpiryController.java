package domainLayerInventory;

import util.AppConfig;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpiryController {
    private final ExpiryReportRepository repository;

    public ExpiryController() {
        this.repository = AppConfig.expiryReportRepository;
    }

    public boolean addExpiryReport(ExpiryReport report) {
        try {
            repository.saveExpiryReport(report);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ExpiryReport> getAllReports() {
        try {
            return repository.getAllExpiryReports();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void resetAllReports() {
        try {
            List<ExpiryReport> allReports = repository.getAllExpiryReports();
            for (ExpiryReport report : allReports) {
                repository.deleteExpiryReport(report.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ExpiryReport> getReportsByProduct(String barcode) {
        try {
            return repository.getAllExpiryReports().stream()
                    .filter(report -> report.getBarcode().equals(barcode))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ExpiryReport> generateReport(int daysAhead, LocalDate currDate) {
    try {
        LocalDate targetDate = currDate.plusDays(daysAhead);
        return repository.getAllExpiryReports().stream()
                .filter(report -> report.getReportedAt().isBefore(targetDate))
                .collect(Collectors.toList());
    } catch (SQLException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}
}
