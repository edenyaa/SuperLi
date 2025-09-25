package dataAccessLayer.dao;

import dto.ContactPersonDTO;
import java.sql.SQLException;
import java.util.List;

public interface ContactPersonDAO {
    void insert(ContactPersonDTO dto) throws SQLException;
    List<ContactPersonDTO> getBySupplier(String sID) throws SQLException;
    List<ContactPersonDTO> getAll() throws SQLException;
    void delete(String sID, String name) throws SQLException;
    void deleteAllForSupplier(String sID) throws SQLException;
}
