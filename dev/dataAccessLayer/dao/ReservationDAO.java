package dataAccessLayer.dao;

import dto.ReservationDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
    void insert(ReservationDTO dto) throws SQLException;
    Optional<ReservationDTO> get(String resID) throws SQLException;
    List<ReservationDTO> getAll() throws SQLException;
    void update(ReservationDTO dto) throws SQLException;
    void delete(String resID) throws SQLException;
}
