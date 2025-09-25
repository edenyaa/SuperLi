package dataAccessLayer.dao;

import dto.ExpiryReportDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExpiryReportDAO {
    void insert(ExpiryReportDTO dto) throws SQLException;
    Optional<ExpiryReportDTO> getById(String id) throws SQLException;
    List<ExpiryReportDTO> getAll() throws SQLException;
    void delete(String id) throws SQLException;
}
