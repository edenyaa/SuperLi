package domainLayerInventory;

import util.AppConfig;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import dataAccessLayer.repository.DiscountRepositoryImpl;
import dataAccessLayer.repository.ProductRepositoryImpl;

public class InventoryController {

    private final ProductRepositoryImpl productRepository;
    private final DiscountRepositoryImpl discountRepository;

    public InventoryController() {
        this.productRepository = (ProductRepositoryImpl) AppConfig.productRepository;
        this.discountRepository = (DiscountRepositoryImpl) AppConfig.discountRepository;
    }

    public List<Product> getProducts() {
        return productRepository.getAll();
    }

    public List<Discount> getDiscounts() {
        return discountRepository.getAllDiscounts();
    }

    public Product getProductByCode(String barcode) {
        return productRepository.getByBarcode(barcode).orElse(null);
    }

    public Product getProductByName(String name) {
        return productRepository.getByName(name).orElse(null);
    }

    public List<Product> getAllProducts() {
        return getProducts();
    }

    public boolean registerPurchase(Map<String, Integer> boughtItems, LocalDate currentDate) {
        try {
            for (Map.Entry<String, Integer> entry : boughtItems.entrySet()) {
                Optional<Product> productOpt = productRepository.getByBarcode(entry.getKey());
                if (productOpt.isEmpty() || productOpt.get().getTotalQuantity() < entry.getValue()) {
                    System.out.println(productOpt.get().displayFullDetails(currentDate));
                    return false;
                }
            }

            for (Map.Entry<String, Integer> entry : boughtItems.entrySet()) {
                Product product = productRepository.getByBarcode(entry.getKey()).get();
                int quantityToBuy = entry.getValue();

                int quantityFromShelf = Math.min(product.getQuantityOnShelf(), quantityToBuy);
                product.setQuantityOnShelf(product.getQuantityOnShelf() - quantityFromShelf);

                int remainingToBuy = quantityToBuy - quantityFromShelf;
                if (remainingToBuy > 0) {
                    product.setQuantityInStorage(product.getQuantityInStorage() - remainingToBuy);
                }

                int refillAmount = Math.min(product.getQuantityInStorage(), 5 - product.getQuantityOnShelf());
                if (refillAmount > 0) {
                    product.setQuantityOnShelf(product.getQuantityOnShelf() + refillAmount);
                    product.setQuantityInStorage(product.getQuantityInStorage() - refillAmount);
                }

                int month = currentDate.getMonthValue();

                productRepository.updateProductQuantities(
                        product.getBarcode(),
                        product.getQuantityOnShelf(),
                        product.getQuantityInStorage()
                );
                productRepository.updateDemand(product.getBarcode(), month, quantityToBuy);
            }

        } catch (SQLException e) {
            System.err.println("Error during purchase: " + e.getMessage());
            System.out.println("Failed here");
            return false;
        }
        return true;
    }

    public List<Product> generateReportByCategory(List<String> categories) {
        List<Product> allProducts = productRepository.getAll();
        return allProducts.stream()
                .filter(product -> product.getCategories().stream()
                        .anyMatch(c -> categories.contains(c.getName())))
                .collect(Collectors.toList());
        
    }

    public List<Product> generateInsufficientStockReport() {
        List<Product> allProducts = productRepository.getAll();
        return allProducts.stream()
                .filter(Product::isBelowThreshold)
                .collect(Collectors.toList());
    }

    public boolean addNewProduct(Product newProduct, List<String> categoryNames) {
        try {
            if (productRepository.getByBarcode(newProduct.getBarcode()).isPresent()) return false;

            List<Category> categories = new ArrayList<>();
            Category currentParent = null;

            for (String name : categoryNames) {
                Category category = productRepository.getCategoryByNameAndParent(name, currentParent);
                if (category == null) {
                    category = new Category(name, currentParent);
                    productRepository.saveCategory(category);
                }
                currentParent = category;
                categories.add(category);
            }

            newProduct.setCategories(categories);
            productRepository.save(newProduct);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Product> getMostDemandedItems(int periodLimit) {
        List<Product> all = productRepository.getAll();
        return all.stream()
                .filter(p -> p.getTotalDemand(periodLimit) > 0)
                .sorted(Comparator.comparingInt((Product p) -> p.getTotalDemand(periodLimit)).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Integer> getDemandForItem(String barcode) {
        Optional<Product> productOpt = productRepository.getByBarcode(barcode);
        if (productOpt.isEmpty()) return Collections.emptyList();

        Product product = productOpt.get();
        return Arrays.stream(product.getMonthlyDemand()).boxed().toList();
    }

    public boolean applyDiscount(Discount newDiscount) {
        try {
            List<Product> affectedProducts = new ArrayList<>();

            if (newDiscount.getAppliesTo() == Discount.Status.ITEM) {
                for (String barcode : newDiscount.getItemBarcodes()) {
                    Optional<Product> productOpt = productRepository.getByBarcode(barcode);
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        product.setDiscount(newDiscount);
                        productRepository.update(product);
                        affectedProducts.add(product);
                    }
                }
            } else if (newDiscount.getAppliesTo() == Discount.Status.CATEGORY) {
                List<Product> all = productRepository.getAll();
                for (Product product : all) {
                    if (product.getCategories().contains(newDiscount.getItemsCategory())) {
                        product.setDiscount(newDiscount);
                        productRepository.update(product);
                        affectedProducts.add(product);
                    }
                }
            }

            if (!affectedProducts.isEmpty()) {
                discountRepository.saveDiscount(newDiscount);
                return true;
            }
            return false;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean calculateMinByDemandForAllProducts() {
    try {
        List<Product> all = productRepository.getAll();
        for (Product product : all) {
            int dynamicMin = product.calculateDynamicMinimumThreshold();
            productRepository.updateMinThreshold(product.getBarcode(), dynamicMin);
        }
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public Category getCategoryByName(String name) {
        return productRepository.getCategoryByName(name);
    }

    public boolean updateProductQuantity(Product product) {
        try {
            productRepository.updateProductQuantities(
                    product.getBarcode(),
                    product.getQuantityOnShelf(),
                    product.getQuantityInStorage()
            );
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateStock(String barcode, int stock) {
        try {
            Optional<Product> productOpt = productRepository.getByBarcode(barcode);
            if (productOpt.isEmpty()) return false;

            Product product = productOpt.get();
            int shelf = Math.min(stock, 20);
            int storage = stock - shelf;

            product.setQuantityOnShelf(shelf);
            product.setQuantityInStorage(storage);

            productRepository.updateProductQuantities(barcode, shelf, storage);
            return true;

        } catch (SQLException e) {
            return false;
        }
    }
}
