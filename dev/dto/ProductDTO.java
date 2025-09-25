package dto;

public record ProductDTO(String barcode, String name, String manufacturer, double sellPrice,
                         String expiryPeriod, int quantityOnShelf, int quantityInStorage,
                         int minimumThreshold, String location, int deliveryTime) {}
