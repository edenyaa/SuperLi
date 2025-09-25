package dataAccessLayer.dao;

import dto.SupplyDayDTO;
import java.sql.SQLException;
import java.util.List;

public interface SupplyDayDAO {
    void insert(SupplyDayDTO dto) throws SQLException;
    List<SupplyDayDTO> getBySupplier(String sID) throws SQLException;
    List<SupplyDayDTO> getAll() throws SQLException;
    void delete(String sID, String day) throws SQLException;
    void deleteAllForSupplier(String sID) throws SQLException;
}
