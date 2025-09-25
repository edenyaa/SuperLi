package serviceLayer;

import domainLayerSuppliers.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;

public class SupplyService {
    
    private SupplyController supplyController;

    public SupplyService() {
        this.supplyController = new SupplyController();
    }

    // 1. Show all suppliers
    public String showAllSuppliers() {
    List<Supplier> suppliers = this.supplyController.getAllSuppliers();
    if (suppliers.isEmpty()) return "No suppliers found.";

    StringBuilder sb = new StringBuilder();
    for (Supplier supplier : suppliers) {
        sb.append("Supplier Name: ").append(supplier.getName()).append("\n");
        sb.append("ID: ").append(supplier.getSID()).append("\n");
        sb.append("Address: ").append(supplier.getAddress()).append("\n");
        // Supply days
        List<DayOfWeek> supplyDays = supplier.getSupplyDays();
        sb.append("Supply Days: ");
        if (supplyDays.isEmpty()) {
            sb.append("None");
        } else {
            sb.append(supplyDays.stream()
                    .map(DayOfWeek::toString)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(""));
        }
        sb.append("\n");
        // Contact persons
        List<ContactPerson> contacts = supplier.getContactPersons();
        sb.append("Contact Persons: ");
        if (contacts.isEmpty()) {
            sb.append("None");
        } else {
            sb.append("\n");
            for (ContactPerson cp : contacts) {
                sb.append("  - ").append(cp.getName())
                  .append(" (").append(cp.getPhone()).append(")\n");
            }
        }
        sb.append("--------------------------------------------------\n");
    }
    return sb.toString();
}

    // 2. Show all reservations from a supplier
    public String getAllReservations(String supplierId) {
        List<Reservation> supplierReservations = this.supplyController.getAllReservations(supplierId);
        if(supplierReservations == null) return "Supplier not found: " + supplierId;
        if (supplierReservations.isEmpty()) return "No reservations found for supplier: " + supplierId;
        return formatReservations(supplierReservations);
    }

    private String formatReservations(List<Reservation> reservations) {
        StringBuilder sb = new StringBuilder();
        for (Reservation r : reservations) {
            sb.append("Reservation ID: ").append(r.getResID())
              .append(", Date: ").append(r.getReservationDate()).append("\n");
        }
        return sb.toString();
    }

    // 3. Show supplier's supply days
    public String getSupplierSupplyDays(String supplierId) {
        List<DayOfWeek> supplyDays = this.supplyController.getSupplierSupplyDays(supplierId);
        if(supplyDays == null) return "Supplier not found: " + supplierId;
        if(supplyDays.isEmpty()) return "No supply days set for supplier: " + supplierId;
        return "Supply days for supplier " + supplierId + ": " + formatSupplyDays(supplyDays);
    }

    private String formatSupplyDays(List<DayOfWeek> days) {
        StringBuilder sb = new StringBuilder();
        for (DayOfWeek day : days) {
            sb.append(day.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    // 4. Print supplier's contact persons
    public String getSupplierContacts(String supplierId) {
        return this.supplyController.getSupplierContacts(supplierId);
    }

    // 5. Add new supplier
   public String addSupplier(String name, String address, String paymentTypeInput, String bankAccount,
                          String contactName, String contactPhone, String contactEmail, List<String> supplyDayStrings) {
        String supplierId = IdGenerator.generateSupplierID();
        boolean added = this.supplyController.addNewSupplier(supplierId,name, address,paymentTypeInput,bankAccount,contactName,contactPhone,contactEmail,supplyDayStrings);
        if(added) return "Supplier with id: " + supplierId + " was created successfuly.";
        return "Failed to add supplier.";
}

    // 6. Remove a supplier
    public String removeSupplier(String supplierId) {
        boolean removed = this.supplyController.removeSupplier(supplierId);
        if(removed) return "Supplier with id: " + supplierId + " was removed.";
        return "Supplier with id: " + supplierId + " doesn't exist.";
    }

    // 7. Add immediate reservation to supplier
    public String addReservation(String supplierId, List<String> productNums, List<Integer> quantities, String agreementId, LocalDate reservationDate) {
        String resId = IdGenerator.generateResID();
        boolean added = this.supplyController.addReservation(resId, supplierId,productNums,quantities,agreementId,reservationDate);
        if(added) return "Reservation with id: " + resId + " added successfuly.";
        return "Failed to add reservation.";
}

    // 8. Add agreement to supplier
    public String addAgreementToSupplier(String supplierId, String contactName, String contactPhone, String contactEmail,
                                     List<String[]> productDetails, boolean hasRegularDays) {
    String agreementId = IdGenerator.generateAgreementID();
    boolean added = this.supplyController.addAgreementToSupplier(agreementId, supplierId, contactName, contactPhone, contactEmail, productDetails, hasRegularDays);
    if(!added) return "Failed to add agreement.";
    return "Agreement added to supplier " + supplierId;
}

    // 9. Remove agreement from supplier
    public String removeAgreementFromSupplier(String supplierId, String agreementId) {
        boolean removed = this.supplyController.removeAgreementFromSupplier(supplierId, agreementId);
        if(removed) return "Agreement removed successfuly.";
        return "Failed to remove agreement.";
}

    // 10. Add contact person to supplier
    public String addContactPersonToSupplier(String supplierId, String name, String phone, String email) {
        boolean added = this.supplyController.addContactPersonToSupplier(supplierId, name, phone, email);
        if(added) return "New contact added successfuly.";
        return "Failed to add new contact.";
}

    // 11. Add a product to an agreement of a supplier
    public String addProductToAgreement(String supplierId, String agreementId, String productName, double productPrice, String productNum) {
        boolean added = this.supplyController.addProductToAgreement(supplierId, agreementId, productName, productPrice, productNum);
        if (added) return "Product added to agreement successfuly.";
        return "Failed to add product to agreement.";
    }

    // 12. Remove a product from an agreement of a supplier
    public String removeProductFromAgreement(String supplierId, String agreementId, String productCode) {
        boolean removed = this.supplyController.removeProductFromAgreement(supplierId, agreementId, productCode);
        if(removed) return "Product removed successfuly.";
        return "Failed to remove product from agreement.";
    }

    // 13. Show all supplier agreements by supplier ID 
    public String showAllSupplierAgreements(String supplierId) {
    List<SupplierAgreement> agreements = this.supplyController.showAllSupplierAgreements(supplierId);
    if (agreements == null) return "Supplier with id: " + supplierId + " not found.";
    if (agreements.isEmpty()) return "No agreements found for supplier with id: " + supplierId;
    StringBuilder sb = new StringBuilder();
    sb.append("Agreements for Supplier ID: ").append(supplierId).append("\n");
    for (SupplierAgreement agreement : agreements) {
        sb.append("Agreement ID: ").append(agreement.getAgreementID()).append("\n");

        ContactPerson cp = agreement.getContactPerson();
        sb.append("Contact Person: ").append(cp.getName())
          .append(" (").append(cp.getPhone()).append(")\n");

        List<SupplierProduct> products = agreement.getProducts();
        sb.append("Products:\n");
        for (SupplierProduct product : products) {
            sb.append("  - ").append(product.getProductName())
              .append(" (Barcode: ").append(product.getProductNum()).append(")")
              .append(" | Price: ").append(product.getPrice()).append("\n");
        }
        sb.append("--------------------------------------------------\n");
    }
    return sb.toString();
}


   // 14. Show supplier discount for a product by supplier ID and product Number
    public String showSupplierDiscountForProduct(String supplierId, String productNum) {
        return this.supplyController.showSupplierDiscountForProduct(supplierId, productNum);
    }

    //15. Add periodic reservation
    public String addResTemplate(String supplierId, List<String> productNums, List<Integer> quantities, String agreementId,DayOfWeek supplyDay){
        String templateId = IdGenerator.generateTemplateID();
        LocalDate currDate = SimulatedClock.getInstance().getCurrentDate();
        boolean added = this.supplyController.addResTemplate(templateId, currDate, supplierId, productNums, quantities, agreementId, supplyDay);
        if(added) return "Weekly template added successfuly.";
        return "Failed to add weekly template.";
    }

    public boolean makeNextTemplateReservation(WeeklyTemplate template, String newResId, LocalDate today){
        return this.supplyController.makeNextTemplateReservation(template, newResId, today);
    }

    // 16. Edit reservation (allowed up to 24 hours before delivery date)
    public String editReservation(String supplierName, String resID, String barcode,int newQuantity){
        return this.supplyController.editReservation(SimulatedClock.getInstance().getCurrentDate(), supplierName, resID, barcode, newQuantity);
    }

    protected Optional<Supplier> findSupplierById(String supplierId) {
        return this.supplyController.findSupplierById(supplierId);
    }
    protected Optional<Supplier> findSupplierByName(String supplierName) {
        return this.supplyController.findSupplierByName(supplierName);
    }

    public SimpleEntry<Supplier, SupplierAgreement> findBestSupplierAndAgreementForProduct(String productBarcode, int quantity){
        return this.supplyController.findBestSupplierAndAgreementForProduct(productBarcode, quantity);
    }

    public List<Supplier> getAllSuppliers(){
        return this.supplyController.getAllSuppliers();
    }

    public List<WeeklyTemplate> getAllTemplates(){
        return this.supplyController.getAllTemplates();
    }
}
