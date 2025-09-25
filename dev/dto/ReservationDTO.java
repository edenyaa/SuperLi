package dto;

public record ReservationDTO(String resID, String sID, String agreementID, String reservationDate,
                             String deliveryDate, double totalPrice) {}
