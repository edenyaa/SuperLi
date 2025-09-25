package domainLayerSuppliers;

public class ProductRes {
    private SupplierProduct product;
    private int quantity;

    public ProductRes(SupplierProduct product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public SupplierProduct getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public void increaseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Increase quantity cannot be negative");
        }
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Decrease quantity cannot be negative");
        }
        if (this.quantity - quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be reduced below zero");
        }
        this.quantity -= quantity;
    }

    @Override
    public String toString() {
        return product.toString() + "\n" +
               "Quantity: " + quantity;
    }
}
