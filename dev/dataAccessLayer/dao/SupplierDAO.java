package dataAccessLayer.dao;

import dto.SupplierDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SupplierDAO {
    void insert(SupplierDTO dto) throws SQLException;
    Optional<SupplierDTO> get(String sID) throws SQLException;
    List<SupplierDTO> getAll() throws SQLException;
    void update(SupplierDTO dto) throws SQLException;
    void delete(String sID) throws SQLException;
}
