package dataAccessLayer.dao;

import dto.PaymentConditionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPaymentConditionDAO extends BaseJdbcDAO implements PaymentConditionDAO {

    @Override
    public void insert(PaymentConditionDTO dto) throws SQLException {
        String sql = "INSERT INTO payment_conditions (sID, payment_type, bank_account) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.sID());
            ps.setString(2, dto.paymentType());
            ps.setString(3, dto.bankAccount());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<PaymentConditionDTO> get(String sID) throws SQLException {
        String sql = "SELECT * FROM payment_conditions WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new PaymentConditionDTO(
                            rs.getString("sID"),
                            rs.getString("payment_type"),
                            rs.getString("bank_account")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PaymentConditionDTO> getAll() throws SQLException {
        List<PaymentConditionDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_conditions";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PaymentConditionDTO(
                        rs.getString("sID"),
                        rs.getString("payment_type"),
                        rs.getString("bank_account")
                ));
            }
        }
        return list;
    }

    @Override
    public void update(PaymentConditionDTO dto) throws SQLException {
        String sql = "UPDATE payment_conditions SET payment_type = ?, bank_account = ? WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dto.paymentType());
            ps.setString(2, dto.bankAccount());
            ps.setString(3, dto.sID());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String sID) throws SQLException {
        String sql = "DELETE FROM payment_conditions WHERE sID = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, sID);
            ps.executeUpdate();
        }
    }
}
