package dataAccessLayer.repository;

import domainLayerInventory.ExpiryReportRepository;
import dto.ExpiryReportDTO;
import dataAccessLayer.dao.ExpiryReportDAO;
import domainLayerInventory.ExpiryReport;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ExpiryReportRepositoryImpl implements ExpiryReportRepository {

    private final ExpiryReportDAO expiryReportDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final List<ExpiryReport> cachedReports = new ArrayList<>();

    public ExpiryReportRepositoryImpl(ExpiryReportDAO expiryReportDAO) throws SQLException {
        this.expiryReportDAO = expiryReportDAO;
        reloadCache();
    }

    private void reloadCache() throws SQLException {
        cachedReports.clear();
        List<ExpiryReportDTO> dtos = expiryReportDAO.getAll();
        for (ExpiryReportDTO dto : dtos) {
            cachedReports.add(toDomain(dto));
        }
    }

    @Override
    public void saveExpiryReport(ExpiryReport report) throws SQLException {
        ExpiryReportDTO dto = toDTO(report);
        expiryReportDAO.insert(dto);
        cachedReports.add(report);  // update cache
    }

    @Override
    public Optional<ExpiryReport> getExpiryReportById(String id) throws SQLException {
        return cachedReports.stream()
                .filter(report -> report.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ExpiryReport> getAllExpiryReports() {
        return new ArrayList<>(cachedReports); // return copy of cached list
    }

    @Override
    public void deleteExpiryReport(String id) throws SQLException {
        expiryReportDAO.delete(id);
        cachedReports.removeIf(report -> report.getId().equals(id));
    }

    // Mapper methods
    private ExpiryReportDTO toDTO(ExpiryReport report) {
    String formattedDate = report.getReportedAt().format(dateFormatter);
    return new ExpiryReportDTO(
            report.getId(),
            report.getBarcode(),
            report.getQuantityExpired(),
            report.getLocation(),
            formattedDate,
            report.getReportedBy()
    );
}

    private ExpiryReport toDomain(ExpiryReportDTO dto) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(dto.reportedAt(), dateFormatter);
        } catch (DateTimeParseException e) {
            parsedDate = LocalDate.now(); // fallback
        }
        return new ExpiryReport(dto.id(), dto.barcode(), dto.quantityExpired(), dto.location(), parsedDate, dto.reportedBy());
    }
}
