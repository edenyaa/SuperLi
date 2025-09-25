package dto;

public record ExpiryReportDTO(String id, String barcode, int quantityExpired, String location,
                              String reportedAt, String reportedBy) {}