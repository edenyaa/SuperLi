package domainLayerSuppliers;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WeeklyTemplateRepository {
    void saveTemplate(WeeklyTemplate template) throws SQLException;
    Optional<WeeklyTemplate> getTemplate(String templateId, Supplier supplier) throws SQLException;
    List<WeeklyTemplate> getAllTemplates(Supplier supplier) throws SQLException;
    void deleteTemplate(String templateId) throws SQLException;
    void updateTemplate(WeeklyTemplate template) throws SQLException;
}
