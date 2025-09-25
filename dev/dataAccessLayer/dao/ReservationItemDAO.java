package dataAccessLayer.dao;

import dto.ReservationItemDTO;
import java.sql.SQLException;
import java.util.List;

public interface ReservationItemDAO {
    void insert(ReservationItemDTO dto) throws SQLException;
    List<ReservationItemDTO> getByReservationId(String resID) throws SQLException;
    void update(ReservationItemDTO dto) throws SQLException;
    void delete(String resID, String productSnum) throws SQLException;
    void deleteByReservationId(String resID) throws SQLException;
}
