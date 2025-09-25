package domainLayerInventory;

import java.time.LocalDate;
import java.util.Objects;

public class ExpiryReport {

    private String id;
    private String barcode;
    private int quantityExpired;
    private String location;
    private LocalDate reportedAt;
    private String reportedBy;

    // Constructor
    public ExpiryReport(String id, String barcode, int quantityExpired,
                        String location, LocalDate reportedAt, String reportedBy) {
        this.id = id;
        this.barcode = barcode;
        this.quantityExpired = quantityExpired;
        this.location = location;
        this.reportedAt = reportedAt;
        this.reportedBy = reportedBy;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public int getQuantityExpired() { return quantityExpired; }
    public void setQuantityExpired(int quantityExpired) { this.quantityExpired = quantityExpired; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDate reportedAt) { this.reportedAt = reportedAt; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    // toString
    @Override
    public String toString() {
        return "ExpiryReport{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", quantityExpired=" + quantityExpired +
                ", location='" + location + '\'' +
                ", reportedAt=" + reportedAt +
                ", reportedBy='" + reportedBy + '\'' +
                '}';
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpiryReport)) return false;
        ExpiryReport that = (ExpiryReport) o;
        return id == that.id &&
               Objects.equals(barcode, that.barcode);
    }

}
