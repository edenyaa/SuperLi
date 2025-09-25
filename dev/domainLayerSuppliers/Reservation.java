package domainLayerSuppliers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private String resID;
    private List<ProductRes> resProducts;
    private SupplierAgreement resAgreement;
    private LocalDate reservationDate;
    private double totalPrice;
    private LocalDate deliveryDate;

    public Reservation(String id, List<ProductRes> resProduct, String agreementID, LocalDate reservationDate, Supplier supplier,DayOfWeek supplyDay) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Reservation ID cannot be null or empty");
        if (resProduct == null || resProduct.isEmpty()) throw new IllegalArgumentException("Reservation products cannot be null or empty");
        if (reservationDate == null) throw new IllegalArgumentException("Reservation date cannot be null");
        if (supplier == null) throw new IllegalArgumentException("Supplier cannot be null");

        // Check duplicate reservation ID in supplier
        for (Reservation res : supplier.getReservations()) {
            if (res.getResID().equals(id)) {
                throw new IllegalArgumentException("Reservation ID already exists for this supplier");
            }
        }
        this.resID = id;
        this.resProducts = new ArrayList<>(resProduct);
        this.reservationDate = reservationDate;

        SupplierAgreement sa = supplier.searchAgreement(agreementID);
        if (sa == null) {
            this.resAgreement = createAgreementOnTheFly(supplier, agreementID);
        } else {
            // Update agreement products with any new reservation products not yet included
            for (ProductRes resp : resProduct) {
                if (!sa.getProducts().contains(resp.getProduct())) {
                    sa.addProduct(resp.getProduct());
                }
            }
            this.resAgreement = sa;
        }

        if(supplyDay == null) assignDeliveryDate(supplier);
        else assignDeliveryDateOnDemand(supplier,supplyDay);
        calculatePrice();
        supplier.addReservation(this);
    }

    public Reservation(String id, List<ProductRes> resProduct, String agreementID, LocalDate reservationDate, Supplier supplier,LocalDate deliveryDate, double totalPrice) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("Reservation ID cannot be null or empty");
        if (resProduct == null || resProduct.isEmpty()) throw new IllegalArgumentException("Reservation products cannot be null or empty");
        if (reservationDate == null) throw new IllegalArgumentException("Reservation date cannot be null");
        if (supplier == null) throw new IllegalArgumentException("Supplier cannot be null");

        // Check duplicate reservation ID in supplier
        for (Reservation res : supplier.getReservations()) {
            if (res.getResID().equals(id)) {
                throw new IllegalArgumentException("Reservation ID already exists for this supplier");
            }
        }

        this.resID = id;
        this.resProducts = new ArrayList<>(resProduct);
        this.reservationDate = reservationDate;
        this.totalPrice = totalPrice;
        this.deliveryDate = deliveryDate;
    }

    private SupplierAgreement createAgreementOnTheFly(Supplier supplier, String agreementID) {
        SupplierAgreement agreement = new SupplierAgreement(
            agreementID,
            supplier.getContactPersons().get(0),
            new ArrayList<>(),
            new ArrayList<>(),
            !supplier.getSupplyDays().isEmpty()
        );
        for (ProductRes pr : resProducts) {
            agreement.addProduct(pr.getProduct());
        }
        supplier.addAgreement(agreement);
        return agreement;
    }

    private void assignDeliveryDate(Supplier supplier) {
        deliveryDate = null;
        for (int i = 0; i < 7; i++) {
            LocalDate dateToCheck = reservationDate.plusDays(i);
            if (supplier.getSupplyDays().contains(dateToCheck.getDayOfWeek())) {
                deliveryDate = dateToCheck;
                break;
            }
        }
    }

    private void assignDeliveryDateOnDemand(Supplier supplier, DayOfWeek supplyDay){
        deliveryDate = null;
        for (int i = 0; i < 7; i++) {
            LocalDate dateToCheck = reservationDate.plusDays(i);
            if (dateToCheck.getDayOfWeek() == supplyDay) {
                deliveryDate = dateToCheck;
                break;
            }
        }
    }

    public String getResID() { return resID; }
    public List<ProductRes> getResProducts() { return new ArrayList<>(resProducts); }
    public SupplierAgreement getResAgreement() { return resAgreement; }
    public LocalDate getReservationDate() { return reservationDate; }
    public LocalDate getDeliveryDate() { return deliveryDate; }

    public double getTotalPrice() {
        calculatePrice();
        return totalPrice;
    }

    public void addProductToReservation(ProductRes pr) {
        if (pr == null) throw new IllegalArgumentException("ProductRes cannot be null");

        for (int i = 0; i < resProducts.size(); i++) {
            if (resProducts.get(i).getProduct().equals(pr.getProduct())) {
                resProducts.set(i, pr);
                return;
            }
        }
        resProducts.add(pr);
        calculatePrice();
    }

    public void removeProductFromReservation(ProductRes pr) {
        if (pr == null) return;
        resProducts.removeIf(p -> p.getProduct().equals(pr.getProduct()));
        calculatePrice();
    }

    private void calculatePrice() {
        totalPrice = 0;
        for (ProductRes pr : resProducts) {
            double pricePerUnit = pr.getProduct().getPrice();
            int qty = pr.getQuantity();
            double discount = resAgreement.getDiscountFromProducts(pr.getProduct());
            int minQty = resAgreement.getMinAmountOfProduct(pr.getProduct());

            if (qty >= minQty) {
                totalPrice += qty * pricePerUnit * (1 - discount);
            } else {
                totalPrice += qty * pricePerUnit;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reservation ID: ").append(resID).append("\n");
        sb.append("Reservation Date: ").append(reservationDate).append("\n");
        sb.append("Delivery Date: ").append(deliveryDate != null ? deliveryDate : "No delivery date available").append("\n");
        sb.append("Contact Phone: ").append(resAgreement.getContactPerson().getPhone()).append("\n");
        sb.append("Products:\n");
        for (ProductRes p : resProducts) {
            sb.append("  - ").append(p.getProduct().getProductName())
              .append(", Quantity: ").append(p.getQuantity())
              .append(", Unit Price: ").append(String.format("%.2f", p.getProduct().getPrice()));

            int minQty = resAgreement.getMinAmountOfProduct(p.getProduct());
            double discount = resAgreement.getDiscountFromProducts(p.getProduct());
            if (p.getQuantity() >= minQty) {
                sb.append(", Discount: ").append(String.format("%.2f%%", discount * 100));
                sb.append(", Discounted Unit Price: ").append(String.format("%.2f", p.getProduct().getPrice() * (1 - discount)));
            }
            sb.append("\n");
        }
        sb.append("Total Price: ").append(String.format("%.2f", totalPrice)).append("\n");

        return sb.toString();
    }
}
