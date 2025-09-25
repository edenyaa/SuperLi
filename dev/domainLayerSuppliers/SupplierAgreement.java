package domainLayerSuppliers;

import java.util.ArrayList;
import java.util.List;

public class SupplierAgreement {
    private final List<SupplierAgreement> changedHistory;
    private String agreementID;
    private ContactPerson contactPerson;
    private final List<ProductAgreement> supplierProductsDiscount;
    private final List<SupplierProduct> products;
    private boolean hasRegularDays;

    public SupplierAgreement(String ID, ContactPerson contactPerson, List<ProductAgreement> supplierProductsDiscount, List<SupplierProduct> products, boolean hasRegularDays) {
        this.changedHistory = new ArrayList<>();
        this.agreementID = ID;
        this.contactPerson = contactPerson;
        this.supplierProductsDiscount = (supplierProductsDiscount != null) ? new ArrayList<>(supplierProductsDiscount) : new ArrayList<>();
        this.products = (products != null) ? new ArrayList<>(products) : new ArrayList<>();
        this.hasRegularDays = hasRegularDays;
    }

    public String getAgreementID() { return agreementID; }
    public ContactPerson getContactPerson() { return contactPerson; }
    public List<ProductAgreement> getSupplierProductsDiscount() { return supplierProductsDiscount; }
    public List<SupplierProduct> getProducts() { return products; }
    public boolean hasRegularDays() { return hasRegularDays; }
    public List<SupplierAgreement> getChangeHistory() { return changedHistory; }

    public void setAgreementID(String ID) { this.agreementID = ID; }
    public void setContactPerson(ContactPerson person) { this.contactPerson = person; }
    public void setHasRegularDays(boolean hasRegularDays) { this.hasRegularDays = hasRegularDays; }

    public double getDiscountFromProducts(SupplierProduct product) {
        for (ProductAgreement p : supplierProductsDiscount) {
            if (p.getProduct().equals(product)) {
                return p.getDiscount();
            }
        }
        return 0.0;
    }

    public void addProduct(SupplierProduct product) {
        changedHistory.add(cloneAgreement());
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).equals(product)) {
                products.set(i, product); // Update existing
                return;
            }
        }
        products.add(product); // Add new
    }

    public SupplierProduct searchProduct(String productNumber) {
        for (SupplierProduct p : products) {
            if (p.getProductNum().equals(productNumber)) {
                return p;
            }
        }
        return null;
    }

    public boolean removeProduct(String productNum) {
    changedHistory.add(cloneAgreement());
    SupplierProduct toRemove = null;
    for (SupplierProduct p : products) {
        if (p.getProductNum().equals(productNum)) {
            toRemove = p;
            break;
        }
    }
    if (toRemove != null) {
        products.remove(toRemove);
        SupplierProduct finalToRemove = toRemove;
        supplierProductsDiscount.removeIf(pa -> pa.getProduct().equals(finalToRemove));
        return true; // removal succeeded
    }
    return false; // product not found
}

    public void addDiscount(ProductAgreement productAgreement) {
        changedHistory.add(cloneAgreement());
        SupplierProduct targetProduct = productAgreement.getProduct();

        if (!products.contains(targetProduct)) {
            products.add(targetProduct);
        }

        supplierProductsDiscount.removeIf(pa -> pa.getProduct().equals(targetProduct));
        supplierProductsDiscount.add(productAgreement);
    }

    public void removeDiscount(ProductAgreement productAgreement) {
        changedHistory.add(cloneAgreement());

        SupplierProduct targetProduct = productAgreement.getProduct();
        if (!products.contains(targetProduct)) {
            return;
        }

        supplierProductsDiscount.removeIf(pa -> pa.getProduct().equals(targetProduct));
    }

    public int getMinAmountOfProduct(SupplierProduct sp) {
        for (ProductAgreement p : supplierProductsDiscount) {
            if (sp.equals(p.getProduct())) {
                return p.getMinQuantity();
            }
        }
        return 0;
    }

    public boolean equals(SupplierAgreement other) {
        return this.agreementID.equals(other.getAgreementID());
    }

    private SupplierAgreement cloneAgreement() {
        return new SupplierAgreement(
                agreementID,
                contactPerson,
                new ArrayList<>(supplierProductsDiscount),
                new ArrayList<>(products),
                hasRegularDays
        );
    }

    @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Supplier Agreement ID: ").append(agreementID).append("\n");
    sb.append("Contact Person: ").append(contactPerson != null ? contactPerson.toString() : "None").append("\n");
    sb.append("Has Regular Days: ").append(hasRegularDays ? "Yes" : "No").append("\n");

    sb.append("Products:\n");
    for (SupplierProduct product : products) {
        sb.append("  - ").append(product.getProductName())
          .append(" (Num: ").append(product.getProductNum()).append(")\n");
    }

    return sb.toString();
}

}
