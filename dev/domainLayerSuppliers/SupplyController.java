package domainLayerSuppliers;

import domainLayerSuppliers.PaymentCondition.PaymentType;
import util.AppConfig;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;

public class SupplyController {
    private final SupplierRepository supplierRepository;
    private final ReservationRepository reservationRepository;
    private final WeeklyTemplateRepository templateRepository;

    public SupplyController() {
        supplierRepository = AppConfig.supplierRepository;
        reservationRepository = AppConfig.reservationRepository;
        templateRepository = AppConfig.weeklyTemplateRepository;
    }

    public List<Supplier> getAllSuppliers() {
        try {
            return supplierRepository.getAllSuppliers();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch suppliers from database", e);
        }
    }

    public List<WeeklyTemplate> getAllTemplates() {
        try {
            List<WeeklyTemplate> allTemplates = new ArrayList<>();
            for (Supplier supplier : getAllSuppliers()) {
                List<WeeklyTemplate> supplierTemplates = templateRepository.getAllTemplates(supplier);
                if (supplierTemplates != null && !supplierTemplates.isEmpty()) {
                    allTemplates.addAll(supplierTemplates);
                }
            }
            return allTemplates;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch templates from database", e);
        }
    }

    public Optional<Supplier> findSupplierById(String supplierId) {
        return getAllSuppliers().stream()
                .filter(s -> s.getSID().equals(supplierId))
                .findFirst();
    }

    public Optional<Supplier> findSupplierByName(String supplierName) {
        return getAllSuppliers().stream()
                .filter(s -> s.getName().equals(supplierName))
                .findFirst();
    }

    public List<Reservation> getAllReservations(String supplierID) {
        return findSupplierById(supplierID)
                .map(Supplier::getReservations)
                .orElse(null);
    }

    public List<DayOfWeek> getSupplierSupplyDays(String supplierId) {
        return findSupplierById(supplierId)
                .map(Supplier::getSupplyDays)
                .orElse(null);
    }

    public String getSupplierContacts(String supplierId) {
        return findSupplierById(supplierId)
                .map(Supplier::getContactsSummary)
                .orElse("Supplier not found: " + supplierId);
    }

    public boolean addNewSupplier(String supplierId, String name, String address, String paymentTypeInput,
                                  String bankAccount, String contactName, String contactPhone,
                                  String contactEmail, List<String> supplyDayStrings) {
        PaymentType paymentType;
        try {
            paymentType = PaymentType.valueOf(paymentTypeInput.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        PaymentCondition paymentCondition = new PaymentCondition(paymentType, bankAccount);

        List<DayOfWeek> supplyDays = new ArrayList<>();
        for (String dayStr : supplyDayStrings) {
            try {
                supplyDays.add(DayOfWeek.valueOf(dayStr.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        ContactPerson initialContact = new ContactPerson(contactName, contactPhone, contactEmail);
        List<ContactPerson> contacts = List.of(initialContact);

        Supplier supplier = new Supplier(name, supplierId, address, paymentCondition, contacts, null, supplyDays);

        if (findSupplierById(supplierId).isPresent()) return false;

        try {
            supplierRepository.saveSupplier(supplier);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean removeSupplier(String supplierId) {
        try {
            supplierRepository.deleteSupplier(supplierId);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addReservation(String resId, String supplierId, List<String> productNums,
                                  List<Integer> quantities, String agreementId, LocalDate reservationDate) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return false;

        Supplier supplier = supplierOpt.get();
        SupplierAgreement agreement = supplier.searchAgreement(agreementId);
        if (agreement == null || productNums.size() != quantities.size()) return false;
        System.out.println(agreement.getContactPerson().getName());

        List<ProductRes> resProducts = new ArrayList<>();
        for (int i = 0; i < productNums.size(); i++) {
            SupplierProduct product = agreement.searchProduct(productNums.get(i));
            if (product == null || quantities.get(i) <= 0) return false;
            resProducts.add(new ProductRes(product, quantities.get(i)));
        }

        Reservation newRes = new Reservation(resId, resProducts, agreementId, reservationDate, supplier, null);
        try {
            reservationRepository.saveReservation(newRes, supplierId);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean addAgreementToSupplier(String agreementId, String supplierId, String contactName,
                                      String contactPhone, String contactEmail,
                                      List<String[]> productDetails, boolean hasRegularDays) {
    Optional<Supplier> supplierOpt = findSupplierById(supplierId);
    if (supplierOpt.isEmpty()) return false;

    ContactPerson contactPerson = new ContactPerson(contactName, contactPhone, contactEmail);
    List<SupplierProduct> products = new ArrayList<>();

    for (String[] details : productDetails) {
        if (details.length != 3) continue;
        try {
            products.add(new SupplierProduct(details[0], Double.parseDouble(details[1]), details[2]));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    SupplierAgreement agreement = new SupplierAgreement(agreementId, contactPerson, null, products, hasRegularDays);

    try {
        supplierRepository.saveSupplierAgreement(agreement, supplierId);
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    return true;
}


    public boolean removeAgreementFromSupplier(String supplierId, String agreementId) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return false;

        Optional<SupplierAgreement> agreementOpt = supplierOpt.get().getAgreements().stream()
                .filter(a -> a.getAgreementID().equals(agreementId))
                .findFirst();

        if (agreementOpt.isEmpty()) return false;

        boolean removed = supplierOpt.get().removeAgreement(agreementOpt.get());
        if (removed) {
            try {
                supplierRepository.deleteSupplierAgreement(agreementOpt.get());
            } catch (SQLException e) {
                return false;
            }
        }
        return removed;
    }

    public boolean addContactPersonToSupplier(String supplierId, String name, String phone, String email) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return false;

        ContactPerson newContact = new ContactPerson(name, phone, email);
        supplierOpt.get().addContactPerson(newContact);

        try {
            supplierRepository.saveContactPerson(newContact, supplierId);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean addProductToAgreement(String supplierId, String agreementId,
                                         String productName, double productPrice, String productNum) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return false;

        Optional<SupplierAgreement> agreementOpt = supplierOpt.get().getAgreements().stream()
                .filter(a -> a.getAgreementID().equals(agreementId))
                .findFirst();

        if (agreementOpt.isEmpty()) return false;

        SupplierProduct newProduct = new SupplierProduct(productName, productPrice, productNum);
        agreementOpt.get().addProduct(newProduct);

        try {
            supplierRepository.addProductToAgreement(agreementOpt.get(), newProduct);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean removeProductFromAgreement(String supplierId, String agreementId, String productCode) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return false;

        Optional<SupplierAgreement> agreementOpt = supplierOpt.get().getAgreements().stream()
                .filter(a -> a.getAgreementID().equals(agreementId))
                .findFirst();

        if (agreementOpt.isEmpty()) return false;

        boolean removed = agreementOpt.get().removeProduct(productCode);
        if (removed) {
            try {
                supplierRepository.removeProductFromAgreement(agreementOpt.get(), productCode);
            } catch (SQLException e) {
                return false;
            }
        }
        return removed;
    }

    public List<SupplierAgreement> showAllSupplierAgreements(String supplierId) {
        return findSupplierById(supplierId)
                .map(Supplier::getAgreements)
                .orElse(null);
    }

    public String showSupplierDiscountForProduct(String supplierId, String productNum) {
        return getAllSuppliers().stream()
                .filter(s -> s.getSID().equals(supplierId))
                .flatMap(s -> s.getAgreements().stream())
                .map(a -> a.searchProduct(productNum))
                .filter(p -> p != null)
                .findFirst()
                .map(p -> {
                    Supplier supplier = findSupplierById(supplierId).get();
                    List<ProductAgreement> discounts = supplier.getProductAgreementsToProduct(p);
                    return discounts.isEmpty() ? "No discount found..." : discounts.get(0).toString();
                })
                .orElse("Product or supplier not found.");
    }

    public boolean addResTemplate(String templateId, LocalDate currDate, String supplierId,
                                  List<String> productNums, List<Integer> quantities,
                                  String agreementId, DayOfWeek supplyDay) {
        Optional<Supplier> supplierOpt = findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) return false;

        Supplier supplier = supplierOpt.get();
        SupplierAgreement agreement = supplier.searchAgreement(agreementId);
        if (agreement == null || productNums.size() != quantities.size()) return false;

        List<ProductRes> resProducts = new ArrayList<>();
        for (int i = 0; i < productNums.size(); i++) {
            SupplierProduct product = agreement.searchProduct(productNums.get(i));
            if (product == null || quantities.get(i) <= 0) return false;
            resProducts.add(new ProductRes(product, quantities.get(i)));
        }

        WeeklyTemplate newTemplate = new WeeklyTemplate(templateId, supplier, resProducts, agreement, currDate, supplyDay);
        try {
            templateRepository.saveTemplate(newTemplate);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public String editReservation(LocalDate today, String supplierName, String resID, String barcode, int newQuantity) {
        Optional<Supplier> supplierOpt = findSupplierByName(supplierName);
        if (supplierOpt.isEmpty()) return "Supplier not found";

        Supplier supplier = supplierOpt.get();
        Reservation reservation = supplier.searchRes(resID);
        if (reservation == null) return "Reservation not found";

        if (!today.isBefore(reservation.getDeliveryDate().minusDays(1))) {
            return "Cannot edit reservation less than 24 hours before delivery.";
        }

        List<ProductRes> items = reservation.getResProducts();
        for (ProductRes item : items) {
            if (item.getProduct().getProductNum().equals(barcode)) {
                if (newQuantity == 0) {
                    items.remove(item);
                    try {
                        reservationRepository.removeItemFromReservation(resID, barcode);
                    } catch (SQLException e) {
                        return "Failed to remove item from reservation.";
                    }
                    return "Item removed.";
                } else {
                    item.setQuantity(newQuantity);
                    try {
                        reservationRepository.updateReservationItem(resID, barcode, newQuantity);
                    } catch (SQLException e) {
                        return "Failed to update item.";
                    }
                    return "Quantity updated.";
                }
            }
        }

        SupplierAgreement agreement = reservation.getResAgreement();
        if (agreement != null && agreement.searchProduct(barcode) != null) {
            if (newQuantity <= 0) {
                return "Invalid quantity.";
            }
            ProductRes newItem = new ProductRes(agreement.searchProduct(barcode), newQuantity);
            items.add(newItem);
            try {
                reservationRepository.addItemToReservation(resID, newItem);
            } catch (SQLException e) {
                return "Failed to add item.";
            }
            return "Item added.";
        }

        return "Product not part of agreement.";
    }

    public boolean makeNextTemplateReservation(WeeklyTemplate template, String newResId, LocalDate today) {
    Reservation newRes = new Reservation(
        newResId,
        template.getTemplateProducts(),
        template.getTemplateAgreement().getAgreementID(),
        today,
        template.getSupplier(),
        template.getSupplyDay()
    );

    try {
        reservationRepository.saveReservation(newRes, template.getSupplier().getSID());  // Persist to DB
        templateRepository.updateTemplate(template); // Persist template update
    } catch (SQLException e) {
        System.err.println("Failed to make reservation from weekly template: " + e.getMessage());
        return false;
    }

    return true;
}

    public SimpleEntry<Supplier, SupplierAgreement> findBestSupplierAndAgreementForProduct(String productBarcode, int quantity) {
    Supplier bestSupplier = null;
    SupplierAgreement bestAgreement = null;
    double bestPrice = Double.MAX_VALUE;

    try {
        List<Supplier> allSuppliers = supplierRepository.getAllSuppliers();

        for (Supplier supplier : allSuppliers) {
            List<SupplierAgreement> agreements = supplierRepository.getSupplier(supplier.getSID()).get().getAgreements();

            for (SupplierAgreement agreement : agreements) {
                SupplierProduct sp = agreement.searchProduct(productBarcode);
                if (sp == null) continue;

                double priceForQuantity = sp.getPrice() * quantity;

                for (ProductAgreement pa : agreement.getSupplierProductsDiscount()) {
                    if (pa.getProduct().getProductNum().equals(productBarcode)) {
                        if (quantity >= pa.getMinQuantity()) {
                            double discountPrice = pa.getDiscount() * quantity;
                            if (discountPrice < priceForQuantity) {
                                priceForQuantity = discountPrice;
                            }
                        }
                    }
                }

                if (priceForQuantity < bestPrice) {
                    bestPrice = priceForQuantity;
                    bestSupplier = supplier;
                    bestAgreement = agreement;
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error finding best supplier and agreement for product '" + productBarcode + "': " + e.getMessage());
        return null;
    }

    if (bestSupplier == null || bestAgreement == null) return null;
    return new SimpleEntry<>(bestSupplier, bestAgreement);
}

    
}
