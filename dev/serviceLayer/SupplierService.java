package serviceLayer;

import domainLayerSuppliers.*;
import domainLayerSuppliers.PaymentCondition.PaymentType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierService {
    protected List<Supplier> suppliers;
    protected List<WeeklyTemplate> templates;

    public SupplierService() {
        suppliers = new ArrayList<>();
        templates = new ArrayList<>();
    }

    // 1. Show all suppliers
    public String getAllSuppliers() {
        if (suppliers.isEmpty()) return "No suppliers available.";
        StringBuilder sb = new StringBuilder("Suppliers:\n");
        for (Supplier s : suppliers) {
            sb.append(formatSupplierSummary(s)).append("\n");
        }
        return sb.toString();
    }

    private String formatSupplierSummary(Supplier s) {
        return String.format("ID: %s, Name: %s", s.getSID(), s.getName());
    }

    // 2. Show all reservations from a supplier
    public String getAllReservations(String supplierId) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;
        List<Reservation> reservations = supplierOpt.get().getReservations();
        if (reservations.isEmpty()) return "No reservations found for supplier: " + supplierId;
        return formatReservations(reservations);
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
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;
        List<DayOfWeek> days = supplierOpt.get().getSupplyDays();
        if (days.isEmpty()) return "No supply days set for supplier: " + supplierId;
        return "Supply days for supplier " + supplierId + ": " + formatSupplyDays(days);
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
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;
        return supplierOpt.get().getContactsSummary();
    }

    // 5. Add new supplier
    public String addSupplier(String name, String address, String paymentTypeInput, String bankAccount,
                              String contactName, String contactPhone, String contactEmail, List<String> supplyDayStrings) {

        // Convert string to PaymentType
        PaymentType paymentType;
        try {
            paymentType = PaymentType.valueOf(paymentTypeInput.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Invalid payment type: must be one of TRANSFER, CASH, or CHECK.";
        }

        PaymentCondition paymentCondition = new PaymentCondition(paymentType, bankAccount);

        // Convert supply day strings to DayOfWeek
        List<DayOfWeek> supplyDays = new ArrayList<>();
        for (String dayStr : supplyDayStrings) {
            try {
                DayOfWeek day = DayOfWeek.valueOf(dayStr.trim().toUpperCase());
                supplyDays.add(day);
            } catch (IllegalArgumentException e) {
                return "Invalid supply day: " + dayStr + ". Must be a valid day of the week.";
            }
        }

        // Generate ID and create contact
        String generatedID = IdGenerator.generateSupplierID();
        ContactPerson initialContact = new ContactPerson(contactName, contactPhone, contactEmail);
        List<ContactPerson> contacts = new ArrayList<>();
        contacts.add(initialContact);

        // Create supplier and add to system
        Supplier supplier = new Supplier(name, generatedID, address, paymentCondition, contacts, null, supplyDays);
        if (findSupplierById(supplier.getSID()).isPresent()) {
            return "Supplier with ID " + supplier.getSID() + " already exists.";
        }

        suppliers.add(supplier);
        return "Supplier added successfully: " + supplier.getSID();
    }

    // 6. Remove a supplier
    public String removeSupplier(String supplierId) {
        boolean removed = suppliers.removeIf(s -> s.getSID().equals(supplierId));
        return removed ? "Supplier removed: " + supplierId : "Supplier not found: " + supplierId;
    }

    // 7. Add immediate reservation to supplier
    public String addReservation(String supplierId, List<String> productNums, List<Integer> quantities, String agreementId, LocalDate reservationDate) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;

        Supplier supplier = supplierOpt.get();
        SupplierAgreement agreement = supplier.searchAgreement(agreementId);
        if (agreement == null) return "Agreement not found: " + agreementId;

        if (productNums.size() != quantities.size()) return "Mismatched product and quantity lists.";

        List<ProductRes> resProducts = new ArrayList<>();
        for (int i = 0; i < productNums.size(); i++) {
            String prodNum = productNums.get(i);
            SupplierProduct product = agreement.searchProduct(prodNum);
            if (product == null) return "Product " + prodNum + " not found in agreement.";

            int qty = quantities.get(i);
            if (qty <= 0) return "Quantity must be positive for product: " + prodNum;

            resProducts.add(new ProductRes(product, qty));
        }

        String reservationId = IdGenerator.generateResID();
        new Reservation(reservationId, resProducts, agreementId, reservationDate, supplier,null);
        return "Reservation created: " + reservationId;
    }

    // 8. Add agreement to supplier
    public String addAgreementToSupplier(String supplierId, String contactName, String contactPhone, String contactEmail,
                                         List<String[]> productDetails, boolean hasRegularDays) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) {
            return "Supplier not found: " + supplierId;
        }

        ContactPerson contactPerson = new ContactPerson(contactName, contactPhone, contactEmail);
        List<SupplierProduct> products = new ArrayList<>();

        for (String[] details : productDetails) {
            // Expecting each entry as: [productName, productPrice, productBarcode]
            if (details.length != 3) continue;
            try {
                String productName = details[0];
                double price = Double.parseDouble(details[1]);
                String barcode = details[2];
                products.add(new SupplierProduct(productName, price, barcode));
            } catch (NumberFormatException e) {
                return "Invalid price format for product: " + details[0];
            }
        }

        String agreementId = IdGenerator.generateAgreementID();
        SupplierAgreement agreement = new SupplierAgreement(agreementId, contactPerson, null, products, hasRegularDays);

        supplierOpt.get().addAgreement(agreement);
        return "Agreement added to supplier " + supplierId;
    }

    // 9. Remove agreement from supplier
    public String removeAgreementFromSupplier(String supplierId, String agreementId) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;

        Supplier supplier = supplierOpt.get();
        Optional<SupplierAgreement> agreementOpt = supplier.getAgreements().stream()
                .filter(a -> a.getAgreementID().equals(agreementId))
                .findFirst();

        if (agreementOpt.isEmpty()) return "Agreement not found: " + agreementId;

        boolean removed = supplier.removeAgreement(agreementOpt.get());
        return removed ? "Agreement removed: " + agreementId : "Failed to remove agreement: " + agreementId;
    }

    // 10. Add contact person to supplier
    public String addContactPersonToSupplier(String supplierId, String name, String phone, String email) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) {
            return "Supplier not found: " + supplierId;
        }

        ContactPerson newContact = new ContactPerson(name, phone, email);
        supplierOpt.get().addContactPerson(newContact);
        return "Contact person added to supplier " + supplierId;
    }

    // 11. Add a product to an agreement of a supplier
    public String addProductToAgreement(String supplierId, String agreementId, String productName, double productPrice, String productNum) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;

        Optional<SupplierAgreement> agreementOpt = supplierOpt.get().getAgreements().stream()
                .filter(a -> a.getAgreementID().equals(agreementId))
                .findFirst();
        if (agreementOpt.isEmpty()) return "Agreement not found: " + agreementId;

        SupplierProduct newProduct = new SupplierProduct(productName, productPrice, productNum);
        agreementOpt.get().addProduct(newProduct);
        return "Product added to agreement " + agreementId;
    }

    // 12. Remove a product from an agreement of a supplier
    public String removeProductFromAgreement(String supplierId, String agreementId, String productCode) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;
        Optional<SupplierAgreement> agreementOpt = supplierOpt.get().getAgreements().stream()
                .filter(a -> a.getAgreementID().equals(agreementId))
                .findFirst();
        if (agreementOpt.isEmpty()) return "Agreement not found: " + agreementId;
        boolean removed = agreementOpt.get().removeProduct(productCode);
        return removed ? "Product removed: " + productCode : "Product not found: " + productCode;
    }

    // Internal helper: find supplier by ID
    protected Optional<Supplier> findSupplierById(String supplierId) {
        return suppliers.stream()
                .filter(s -> s.getSID().equals(supplierId))
                .findFirst();
    }
    // Internal helper: find supplier by Name
    protected Optional<Supplier> findSupplierByName(String supplierName) {
        return suppliers.stream()
                .filter(s -> s.getName().equals(supplierName))
                .findFirst();
    }

    // 13. Show all supplier agreements by supplier ID
    public String showAllSupplierAgreements(String supplierId) {
        for (Supplier supplier : suppliers) {
            if (supplier.getSID().equals(supplierId)) {
                List<SupplierAgreement> agreements = supplier.getAgreements();
                if (agreements.isEmpty()) {
                    return "Supplier with ID " + supplierId + " has no agreements.";
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Agreements for Supplier ID ").append(supplierId).append(":\n");
                for (SupplierAgreement agreement : agreements) {
                    sb.append("-----------------------------------\n");
                    sb.append(agreement.toString()).append("\n");
                }
                return sb.toString();
            }
        }

        return "Supplier with ID " + supplierId + " not found.";
    }

    // 14. Show supplier discount for a product by supplier ID and product Number
    public String showSupplierDiscountForProduct(String supplierId, String productNum) {
        // Find supplier by ID
        for (Supplier supplier : suppliers) {
            if (supplier.getSID().equals(supplierId)) {
                // Find the product in the supplier's agreements
                for (SupplierAgreement agreement : supplier.getAgreements()) {
                    SupplierProduct product = agreement.searchProduct(productNum);
                    if (product != null) {
                        // Use the existing method to get product agreements (discounts)
                        List<ProductAgreement> discounts = supplier.getProductAgreementsToProduct(product);
                        if (!discounts.isEmpty()) {
                            // Return the first discount's toString
                            return discounts.get(0).toString();
                        } else {
                            return "No discount found for product " + productNum + " under supplier " + supplierId + ".";
                        }
                    }
                }
                return "Product " + productNum + " not found under supplier " + supplierId + ".";
            }
        }
        return "Supplier with ID " + supplierId + " not found.";
    }

    //15. Add periodic reservation
    public String addResTemplate(String supplierId, List<String> productNums, List<Integer> quantities, String agreementId, LocalDate reservationDate,DayOfWeek supplyDay){
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return "Supplier not found: " + supplierId;

        Supplier supplier = supplierOpt.get();
        SupplierAgreement agreement = supplier.searchAgreement(agreementId);
        if (agreement == null) return "Agreement not found: " + agreementId;

        if (productNums.size() != quantities.size()) return "Mismatched product and quantity lists.";

        List<ProductRes> resProducts = new ArrayList<>();
        for (int i = 0; i < productNums.size(); i++) {
            String prodNum = productNums.get(i);
            SupplierProduct product = agreement.searchProduct(prodNum);
            if (product == null) return "Product " + prodNum + " not found in agreement.";

            int qty = quantities.get(i);
            if (qty <= 0) return "Quantity must be positive for product: " + prodNum;

            resProducts.add(new ProductRes(product, qty));
        }

        String templateId = IdGenerator.generateTemplateID();
        WeeklyTemplate newTemplate = new WeeklyTemplate(templateId, supplier, resProducts, agreement, SimulatedClock.getInstance().getCurrentDate(),supplyDay);
        templates.add(newTemplate);
        return "Template created: " + templateId;
    }

    // 16. Edit reservation (allowed up to 24 hours before delivery date)
    public String editReservation(String supplierName, String resID, String barcode,int newQuantity){
        Optional<Supplier> supplier = this.findSupplierByName(supplierName);
        if (supplier.get() == null) return "Supplier not found";
        Supplier foundSupplier = supplier.get();
        Reservation foundRes = foundSupplier.searchRes(resID);
        if(foundRes == null) return "Reservation doesn't belong to supplier or doesn't exist";
        LocalDate today = SimulatedClock.getInstance().getCurrentDate();
        LocalDate deliveryDate = foundRes.getDeliveryDate();

        if (!today.isBefore(deliveryDate.minusDays(1))) {
            return "Error: Reservation can only be edited more than 24 hours before delivery (" + deliveryDate + ").";
        }
        List<ProductRes> items = foundRes.getResProducts();
        ProductRes targetItem = null;
        // Search for the product in the reservation
        for (ProductRes item : items) {
            if (item.getProduct().getProductNum().equals(barcode)) {
                targetItem = item;
                break;
            }
        }
        if (targetItem != null) {
            if (newQuantity == 0) {
                items.remove(targetItem);
                return "Product removed from reservation.";
            } else {
                targetItem.setQuantity(newQuantity);
                return "Product quantity updated to " + newQuantity + ".";
            }
        } else {
            // Not in reservation, check if it's part of the supplier's agreement
            SupplierAgreement agreement = foundRes.getResAgreement();
            if (agreement != null && agreement.searchProduct(barcode)!= null) {
                if (newQuantity <= 0) {
                    return "Error: Cannot add product with zero or negative quantity.";
                }
                ProductRes newProductRes = new ProductRes(agreement.searchProduct(barcode), newQuantity);
                items.add(newProductRes);
                return "Product added to reservation with quantity " + newQuantity + ".";
            } else {
                return "Error: Product is not part of the supplier's agreement.";
            }
        }
    }

    //.....Protected Utility Functions.....//

    protected SimpleEntry<Supplier, SupplierAgreement> findBestSupplierAndAgreementForProduct(String productBarcode, int quantity) {
        Supplier bestSupplier = null;
        SupplierAgreement bestAgreement = null;
        double bestPrice = Double.MAX_VALUE;

        // Iterate all suppliers in SupplierService
        for (Supplier supplier : this.suppliers) {
            for (SupplierAgreement agreement : supplier.getAgreements()) {
                // Check if agreement supplies this product
                SupplierProduct sp = agreement.searchProduct(productBarcode);
                if (sp == null) continue;

                // Calculate price for this quantity
                double priceForQuantity = sp.getPrice() * quantity;

                // Check for any discounts for this product in this agreement
                for (ProductAgreement pa : agreement.getSupplierProductsDiscount()) {
                    if (pa.getProduct().getProductName().equals(productBarcode)) {
                        // Check if quantity qualifies for discount (assuming pa has minQuantity field)
                        if (quantity >= pa.getMinQuantity()) {
                            double discountPrice = pa.getDiscount() * quantity;
                            if (discountPrice < priceForQuantity) {
                                priceForQuantity = discountPrice;
                            }
                        }
                    }
                }
                // Keep track of the best price and corresponding supplier/agreement
                if (priceForQuantity < bestPrice) {
                    bestPrice = priceForQuantity;
                    bestSupplier = supplier;
                    bestAgreement = agreement;
                }
            }
        }
        if (bestSupplier == null || bestAgreement == null) return null;
        return new SimpleEntry<>(bestSupplier, bestAgreement);
    }

}