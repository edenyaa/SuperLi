package serviceLayer;

import java.util.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import Backend.DTO.DeliveryDTO;
import Backend.DTO.LocationDTO;
import Backend.ServiceLayer.ServiceLayerT.ManagerService;
import Backend.ServiceLayer.ServiceLayerT.ShipmentService;
import domainLayerInventory.*;
import domainLayerSuppliers.*;

public class StorageController {

    private SupplyService supplierService;
    private InventoryService inventoryService;

    public StorageController() {
        this.supplierService = new SupplyService();
        this.inventoryService = new InventoryService();
    }

    //.....Suppliers Service functions.....//

    public String showAllSuppliers(){
        return this.supplierService.showAllSuppliers();
    }

    public String showAllSupplierReservations(String supplierID){
        return this.supplierService.getAllReservations(supplierID);
    }

    public String showSupplierSupplyDays(String supplierID){
        return this.supplierService.getSupplierSupplyDays(supplierID);
    }

    public String showSupplierContacts(String supplierID){
        return this.supplierService.getSupplierContacts(supplierID);
    }

    public String addNewSupplier(String name, String address, String paymentTypeInput, String bankAccount,
                          String contactName, String contactPhone, String contactEmail, List<String> supplyDayStrings){
        return this.supplierService.addSupplier(name, address, paymentTypeInput, bankAccount, contactName, contactPhone, contactEmail, supplyDayStrings);
    }

    public String removeSupplier(String supplierID){
        // Step 1: Attempt to remove the supplier
    Optional<Supplier> supplierToRemove = this.supplierService.findSupplierById(supplierID);
    String result = this.supplierService.removeSupplier(supplierID);

    // Step 2: Check if the supplier was successfully removed
    if (!result.endsWith("was removed.")) {
        return result; // Return error message
    }

    // Step 3: Get all products
    List<Product> allProducts = this.inventoryService.inventoryController.getAllProducts();
    Supplier foundSupplier = supplierToRemove.get();

    // Step 4: Remove supplier's name from each product's supplier list
    for (Product product : allProducts) {
        List<String> suppliers = product.getSuppliers();
        if (suppliers.contains(foundSupplier.getName())) {
            int index = suppliers.indexOf(foundSupplier.getName());
            suppliers.remove(supplierID);
            product.getCostPrices().remove(index);
        }
    }
    return result; // Return the original successful message   
    }

    public String makeNewReservation(String supplierId, List<String> productNums, List<Integer> quantities, String agreementId) {
    Optional<Supplier> supplierOpt = this.supplierService.findSupplierById(supplierId);
    Supplier supplier = supplierOpt.get();
    SupplierAgreement agreement = supplier.searchAgreement(agreementId);
    LocalDate reservationDate = SimulatedClock.getInstance().getCurrentDate();

    if (agreement.hasRegularDays()) {
        return this.supplierService.addReservation(supplierId, productNums, quantities, agreementId, reservationDate);
    }

    // Build item list for delivery
    LinkedList<SimpleEntry<String, Integer>> listOfItems = new LinkedList<>();
    for (int i = 0; i < productNums.size(); i++) {
        String barcode = productNums.get(i);
        int quantity = quantities.get(i);
        String productName = this.inventoryService.getProduct(barcode).getName();
        listOfItems.add(new SimpleEntry<>(productName, quantity));
    }

    // === Get Supplier Location (must exist) ===
    String supplierAreaName = "suppliersArea";
    LocationDTO supplierLocation;
    try {
        supplierLocation = ManagerService.getInstance().getLocationByAddress(supplierAreaName, supplier.getAddress());
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Supplier location not found in area '" + supplierAreaName + "' at address '" + supplier.getAddress() + "'");
    }

    // === Get Supermarket Location (must exist) ===
    String superAreaName = "beer sheva";
    String superAddress = "David Ben Gurion 1";
    LocationDTO superLocation;
    try {
        superLocation = ManagerService.getInstance().getLocationByAddress(superAreaName, superAddress);
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Supermarket location not found in area '" + superAreaName + "' at address '" + superAddress + "'");
    }

    // === Create Delivery and Update Stock ===
    DeliveryDTO deliveryDTO = new DeliveryDTO(
        ManagerService.getInstance().getMaxDeliveryID() + 1,
        reservationDate,
        reservationDate,
        superLocation,
        supplierLocation,
        listOfItems
    );
    ShipmentService.getInstance().addDelivery(deliveryDTO);

    // === Update Stock ===
    for (int i = 0; i < productNums.size(); i++) {
        String barcode = productNums.get(i);
        int quantity = quantities.get(i);
        Product product = this.inventoryService.getProduct(barcode);
        int newQuantity = product.getTotalQuantity() + quantity;
        this.inventoryService.updateStock(barcode, newQuantity);
    }
    return "Reservation added successfully with a self delivery";
}


    public String makeNewReservationTemplate(String supplierId, List<String> productNums, List<Integer> quantities, String agreementId,DayOfWeek supplyDay){
        return this.supplierService.addResTemplate(supplierId, productNums, quantities, agreementId, supplyDay);
    }

    public String editReservation(String supplierName, String resID, String barcode,int newQuantity){
        return this.supplierService.editReservation(supplierName,resID,barcode,newQuantity);
    }

    public String AddNewAgreementForSupplier(String supplierId, String contactName, String contactPhone, String contactEmail,
                                         List<String[]> productDetails, boolean hasRegularDays) {
    // First, add the agreement through the service
    String result = this.supplierService.addAgreementToSupplier(supplierId, contactName, contactPhone, contactEmail, productDetails, hasRegularDays);

    // Only proceed if the agreement was added successfully
    if (!result.startsWith("Agreement added to supplier")) {
        return result;
    }

    // Now update each product mentioned in the agreement
    for (String[] detail : productDetails) {
        if (detail.length < 3) continue;
        String barcode = detail[1];
        double price;
        try {
            price = Double.parseDouble(detail[2]);
        } catch (NumberFormatException e) {
            continue; // Skip invalid price
        }

        Product product = this.inventoryService.inventoryController.getProductByCode(barcode);
        Optional<Supplier> supplier = this.supplierService.findSupplierById(supplierId);
        if (product != null) {
            if (!product.getSuppliers().contains(supplier.get().getName())) {
                product.getSuppliers().add(supplier.get().getName());
                product.getCostPrices().add(price);
            }
        }
    }
    return result;
}

    public String removeAgreementForSupplier(String supplierId, String agreementId){
        // Step 1: Get the agreement BEFORE removing it to access its products
    Supplier supplier = this.supplierService.findSupplierById(supplierId).orElse(null);
    if (supplier == null) return "Supplier not found: " + supplierId;

    SupplierAgreement agreement = supplier.searchAgreement(agreementId);
    if (agreement == null) return "Agreement not found: " + agreementId;

    List<SupplierProduct> productsInAgreement = new ArrayList<>(agreement.getProducts());

    // Step 2: Remove the agreement using service
    String result = this.supplierService.removeAgreementFromSupplier(supplierId, agreementId);

    // Step 3: If successful, update product info
    if (result.startsWith("Agreement removed")) {
        for (SupplierProduct sp : productsInAgreement) {
            Product product = this.inventoryService.inventoryController.getProductByCode(sp.getProductNum());
            if (product != null) {
                int index = product.getSuppliers().indexOf(supplier.getName());
                product.getSuppliers().remove(supplier.getName());
                product.getCostPrices().remove(index);
            }
        }
    }
    return result;
    }

    public String addNewContactForSupplier(String supplierId, String name, String phone, String email){
        return this.supplierService.addContactPersonToSupplier(supplierId,name,phone,email);
    }

    public String addNewProductToSupplierAgreement(String supplierId, String agreementId, String productName, double productPrice, String productNum){
        // Step 1: Call the service method to add the product to the supplier agreement
    String result = this.supplierService.addProductToAgreement(supplierId, agreementId, productName, productPrice, productNum);

    // Step 2: If successful, update the product's suppliers and prices
    if (result.startsWith("Product added to agreement")) {
        Product product = this.inventoryService.inventoryController.getProductByCode(productNum);
        if (product != null) {
            if (!product.getSuppliers().contains(supplierId)) {
                product.getSuppliers().add(supplierId);
            }
            if (!product.getCostPrices().contains(productPrice)) {
                product.getCostPrices().add(productPrice);
            }
        }
    }

    return result;
    }

    public String removeProductFromSupplierAgreement(String supplierId, String agreementId, String productCode){
        String result = this.supplierService.removeProductFromAgreement(supplierId, agreementId, productCode);

    if (result.startsWith("Product removed")) {
        Product product = this.inventoryService.inventoryController.getProductByCode(productCode);
        Optional<Supplier> supplier = this.supplierService.findSupplierById(supplierId);
        Supplier supplierToRemove = supplier.get();
        if (product != null) {
            // Remove supplier and cost price directly since no other agreement from this supplier can include it
            List<String> suppliers = product.getSuppliers();
            List<Double> prices = product.getCostPrices();

            int index = suppliers.indexOf(supplierToRemove.getName());
            if (index != -1) {
                suppliers.remove(index);
                if (index < prices.size()) {
                    prices.remove(index);
                }
            }
        }
    }

    return result;
    }

    public String showAllSupplierAgreements(String supplierId){
        return this.supplierService.showAllSupplierAgreements(supplierId);
    }

    public String showAllSupplierDiscountsForProduct(String supplierId, String productNum){
        return this.supplierService.showSupplierDiscountForProduct(supplierId,productNum);
    }


    //.....Inventory Service functions.....//

    public String showProductByBarcode(String barcode){
        return this.inventoryService.getProductByCode(barcode);
    }
    public Product finProductByCode(String barcode){
        return this.inventoryService.getProduct(barcode);
    }
    public String updateProductStock(String barcode, int stock) {
    String msg = this.inventoryService.updateStock(barcode, stock);
    if (!msg.equals("Stock updated sucessfully")) {
        return msg;
    }
    Product product = this.inventoryService.inventoryController.getProductByCode(barcode);
    if (!product.isBelowThreshold()) {
        return msg; // no need to reorder
    }
    int reorderQty = product.getMinimumThreshold() * 2;
    // Use the new method returning both supplier and agreement
    var bestSupplierAndAgreement = this.supplierService.findBestSupplierAndAgreementForProduct(barcode, reorderQty);
    if (bestSupplierAndAgreement == null) {
        return "No supplier found that can supply product " + barcode + " in required quantity";
    }
    Supplier bestSupplier = bestSupplierAndAgreement.getKey();
    SupplierAgreement agreement = bestSupplierAndAgreement.getValue();
    String supplierId = bestSupplier.getSID();
    String agreementId = agreement.getAgreementID();
    List<String> productNums = List.of(barcode);
    List<Integer> quantities = List.of(reorderQty);
    return "Product stock has been updated, " + makeNewReservation(supplierId, productNums, quantities, agreementId);
}

public String registerPurchase(Map<String, Integer> boughtItems) {
    String purchaseMsg = this.inventoryService.registerPurchase(boughtItems);
    if (!purchaseMsg.equals("The purchase was registered sucessfully")) {
        return purchaseMsg;
    }

    StringBuilder resultMsg = new StringBuilder(purchaseMsg + "\n");

    // Group products by best supplier and agreement
    Map<Supplier, Map<SupplierAgreement, List<ProductRes>>> reservationsMap = new HashMap<>();

    for (Map.Entry<String, Integer> entry : boughtItems.entrySet()) {
        String barcode = entry.getKey();
        Product product = this.inventoryService.inventoryController.getProductByCode(barcode);
        if (product == null || !product.isBelowThreshold()) continue;

        int reorderQty = product.getMinimumThreshold() * 2;

        var bestSupplierAndAgreement = this.supplierService.findBestSupplierAndAgreementForProduct(barcode, reorderQty);
        if (bestSupplierAndAgreement == null) {
            resultMsg.append("No supplier found for product ").append(barcode).append("\n");
            continue;
        }

        Supplier bestSupplier = bestSupplierAndAgreement.getKey();
        SupplierAgreement agreement = bestSupplierAndAgreement.getValue();

        SupplierProduct sp = agreement.searchProduct(barcode);
        if (sp == null) continue; // extra safety

        ProductRes pr = new ProductRes(sp, reorderQty);

        reservationsMap
            .computeIfAbsent(bestSupplier, k -> new HashMap<>())
            .computeIfAbsent(agreement, k -> new ArrayList<>())
            .add(pr);
    }

    // Create reservations from grouped map
    for (Map.Entry<Supplier, Map<SupplierAgreement, List<ProductRes>>> supEntry : reservationsMap.entrySet()) {
        Supplier supplier = supEntry.getKey();
        Map<SupplierAgreement, List<ProductRes>> agreementsMap = supEntry.getValue();

        for (Map.Entry<SupplierAgreement, List<ProductRes>> agrEntry : agreementsMap.entrySet()) {
            SupplierAgreement agreement = agrEntry.getKey();
            List<ProductRes> productsList = agrEntry.getValue();

            List<String> productNums = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();

            for (ProductRes pr : productsList) {
                productNums.add(pr.getProduct().getProductNum());
                quantities.add(pr.getQuantity());
            }

            String reservationMsg = this.supplierService.addReservation(
                supplier.getSID(),
                productNums,
                quantities,
                agreement.getAgreementID(),
                SimulatedClock.getInstance().getCurrentDate()
            );

            resultMsg.append(reservationMsg).append("\n");
        }
    }

    return resultMsg.toString();
}

     
    public String showReportByCategory(List<String> categoryNames){
        return this.inventoryService.generateReportByCategory(categoryNames);
    }

    public String showInsufficiencyReport(){
        return this.inventoryService.generateInsufficientStockReport();
    }

    public String addNewProduct(String barcode, String name, List<String> categories, String manufacturer,
                            Double sellPrice, String expiryPeriod, String location, int quantityOnShelf, int quantityInStorage,
                            int minimumThreshold, String supplierName,int delTime)
    {
        Optional<Supplier> supplierOpt = supplierService.findSupplierByName(supplierName);
            if (supplierOpt.isEmpty()) return "Supplier " + supplierName + " not found.";
            
            Supplier supplier = supplierOpt.get();
            SupplierAgreement matchingAgreement = null;
            SupplierProduct matchedProduct = null;
        
            for (SupplierAgreement agreement : supplier.getAgreements()) {
                SupplierProduct product = agreement.searchProduct(barcode);
                if (product != null) {
                    matchedProduct = product;
                    matchingAgreement = agreement;
                    break;
                }
            }
        
            if (matchedProduct == null || matchingAgreement == null) {
                return "Supplier does not offer product with barcode: " + barcode + " in any agreement.";
            }
        
            double costPrice = matchedProduct.getPrice();
            String contactInfo = matchingAgreement.getContactPerson().getPhone();
        
            // Call inventory service to add the product
            return inventoryService.addNewProduct(
                barcode, name, categories, manufacturer, costPrice,
                sellPrice, expiryPeriod, location, quantityOnShelf,
                quantityInStorage, minimumThreshold, supplierName, contactInfo, 
                delTime
            );
    }

    public String showMostDemandedItems(int limit){
        return this.inventoryService.getMostDemandedItems(limit);
    }

    public String showDemandForItem(String barcode){
        return this.inventoryService.getDemandForItem(barcode);
    }

    public String applyDiscount(Date startDate,Date endDate,int percentage, Double fixedPrice, List<String> itemBarcodes, String category){
        return this.inventoryService.applyDiscount(startDate, endDate, percentage, fixedPrice, itemBarcodes,category);   
    }

    public String calculateMinimums(){
        return this.inventoryService.calculateMinByDemandForAllProducts();
    }

    public String reportExpiredProduct(String barcode, int quantity, String location, String reportedBy){
       String msg = this.inventoryService.addExpiryReport(barcode, quantity, location, reportedBy);
    if (!msg.startsWith("Expiry report issued successfully")) {
        return msg;
    }
    Product product = this.inventoryService.inventoryController.getProductByCode(barcode);
    if (!product.isBelowThreshold()) {
        return msg; // no need to reorder
    }
    int reorderQty = product.getMinimumThreshold() * 2;
    // Use the new method returning both supplier and agreement
    var bestSupplierAndAgreement = this.supplierService.findBestSupplierAndAgreementForProduct(barcode, reorderQty);
    if (bestSupplierAndAgreement == null) {
        return "No supplier found that can supply product " + barcode + " in required quantity";
    }
    Supplier bestSupplier = bestSupplierAndAgreement.getKey();
    SupplierAgreement agreement = bestSupplierAndAgreement.getValue();
    String supplierId = bestSupplier.getSID();
    String agreementId = agreement.getAgreementID();
    List<String> productNums = List.of(barcode);
    List<Integer> quantities = List.of(reorderQty);
    return "Expired item's stock has been updated, " + makeNewReservation(supplierId, productNums, quantities, agreementId);
    }

    public String showExpiryReport(int days){
        return this.inventoryService.generateReport(days);
    }

    public String showAllExpiryReports(){
        return this.inventoryService.getAllReports();
    }

    public String resetAllExpiryReports(){
        return this.inventoryService.resetAllReports();
    }

    public String showExpiryReportsByProduct(String barcode){
        return this.inventoryService.getReportsByProduct(barcode);
    }

    //.....Time simulation.....//
    public String forwardDays(int days){
        for(int i=0 ; i<days ; i++){
            doDailyTasks();
            SimulatedClock.getInstance().advanceDay();
        }
        return SimulatedClock.getInstance().getCurrentDate().toString();
    }

    private void doDailyTasks() {
        LocalDate today = SimulatedClock.getInstance().getCurrentDate();
    
        for (Supplier supplier : supplierService.getAllSuppliers()) {
    
            // 1. Handle due reservations
            List<Reservation> toDeliver = new ArrayList<>();
            for (Reservation reservation : new ArrayList<>(supplier.getReservations())) {
                if (reservation.getDeliveryDate().equals(today)) {
                    // Deliver items to inventory
                    for (ProductRes pr : reservation.getResProducts()) {
                        Product product = this.inventoryService.inventoryController.getProductByCode(pr.getProduct().getProductNum());
                        inventoryService.updateStock(pr.getProduct().getProductNum(), pr.getQuantity()+product.getTotalQuantity());
                    }
                    toDeliver.add(reservation); // collect for removal after iteration
                }
            }
            // Remove delivered reservations
            for (Reservation res : toDeliver) {
                supplier.removeReservation(res);
            }
    
            // 2. Handle weekly templates
            for (WeeklyTemplate template : supplierService.getAllTemplates()) {
                if (template.getNextReservationDate().equals(today.plusDays(2))) {
                    String newResId = IdGenerator.generateResID();
                    template.moveToNext(today);
                    this.supplierService.makeNextTemplateReservation(template, newResId, today);
                }
            }
        }
        this.inventoryService.clearExpiredDiscounts();
    }

    //.....Integration methods.....//
    public String checkIfProductExists(String productName){
        return this.inventoryService.getProductByName(productName);
    }
}