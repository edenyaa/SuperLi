package domainLayerInventory;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class Product {
    private String barcode;
    private String name;
    private List<Category> categories;
    private String manufacturer;
    private List<Double> costPrices;
    private Double sellPrice;
    private String expiryPeriod;
    private int quantityOnShelf;
    private String location;
    private int quantityInStorage;
    private int minimumThreshold;
    private List<String> suppliers;
    private int delTime; // delivery time in days
    private Discount discount;
    private int[] monthlyDemand;

    public Product(String barcode, String name, String manufacturer, List<Double> costPrices, Double sellPrice,
                   String expiryPeriod, String location, int quantityOnShelf, int quantityInStorage, int minimumThreshold,
                   List<String> suppliers, int delTime) {
        this.barcode = barcode;
        this.name = name;
        this.categories = new ArrayList<Category>();
        this.manufacturer = manufacturer;
        this.costPrices = costPrices;
        this.sellPrice = sellPrice;
        this.expiryPeriod = expiryPeriod;
        this.location = location;
        this.quantityOnShelf = quantityOnShelf;
        this.quantityInStorage = quantityInStorage;
        this.minimumThreshold = minimumThreshold;
        this.suppliers = suppliers;
        this.delTime = delTime;
        this.discount = null;
        this.monthlyDemand = new int[12];
    }

    public Product(String barcode, String name, String manufacturer, List<Double> costPrices, Double sellPrice,
                   String expiryPeriod, String location, int quantityOnShelf, int quantityInStorage, int minimumThreshold,
                   List<String> suppliers, int delTime , int[] monthlyDemand) {
        this.barcode = barcode;
        this.name = name;
        this.categories = new ArrayList<Category>();
        this.manufacturer = manufacturer;
        this.costPrices = costPrices;
        this.sellPrice = sellPrice;
        this.expiryPeriod = expiryPeriod;
        this.location = location;
        this.quantityOnShelf = quantityOnShelf;
        this.quantityInStorage = quantityInStorage;
        this.minimumThreshold = minimumThreshold;
        this.suppliers = suppliers;
        this.delTime = delTime;
        this.discount = null;
        this.monthlyDemand = monthlyDemand;
    }

    // --- Getters ---
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public List<Category> getCategories() { return categories; }
    public String getManufacturer() { return manufacturer; }
    public List<Double> getCostPrices() { return costPrices; }
    public Double getSellPrice() { return sellPrice; }
    public String getExpiryPeriod() { return expiryPeriod; }
    public String getLocation() { return this.location; }
    public int getQuantityOnShelf() { return quantityOnShelf; }
    public int getQuantityInStorage() { return quantityInStorage; }
    public int getMinimumThreshold() { return minimumThreshold; }
    public List<String> getSuppliers() { return suppliers; }
    public int getDelTime() { return delTime; }
    public Discount getDiscount() { return discount; }
    public int[] getMonthlyDemand() { return monthlyDemand;} 

    // --- Setters ---
    public void addCostPrice(Double costPrice) { this.costPrices.add(costPrice); }
    public void addSupplier(String supplier) { this.suppliers.add(supplier); }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }
    public void setDiscount(Discount discount) { this.discount = discount; }
    public void setCategories(List<Category> categories) { this.categories = categories;}
    public void setLocation(String location){
        this.location = location;
    }
    public void setQuantityOnShelf(int quantityOnShelf) {
        this.quantityOnShelf = quantityOnShelf;
    }

    public void setQuantityInStorage(int quantityInStorage) {
        this.quantityInStorage = quantityInStorage;
    }

    // --- Utility Methods ---
    public void addDemand(int monthIndex, int amount) {
        if (monthIndex >= 0 && monthIndex < 12) {
            monthlyDemand[monthIndex] += amount;
        }
    }

    public int getTotalQuantity() {
        return quantityOnShelf + quantityInStorage;
    }

    public boolean isBelowThreshold() {
        return getTotalQuantity() < minimumThreshold;
    }

    public int calculateDynamicMinimumThreshold() {
        int totalDemand = 0;
        int monthsWithDemand = 0;
        int dynamicThreshold = this.minimumThreshold;
        for (int demand : monthlyDemand) {
            if (demand > 0) {
                totalDemand += demand;
                monthsWithDemand++;
            }
        }
        if (totalDemand != 0 && monthsWithDemand != 0) {
            int totalDays = monthsWithDemand * 30; // only count relevant months
            float dailyAverage = (float) totalDemand / totalDays;
            dynamicThreshold = (int) Math.ceil(dailyAverage * delTime);
            this.minimumThreshold = dynamicThreshold;
        }
        return dynamicThreshold;
    }
    
    public Double getEffectivePrice(LocalDate currentDate) {
        if (discount == null) return sellPrice;
    
        LocalDate startDate = discount.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = discount.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    
        if (currentDate.isBefore(startDate) || currentDate.isAfter(endDate)) {
            return sellPrice;
        }
    
        if (discount.getDiscountSetPrice() > 0) {
            return discount.getDiscountSetPrice();
        } else if (discount.getPercentage() > 0) {
            double discounted = sellPrice * (1 - discount.getPercentage() / 100.0);
            return Math.round(discounted * 100.0) / 100.0;
        }
    
        return sellPrice;
    }
    
    
    public int getTotalDemand(int periodLimit) {
        int total = 0;
        // Get current month index 
        int currentMonth = LocalDate.now().getMonthValue() - 1;
        for (int i = 0; i < periodLimit; i++) {
            int index = (currentMonth - i + 12) % 12;
            total += monthlyDemand[index];
        }
        return total;
    }

    public boolean removeSupplier(String supplierToRemove){
        if(this.suppliers.size()>1) {
            this.costPrices.remove(this.suppliers.indexOf(supplierToRemove));
            this.suppliers.remove(supplierToRemove);
            return true;
        }
        return false;
    }
    
    // Full details, including effective price (requires currentDate)
public String displayFullDetails(LocalDate currentDate) {
    return "Product{" +
            "barcode='" + barcode + '\'' +
            ", name='" + name + '\'' +
            ", categories=" + categories +
            ", manufacturer='" + manufacturer + '\'' +
            ", costPrices=" + costPrices +
            ", sellPrice=" + sellPrice +
            ", effectivePrice=" + getEffectivePrice(currentDate) +
            ", expiryPeriod='" + expiryPeriod + '\'' +
            ", location='" + location + '\'' +
            ", quantityOnShelf=" + quantityOnShelf +
            ", quantityInStorage=" + quantityInStorage +
            ", minimumThreshold=" + minimumThreshold +
            ", suppliers=" + suppliers +
            ", delTime=" + delTime +
            ", discount=" + (discount != null ? discount.toString() : "None") +
            '}';
}

// Common info for category report
public String displayCategoryView() {
    return "Product{" +
            "barcode='" + barcode + '\'' +
            ", name='" + name + '\'' +
            ", categories=" + categories +
            ", manufacturer='" + manufacturer + '\'' +
            ", sellPrice=" + sellPrice +
            '}';
}

// Common info for insufficient stock report
public String displayInsufficiencyView() {
    return "Product{" +
            "barcode='" + barcode + '\'' +
            ", name='" + name + '\'' +
            ", quantityOnShelf=" + quantityOnShelf +
            ", quantityInStorage=" + quantityInStorage +
            ", minimumThreshold=" + minimumThreshold +
            '}';
}

// Basic info for most demanded items
public String displayDemandSummary() {
    return "Product{" +
            "barcode='" + barcode + '\'' +
            ", name='" + name + '\'' +
            ", totalDemand=" + getTotalDemand(12) +
            '}';
}
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return barcode.equals(product.barcode);
}

}