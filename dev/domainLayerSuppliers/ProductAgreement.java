package domainLayerSuppliers;

public class ProductAgreement {
    private SupplierProduct product;
    private int minQuantity;
    private double discount; // discount as a decimal (e.g., 0.15 for 15%)

    public ProductAgreement(SupplierProduct product, int minQuantity, double discount) {
        if (minQuantity <= 0 || discount < 0) {
            throw new IllegalArgumentException("Cannot create ProductAgreement with negative minQuantity or discount");
        }
        if (discount > 1) {
            throw new IllegalArgumentException("Cannot create ProductAgreement with discount greater than 1");
        }
        this.product = product;
        this.minQuantity = minQuantity;
        this.discount = discount;
    }

    public int getMinQuantity() { return minQuantity; }
    public double getDiscount() { return discount; }
    public SupplierProduct getProduct() { return product; }
    
    public void setProduct(SupplierProduct product) { this.product = product; }
    public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }
    public void setDiscount(double discount) { this.discount = discount; }

    @Override
    public String toString() {
        return "Product: " + (product != null ? product.toString() : "null") + "\n" +
               "Minimum Quantity: " + minQuantity + "\n" +
               "Discount: " + (discount * 100) + "%\n";
    }
}
