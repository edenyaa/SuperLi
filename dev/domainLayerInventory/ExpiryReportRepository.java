package domainLayerInventory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExpiryReportRepository {
    void saveExpiryReport(ExpiryReport report) throws SQLException;
    Optional<ExpiryReport> getExpiryReportById(String id) throws SQLException;
    List<ExpiryReport> getAllExpiryReports() throws SQLException;
    void deleteExpiryReport(String id) throws SQLException;
}
