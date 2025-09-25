package dataAccessLayer.repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import dataAccessLayer.dao.ReservationDAO;
import dataAccessLayer.dao.ReservationItemDAO;
import domainLayerSuppliers.*;
import dto.ReservationDTO;
import dto.ReservationItemDTO;

public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationDAO reservationDAO;
    private final ReservationItemDAO reservationItemDAO;

    public ReservationRepositoryImpl(
        ReservationDAO reservationDAO,
        ReservationItemDAO reservationItemDAO
    ) {
        this.reservationDAO = reservationDAO;
        this.reservationItemDAO = reservationItemDAO;
    }

    @Override
    public void saveReservation(Reservation reservation, String sID) throws SQLException {
        ReservationDTO reservationDTO = new ReservationDTO(
            reservation.getResID(),
            sID,
            reservation.getResAgreement().getAgreementID(),
            reservation.getReservationDate().toString(),
            reservation.getDeliveryDate().toString(),
            reservation.getTotalPrice()
        );

        reservationDAO.insert(reservationDTO);

        for (ProductRes pr : reservation.getResProducts()) {
            ReservationItemDTO itemDTO = new ReservationItemDTO(
                reservation.getResID(),
                pr.getProduct().getProductNum(),
                pr.getQuantity()
            );
            reservationItemDAO.insert(itemDTO);
        }
    }

    @Override
    public Optional<Reservation> getReservation(String resID, Supplier supplier) throws SQLException {
        Optional<ReservationDTO> dtoOpt = reservationDAO.get(resID);
        if (dtoOpt.isEmpty()) return Optional.empty();

        ReservationDTO dto = dtoOpt.get();

        // Match the agreement within the supplier's agreements
        SupplierAgreement agreement = supplier.getAgreements()
            .stream()
            .filter(a -> a.getAgreementID().equals(dto.agreementID()))
            .findFirst()
            .orElse(null);

        if (agreement == null) return Optional.empty();

        List<ReservationItemDTO> itemDTOs = reservationItemDAO.getByReservationId(resID);
        List<ProductRes> products = new ArrayList<>();

        for (ReservationItemDTO itemDTO : itemDTOs) {
            SupplierProduct sp = agreement.searchProduct(itemDTO.productSnum());
            if (sp != null) {
                products.add(new ProductRes(sp, itemDTO.quantity()));
            }
        }

        Reservation reservation = new Reservation(
            dto.resID(),
            products,
            dto.agreementID(),
            LocalDate.parse(dto.reservationDate()),
            supplier,
            LocalDate.parse(dto.deliveryDate()),
            dto.totalPrice()
        );

        return Optional.of(reservation);
    }

    @Override
    public List<Reservation> getAllReservations(Supplier supplier) throws SQLException {
        List<ReservationDTO> dtos = reservationDAO.getAll();
        List<Reservation> reservations = new ArrayList<>();

        for (ReservationDTO dto : dtos) {
            if (!dto.sID().equals(supplier.getSID())) continue;
            Optional<Reservation> reservationOpt = getReservation(dto.resID(), supplier);
            reservationOpt.ifPresent(reservations::add);
        }

        return reservations;
    }

    @Override
    public void deleteReservation(String resID) throws SQLException {
        reservationItemDAO.deleteByReservationId(resID);
        reservationDAO.delete(resID);
    }

    @Override
public void addItemToReservation(String resId, ProductRes productRes) throws SQLException {
    ReservationItemDTO dto = new ReservationItemDTO(
        resId,
        productRes.getProduct().getProductNum(),
        productRes.getQuantity()
    );
    reservationItemDAO.insert(dto);
}

@Override
public void removeItemFromReservation(String resId, String productNum) throws SQLException {
    reservationItemDAO.delete(resId, productNum);
}

@Override
public void updateReservationItem(String resId, String productNum, int quantity) throws SQLException {
    ReservationItemDTO dto = new ReservationItemDTO(resId, productNum, quantity);
    reservationItemDAO.update(dto);
}

}
