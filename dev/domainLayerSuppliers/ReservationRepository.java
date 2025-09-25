package domainLayerSuppliers;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    void saveReservation(Reservation reservation, String sID) throws SQLException;
    Optional<Reservation> getReservation(String resID, Supplier supplier) throws SQLException;
    List<Reservation> getAllReservations(Supplier supplier) throws SQLException;
    void deleteReservation(String resID) throws SQLException;
    void addItemToReservation(String resId, ProductRes productRes) throws SQLException;
    void removeItemFromReservation(String resId, String productNum) throws SQLException;
    void updateReservationItem(String resId, String productNum,int quantity) throws SQLException;
}
