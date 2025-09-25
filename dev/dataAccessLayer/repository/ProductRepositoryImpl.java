package dataAccessLayer.repository;

import dataAccessLayer.dao.*;
import dto.*;
import domainLayerInventory.*;
import java.sql.SQLException;
import java.util.*;

public class ProductRepositoryImpl implements ProductRepository {
    private final ProductDAO productDAO;
    private final ProductCategoryDAO productCategoryDAO;
    private final ProductSupplierDAO productSupplierDAO;
    private final ProductDemandDAO productDemandDAO;
    private final DiscountRepository discountRepository;

    private final List<Product> products = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();

    public ProductRepositoryImpl(ProductDAO productDAO,
                                 ProductCategoryDAO productCategoryDAO,
                                 ProductSupplierDAO productSupplierDAO,
                                 ProductDemandDAO productDemandDAO,
                                 DiscountRepository discountRepository) throws SQLException {
        this.productDAO = productDAO;
        this.productCategoryDAO = productCategoryDAO;
        this.productSupplierDAO = productSupplierDAO;
        this.productDemandDAO = productDemandDAO;
        this.discountRepository = discountRepository;
        loadCache();
    }

    private void loadCache() throws SQLException {
        products.clear();
        categories.clear();

        Map<String, Category> categoryMap = new HashMap<>();
        List<ProductDTO> productDTOs = productDAO.getAll();
        List<ProductCategoryDTO> categoryDTOs = productCategoryDAO.getAll();
        List<ProductSupplierDTO> supplierDTOs = productSupplierDAO.getAll();
        List<ProductDemandDTO> demandDTOs = productDemandDAO.getAll();

        Map<String, List<Category>> productCategories = new HashMap<>();
        for (ProductCategoryDTO dto : categoryDTOs) {
            List<Category> catHierarchy = parseCategoryPath(dto.categoryPath(), categoryMap);
            productCategories.computeIfAbsent(dto.barcode(), k -> new ArrayList<>()).addAll(catHierarchy);
        }
        categories.addAll(new HashSet<>(categoryMap.values()));

        Map<String, List<String>> suppliersMap = new HashMap<>();
        Map<String, List<Double>> costPricesMap = new HashMap<>();
        for (ProductSupplierDTO dto : supplierDTOs) {
            suppliersMap.computeIfAbsent(dto.barcode(), k -> new ArrayList<>()).add(dto.supplierName());
            costPricesMap.computeIfAbsent(dto.barcode(), k -> new ArrayList<>()).add(dto.costPrice());
        }

        Map<String, int[]> demandMap = new HashMap<>();
        for (ProductDemandDTO dto : demandDTOs) {
            demandMap.computeIfAbsent(dto.barcode(), k -> new int[12])[dto.month() - 1] = dto.demand();
        }

        for (ProductDTO dto : productDTOs) {
            List<Category> prodCats = productCategories.getOrDefault(dto.barcode(), new ArrayList<>());
            List<String> suppliers = suppliersMap.getOrDefault(dto.barcode(), new ArrayList<>());
            List<Double> costPrices = costPricesMap.getOrDefault(dto.barcode(), new ArrayList<>());
            int[] monthlyDemand = demandMap.getOrDefault(dto.barcode(), new int[12]);

            Product product = new Product(
                    dto.barcode(), dto.name(), dto.manufacturer(),
                    costPrices, dto.sellPrice(), dto.expiryPeriod(),
                    dto.location(), dto.quantityOnShelf(), dto.quantityInStorage(),
                    dto.minimumThreshold(), suppliers, dto.deliveryTime(), monthlyDemand
            );
            product.setCategories(prodCats);

            findDiscountForProduct(dto.barcode(), prodCats).ifPresent(product::setDiscount);
            products.add(product);
        }
    }

    private Optional<Discount> findDiscountForProduct(String barcode, List<Category> categories) throws SQLException {
        List<Discount> itemDiscounts = discountRepository.getDiscountsByProduct(barcode);
        if (!itemDiscounts.isEmpty()) return Optional.of(itemDiscounts.get(0));

        List<Discount> allDiscounts = discountRepository.getAllDiscounts();
        for (Discount discount : allDiscounts) {
            if (discount.getAppliesTo() == Discount.Status.CATEGORY) {
                Category targetCat = discount.getItemsCategory();
                for (Category c : categories) {
                    if (c.equals(targetCat)) return Optional.of(discount);
                }
            }
        }
        return Optional.empty();
    }

    private List<Category> parseCategoryPath(String path, Map<String, Category> categoryMap) {
        String[] names = path.split("->");
        List<Category> hierarchy = new ArrayList<>();
        Category parent = null;
        for (String name : names) {
            String key = (parent == null ? "" : parent.getName() + "->") + name;
            Category current = categoryMap.get(key);
            if (current == null) {
                current = new Category(name, parent);
                categoryMap.put(key, current);
                if (parent != null) parent.addSubCategory(current);
            }
            parent = current;
            hierarchy.add(current);
        }
        return hierarchy;
    }

    @Override
    public Optional<Product> getByBarcode(String barcode) {
        return products.stream().filter(p -> p.getBarcode().equals(barcode)).findFirst();
    }

    @Override
    public Optional<Product> getByName(String name) {
        return products.stream().filter(p -> p.getName().equals(name)).findFirst();
    }

    @Override
    public List<Product> getAll() {
        return new ArrayList<>(products);
    }

    @Override
    public void save(Product product) throws SQLException {
        productDAO.insert(toDTO(product));
        for (Category category : product.getCategories()) {
            productCategoryDAO.insert(new ProductCategoryDTO(product.getBarcode(), buildCategoryPath(category)));
        }
        saveSuppliers(product);
        saveDemands(product);
        products.add(product);
    }

    @Override
    public void update(Product product) throws SQLException {
        productDAO.update(toDTO(product));
        productCategoryDAO.deleteByBarcode(product.getBarcode());
        for (Category category : product.getCategories()) {
            productCategoryDAO.insert(new ProductCategoryDTO(product.getBarcode(), buildCategoryPath(category)));
        }
        productSupplierDAO.deleteAllByBarcode(product.getBarcode());
        saveSuppliers(product);
        productDemandDAO.deleteAllByBarcode(product.getBarcode());
        saveDemands(product);
        products.removeIf(p -> p.getBarcode().equals(product.getBarcode()));
        products.add(product);
    }

    @Override
    public void delete(String barcode) throws SQLException {
        productCategoryDAO.deleteByBarcode(barcode);
        productSupplierDAO.deleteAllByBarcode(barcode);
        productDemandDAO.deleteAllByBarcode(barcode);
        productDAO.delete(barcode);
        products.removeIf(p -> p.getBarcode().equals(barcode));
    }

    @Override
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    @Override
public void updateMinThreshold(String barcode, int newThreshold) throws SQLException {
    productDAO.updateMinimumThreshold(barcode, newThreshold);  // Just delegate directly to DAO
}

    @Override
    public void updateProductQuantities(String barcode, int quantityOnShelf, int quantityInStorage) throws SQLException {
        Product product = getByBarcode(barcode).orElseThrow(() -> new SQLException("Product not found: " + barcode));
        product.setQuantityOnShelf(quantityOnShelf);
        product.setQuantityInStorage(quantityInStorage);
        productDAO.updateQuantities(barcode, quantityOnShelf, quantityInStorage);
    }

    @Override
    public void addDemand(String barcode, int month, int quantity) throws SQLException {
        Product product = getByBarcode(barcode).orElseThrow(() -> new SQLException("Product not found: " + barcode));
        product.addDemand(month - 1, quantity);
        productDemandDAO.insert(new ProductDemandDTO(barcode, month, product.getMonthlyDemand()[month - 1]));
    }

    @Override
    public void updateDemand(String barcode, int month, int quantity) throws SQLException {
        Product product = getByBarcode(barcode).orElseThrow(() -> new SQLException("Product not found: " + barcode));
        product.addDemand(month - 1, quantity);
        productDemandDAO.update(new ProductDemandDTO(barcode, month, quantity));
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getBarcode(), product.getName(), product.getManufacturer(),
                product.getSellPrice(), product.getExpiryPeriod(), product.getQuantityOnShelf(),
                product.getQuantityInStorage(), product.getMinimumThreshold(),
                product.getLocation(), product.getDelTime());
    }

    private void saveSuppliers(Product product) throws SQLException {
        List<String> suppliers = product.getSuppliers();
        List<Double> costPrices = product.getCostPrices();
        for (int i = 0; i < suppliers.size(); i++) {
            productSupplierDAO.insert(new ProductSupplierDTO(product.getBarcode(), suppliers.get(i), costPrices.get(i)));
        }
    }

    private void saveDemands(Product product) throws SQLException {
        int[] demand = product.getMonthlyDemand();
        for (int month = 0; month < demand.length; month++) {
            if (demand[month] > 0) {
                productDemandDAO.insert(new ProductDemandDTO(product.getBarcode(), month + 1, demand[month]));
            }
        }
    }

    private String buildCategoryPath(Category category) {
        List<String> names = new ArrayList<>();
        while (category != null) {
            names.add(category.getName());
            category = category.getParentCategory();
        }
        Collections.reverse(names);
        return String.join("->", names);
    }

    @Override
    public Category getCategoryByNameAndParent(String name, Category parent) {
        return categories.stream()
                .filter(cat -> cat.getName().equals(name) && Objects.equals(cat.getParentCategory(), parent))
                .findFirst().orElse(null);
    }

    @Override
    public void saveCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    @Override
    public Category getCategoryByName(String name) {
        return categories.stream()
                .filter(cat -> cat.getName().equals(name))
                .findFirst().orElse(null);
    }
}
