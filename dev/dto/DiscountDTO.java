package dto;

public record DiscountDTO(String discountId, String startDate, String endDate, int percentage,
                          Double discountSetPrice, String appliesTo) {}
