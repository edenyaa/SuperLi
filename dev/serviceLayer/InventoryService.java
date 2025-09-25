package serviceLayer;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import domainLayerInventory.*;

public class InventoryService {

    protected InventoryController inventoryController;
    protected ExpiryController expiryController;

    public InventoryService(){
        this.inventoryController = new InventoryController();
        this.expiryController = new ExpiryController();
    }

    public String getProductByCode(String barcode){
        Product foundProduct = this.inventoryController.getProductByCode(barcode);
        if(foundProduct != null) return foundProduct.displayFullDetails(SimulatedClock.getInstance().getCurrentDate());
        else return "No matching item was found, please check your input";
    }

    public String getProductByName(String name){
        Product foundProduct = this.inventoryController.getProductByName(name);
        if(foundProduct != null) return foundProduct.displayFullDetails(SimulatedClock.getInstance().getCurrentDate());
        else return null;
    }

    public Product getProduct(String barcode){
        return this.inventoryController.getProductByCode(barcode);
    }

    public String updateStock(String barcode,int stock){
        boolean succes = this.inventoryController.updateStock(barcode, stock);
        if(succes) return "Stock updated sucessfully";
        return "Failed to update stock";
    }

    public String registerPurchase(Map<String,Integer> boughtItems){
        boolean operation = this.inventoryController.registerPurchase(boughtItems,SimulatedClock.getInstance().getCurrentDate());
        if(!operation) return "Something went wrong with the purchase - either invalid barcodes or insufficient inventory";
        return "The purchase was registered sucessfully";
    }

    public String generateReportByCategory(List<String> categoryNames){
        List<Product> foundProducts = this.inventoryController.generateReportByCategory(categoryNames);
        if(foundProducts.size() == 0) return "No matching items were found";
        else
        {
            StringBuilder report = new StringBuilder();
            for (Product product : foundProducts) {
                report.append(product.displayCategoryView()).append("\n"); // Add product info with a newline
            }
            return report.toString();
        }   
    }

    public String generateInsufficientStockReport(){
        List<Product> foundProducts = this.inventoryController.generateInsufficientStockReport();
        if(foundProducts.size() == 0) return "No matching items were found";
        else
        {
            StringBuilder report = new StringBuilder();
            for (Product product : foundProducts) {
                report.append(product.displayInsufficiencyView()).append("\n"); // Add product info with a newline
            }
            return report.toString();
        }   
    }

    public String addNewProduct(String barcode, String name, List<String> categories, String manufacturer, Double costPrice,
                            Double sellPrice, String expiryPeriod, String location, int quantityOnShelf, int quantityInStorage,
                            int minimumThreshold, String supplier, String supplierContact, int delTime) {
        List<String>suppliers = new ArrayList<String>();
        suppliers.add(supplier);
        List<Double>costPrices = new ArrayList<Double>();
        costPrices.add(costPrice);
        Product newProduct = new Product(barcode, name, manufacturer, costPrices, sellPrice, expiryPeriod, location,
                                     quantityOnShelf, quantityInStorage, minimumThreshold, suppliers, delTime);

        // Add the product to the inventory
        boolean isProductAdded = inventoryController.addNewProduct(newProduct,categories);

        // Return an appropriate message based on whether the product was added or not
        if (isProductAdded) {
                return "Product added successfully!";
            } 
        return "Failed to add product, might already exist.";
    }

    public String getMostDemandedItems(int limit){
        List<Product> foundProducts = this.inventoryController.getMostDemandedItems(limit);
        if(foundProducts.size() == 0) return "No matching items were found";
        else
        {
            StringBuilder report = new StringBuilder();
            for (Product product : foundProducts) {
                report.append(product.displayDemandSummary()).append("\n"); // Add product info with a newline
            }
            return report.toString();
        }   
    }

    public String getDemandForItem(String barcode){
        List<Integer> demandByMonths = this.inventoryController.getDemandForItem(barcode);
        if(demandByMonths.size() == 0) return "No matches were found";
        else return demandByMonths.toString();
    }

    public String applyDiscount(Date startDate,Date endDate,int percentage, Double fixedPrice, List<String> itemBarcodes, String category) {
        // Create a new Discount object
        String newDiscountId = IdGenerator.generateDiscountID();
        Discount.Status appliesTo = Discount.Status.ITEM;
        Category foundCategory = null;
        if(category == "") {
            appliesTo = Discount.Status.ITEM;
        } else {
            appliesTo = Discount.Status.CATEGORY;
            foundCategory = this.inventoryController.getCategoryByName(category);
        }
        Discount newDiscount = new Discount(newDiscountId, startDate, endDate, percentage, fixedPrice, appliesTo, itemBarcodes, foundCategory);
        boolean applied = this.inventoryController.applyDiscount(newDiscount); 
        if (applied) {
            return "Discount applied successfully.";
        } else {
            return "Failed to apply discount.";
        }
    }

    public String calculateMinByDemandForAllProducts() {
        boolean calculated = inventoryController.calculateMinByDemandForAllProducts(); 
        if(calculated) return "Minimum stock thresholds calculated based on demand.";
        return "Failed to calculate minimum stock thresholds.";
    }

public String addExpiryReport(String barcode, int quantity, String location, String reportedBy) {
    // Check if the product exists
    Product product = inventoryController.getProductByCode(barcode);
    if (product == null) {
        return "Error: Product with barcode " + barcode + " not found.";
    }
    // Check if the reported quantity is valid
    int totalAvailable = product.getTotalQuantity();
    if (quantity > totalAvailable) {
        return "Error: Reported quantity exceeds available stock. Available: " + totalAvailable;
    }
    // Withdraw the quantity
    int remainingToWithdraw = quantity;
    // First try to withdraw from the shelf
    int shelfQty = product.getQuantityOnShelf();
    if (shelfQty >= remainingToWithdraw) {
        product.setQuantityOnShelf(shelfQty - remainingToWithdraw);
    } else {
        product.setQuantityOnShelf(0);
        remainingToWithdraw -= shelfQty;

        // Then withdraw the rest from storage
        int storageQty = product.getQuantityInStorage();
        product.setQuantityInStorage(storageQty - remainingToWithdraw);
    }
    // Create a new ExpiryReport
    String reportId = IdGenerator.generateExpiryReportID();
    ExpiryReport report = new ExpiryReport(reportId,barcode,quantity,location,SimulatedClock.getInstance().getCurrentDate(),reportedBy);
    boolean added = this.expiryController.addExpiryReport(report);
    boolean updated = this.inventoryController.updateProductQuantity(product);
    if (added == false || updated == false) {
        return "Error: Failed to register expiry report.";
    }
    return "Expiry report issued successfully. ID: " + reportId;
}
    
public String generateReport(int days) {
    List<ExpiryReport> recentReports = expiryController.generateReport(days, LocalDate.now());

    if (recentReports.isEmpty()) {
        return "No expiry reports in the last " + days + " days.";
    }

    StringBuilder result = new StringBuilder("Expiry Reports in the last " + days + " days:\n");
    for (ExpiryReport report : recentReports) {
        result.append(report.toString()).append("\n");
    }

    return result.toString();
}


public String getAllReports() {
    List<ExpiryReport> allReports = expiryController.getAllReports();

    if (allReports.isEmpty()) {
        return "There are no expiry reports at the moment.";
    }

    StringBuilder sb = new StringBuilder("All Expiry Reports:\n");
    for (ExpiryReport report : allReports) {
        sb.append(report.toString()).append("\n");
    }

    return sb.toString();
}
  
public String resetAllReports() {
    List<ExpiryReport> allReports = expiryController.getAllReports();
    if(allReports.size() == 0) return "No expiry reports to reset.";
    expiryController.resetAllReports();
    return "All expiry reports have been successfully reset.";
}

public String getReportsByProduct(String barcode) {
    List<ExpiryReport> reports = expiryController.getReportsByProduct(barcode);
    if (reports.isEmpty()) {
        return "No expiry reports found for product with barcode: " + barcode;
    }
    return reports.stream()
            .map(ExpiryReport::toString)
            .collect(Collectors.joining("\n"));
}

public void clearExpiredDiscounts() {
        LocalDate today = SimulatedClock.getInstance().getCurrentDate();  // Get the current date
        if(!this.inventoryController.getDiscounts().isEmpty()) { 
            Iterator<Discount> iterator = this.inventoryController.getDiscounts().iterator();
        while (iterator.hasNext()) {
            Discount discount = iterator.next();
            // If the discount has expired
            if (discount.getEndDate().toInstant().isBefore(today.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())) {
    
                // Remove from products by barcode (ITEM discount)
                for (String barcode : discount.getItemBarcodes()) {
                    Product p = this.inventoryController.getProductByCode(barcode);
                    if (p != null && p.getDiscount() != null && p.getDiscount().getId() == discount.getId()) {
                        p.setDiscount(null);  // Clear the discount
                    }
                }
                // Remove from products by category (CATEGORY discount)
                if (discount.getAppliesTo() == Discount.Status.CATEGORY && discount.getItemsCategory() != null) {
                    for (Product product : this.inventoryController.getAllProducts()) {
                        // Check if the product belongs to the category the discount applies to
                        for (Category category : product.getCategories()) {
                            if (category.equals(discount.getItemsCategory()) &&
                                product.getDiscount() != null &&
                                product.getDiscount().getId() == discount.getId()) {
                                product.setDiscount(null);  // Clear the discount
                                break;  // No need to check other categories for this product
                            }
                        }
                    }
                }
                // Remove from the storeDiscounts list
                iterator.remove();
            }
        }
    } 
}   
}
