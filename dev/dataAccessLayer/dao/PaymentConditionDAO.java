package dataAccessLayer.dao;

import dto.PaymentConditionDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PaymentConditionDAO {
    void insert(PaymentConditionDTO dto) throws SQLException;
    Optional<PaymentConditionDTO> get(String sID) throws SQLException;
    List<PaymentConditionDTO> getAll() throws SQLException;
    void update(PaymentConditionDTO dto) throws SQLException;
    void delete(String sID) throws SQLException;
}
