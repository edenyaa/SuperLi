package dataAccessLayer.dao;

import dto.WeeklyTemplateDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WeeklyTemplateDAO {
    void insert(WeeklyTemplateDTO dto) throws SQLException;
    Optional<WeeklyTemplateDTO> get(String templateId) throws SQLException;
    List<WeeklyTemplateDTO> getAll() throws SQLException;
    void update(WeeklyTemplateDTO dto) throws SQLException;
    void delete(String templateId) throws SQLException;
}
