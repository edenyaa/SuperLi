package domainLayerSuppliers;

import java.time.DayOfWeek;
import java.util.*;

public class Supplier {
    private String name;
    private String sID;
    private String address;
    private List<DayOfWeek> supplyDays;
    private PaymentCondition paymentCondition;
    private List<ContactPerson> contactPersons;
    private List<SupplierAgreement> agreements;
    private List<Reservation> reservations;

    public Supplier(String name, String sID, String address, PaymentCondition paymentCondition, List<ContactPerson> contacts, List<SupplierAgreement> agreement, List<DayOfWeek> supplyDays) {
        this.name = name;
        this.sID = sID;
        this.address = address;
        this.paymentCondition = paymentCondition;
        this.contactPersons = (contacts != null) ? new ArrayList<>(contacts) : new ArrayList<>();
        this.agreements = (agreement != null) ? new ArrayList<>(agreement) : new ArrayList<>();
        this.supplyDays = (supplyDays != null) ? new ArrayList<>(supplyDays) : new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    // Getters:
    public String getName() { return name; }
    public String getSID() { return sID; }
    public String getAddress() { return address; }
    public PaymentCondition getPaymentCondition() { return paymentCondition; }
    public List<DayOfWeek> getSupplyDays() { return supplyDays; }
    public List<ContactPerson> getContactPersons() { return contactPersons; }
    public List<SupplierAgreement> getAgreements() { return agreements; }
    public List<Reservation> getReservations() { return reservations; }

    // Setters:
    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }
    public void setAgreements(List<SupplierAgreement> agreements) {
        this.agreements = agreements;
    }
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Update an existing contact person by name
    public void setContactPersonByName(ContactPerson contactPerson) {
        for (int i = 0; i < contactPersons.size(); i++) {
            if (contactPersons.get(i).getName().equals(contactPerson.getName())) {
                contactPersons.set(i, contactPerson);
                break;
            }
        }
    }

    // Returns main supplier details as a string
    public String supplierToString() {
        return "Supplier name: " + name + ", ID: " + sID + ", Address: " + address;
    }

    // Returns reservation details string for given reservationID or an informative message
    public String getReservationDetails(String reservationID) {
        for (Reservation r : reservations) {
            if (r.getResID().equals(reservationID)) {
                return r.toString(); // Assuming Reservation overrides toString with details
            }
        }
        return "Reservation with ID '" + reservationID + "' not found.";
    }

    // Finding all product agreements with the same SupplierProduct
    public List<ProductAgreement> getProductAgreementsToProduct(SupplierProduct product) {
        List<ProductAgreement> productAgreements = new ArrayList<>();
        for (SupplierAgreement agreement : agreements) {
            for (ProductAgreement p : agreement.getSupplierProductsDiscount()) {
                if (p.getProduct().equals(product)) {
                    productAgreements.add(p);
                }
            }
        }
        return productAgreements;
    }

    // Find agreement by agreementID
    public SupplierAgreement searchAgreement(String agreementID) {
        for (SupplierAgreement agreement : agreements) {
            if (agreement.getAgreementID().equals(agreementID)) {
                return agreement;
            }
        }
        return null;
    }

    // Add new reservation if it does not already exist
    public void addReservation(Reservation reservation) {
        boolean exists = reservations.stream().anyMatch(r -> r.getResID().equals(reservation.getResID()));
        if (!exists) {
            reservations.add(reservation);
        }
    }

    // Remove an existing reservation
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    // Add a new contact person if not already in the list
    public void addContactPerson(ContactPerson cp) {
        if (!contactPersons.contains(cp)) contactPersons.add(cp);
    }

    // Remove a contact person if it exists
    public void removeContactPerson(ContactPerson cp) {
        contactPersons.remove(cp);
    }

    // Add and remove agreements
    public boolean addAgreement(SupplierAgreement agreement) {
        if (!agreements.contains(agreement)) {
            agreements.add(agreement);
            return true;
        }
        return false;
    }
    public boolean removeAgreement(SupplierAgreement agreement) {
        if (agreements.contains(agreement)) {
            agreements.remove(agreement);
            return true;
        }
        return false;
    }

    // Add product to reservation by resID
    public void addProductToRes(SupplierProduct product, int amount, String resID) {
        for (Reservation r : reservations) {
            if (r.getResID().equals(resID)) {
                ProductRes pr = new ProductRes(product, amount);
                r.addProductToReservation(pr);
                break;
            }
        }
    }

    // Remove product from reservation by resID
    public void removeProductFromRes(SupplierProduct product, int amount, String resID) {
        for (Reservation r : reservations) {
            if (r.getResID().equals(resID)) {
                ProductRes pr = new ProductRes(product, amount);
                r.removeProductFromReservation(pr);
                break;
            }
        }
    }

    // Add product to agreement by agreementID
    public void addProductToAgreements(String agreementID, SupplierProduct product) {
        SupplierAgreement curr = searchAgreement(agreementID);
        if (curr != null) {
            curr.addProduct(product);
        }
    }

    // Remove product from agreement by agreementID and product barcode
    public void deleteProductFromAgreements(String agreementID, String productBarcode) {
        SupplierAgreement curr = searchAgreement(agreementID);
        if (curr != null) {
            curr.removeProduct(productBarcode);
        }
    }

    // Add discount to agreement
    public void addDiscountToAgreements(String agreementID, ProductAgreement product) {
        SupplierAgreement agreement = searchAgreement(agreementID);
        if (agreement != null) {
            agreement.addDiscount(product);
        }
    }

    // Remove discount from agreement
    public void removeProductDiscountFromAgreements(String agreementID, ProductAgreement product) {
        SupplierAgreement curr = searchAgreement(agreementID);
        if (curr != null) {
            curr.removeDiscount(product);
        }
    }

    // Return string listing all agreement IDs
    public String getAgreementsSummary() {
        StringBuilder sb = new StringBuilder();
        for (SupplierAgreement agreement : agreements) {
            sb.append(agreement.getAgreementID()).append("\n");
        }
        return sb.toString().trim();
    }

    // Return string listing all contact persons' details
    public String getContactsSummary() {
        StringBuilder sb = new StringBuilder();
        for (ContactPerson c : contactPersons) {
            sb.append(c.toString()).append("\n");  // Assuming ContactPerson overrides toString
        }
        return sb.toString().trim();
    }

    // Search reservation by ID and return it (or null if not found)
    public Reservation searchRes(String reservationID) {
        for (Reservation r : reservations) {
            if (r.getResID().equals(reservationID)) {
                return r;
            }
        }
        return null;
    }

    public SupplierAgreement searchAgreementByProduct(String productNumber) {
    for (SupplierAgreement agreement : agreements) {
        for (SupplierProduct sp : agreement.getProducts()) {
            if (sp.getProductNum().equals(productNumber)) {
                return agreement;
            }
        }
    }
    return null; // No matching agreement found
}

}
