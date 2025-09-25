package domainLayerSuppliers;

import java.util.Objects;

public class SupplierProduct {
    private final String productName;
    private final String productSNum;
    private final double price;

    public SupplierProduct(String productName, double productPrice, String productNum) {
        this.productName = productName;
        this.price = productPrice;
        this.productSNum = productNum;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductNum() {
        return productSNum;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SupplierProduct)) return false;
        SupplierProduct other = (SupplierProduct) obj;
        return Objects.equals(productSNum, other.productSNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productSNum);
    }

    @Override
    public String toString() {
        return "SupplierProduct{" +
                "name='" + productName + '\'' +
                ", serialNumber='" + productSNum + '\'' +
                ", price=" + price +
                '}';
    }
}
