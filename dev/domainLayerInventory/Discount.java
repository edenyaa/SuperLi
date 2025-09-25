package domainLayerInventory;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Discount {

    public enum Status {
        ITEM, CATEGORY
    }

    private String id;
    private Date startDate;
    private Date endDate;
    private int percentage;
    private Double discountSetPrice;
    private Status appliesTo;
    private List<String> itemBarcodes;        // Only relevant if appliesTo == ITEM
    private Category itemsCategory;    // Only relevant if appliesTo == CATEGORY

    public Discount(String id, Date startDate, Date endDate, int percentage, Double discountSetPrice, Status appliesTo, List<String> itemBarcodes, Category itemsCategory) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
        this.discountSetPrice = discountSetPrice;
        this.appliesTo = appliesTo;
        this.itemBarcodes = itemBarcodes;
        this.itemsCategory = itemsCategory;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getPercentage() {
        return percentage;
    }

    public Double getDiscountSetPrice(){
        return discountSetPrice;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Status getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(Status appliesTo) {
        this.appliesTo = appliesTo;
    }

    public List<String> getItemBarcodes() {
        return itemBarcodes;
    }

    public Category getItemsCategory() {
        return itemsCategory;
    }

    public void setItemsCategory(Category itemsCategory) {
        this.itemsCategory = itemsCategory;
    }

    public boolean isActive(Date date) {
        return date != null && !date.before(startDate) && !date.after(endDate);
    }

   @Override
   public String toString() {
       SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
       return "Discount{" +
               "id='" + id + '\'' +
               ", startDate=" + sdf.format(startDate) +
               ", endDate=" + sdf.format(endDate) +
               ", percentage=" + percentage +
               ", appliesTo=" + appliesTo +
               ", itemBarcodes=" + itemBarcodes +
               ", itemsCategory=" + itemsCategory +
               '}';
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discount)) return false;
        Discount discount = (Discount) o;
        return id == discount.id;
    }

}
