package dataAccessLayer.dao;

import dto.WeeklyTemplateItemDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WeeklyTemplateItemDAO {
    void insert(WeeklyTemplateItemDTO dto) throws SQLException;
    Optional<WeeklyTemplateItemDTO> get(String templateId, String productSnum) throws SQLException;
    List<WeeklyTemplateItemDTO> getAll() throws SQLException;
    void update(WeeklyTemplateItemDTO dto) throws SQLException;
    void delete(String templateId, String productSnum) throws SQLException;
}
