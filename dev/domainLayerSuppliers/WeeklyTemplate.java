package domainLayerSuppliers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class WeeklyTemplate {
        private String templateId;
        private Supplier supplier;
        private List<ProductRes> templateProducts;
        private SupplierAgreement templateAgreement;
        private LocalDate reservationDate;         // Date the last reservation was created
        private LocalDate nextReservationDate;     // Next scheduled reservation date
        private DayOfWeek supplyDay;               // Day of week for supply
    
        public WeeklyTemplate(String templateId, Supplier supplier, List<ProductRes> templateProducts,
                              SupplierAgreement templateAgreement, LocalDate creationDate,
                              DayOfWeek supplyDay) {
            this.templateId = templateId;
            this.supplier = supplier;
            this.templateProducts = templateProducts;
            this.templateAgreement = templateAgreement;
            this.supplyDay = supplyDay;

            // initialize both to the first run:
            this.reservationDate = creationDate;
            this.nextReservationDate = calcNext(creationDate, supplyDay);
        }
    
        private LocalDate calcNext(LocalDate fromDate, DayOfWeek supplyDay) {
            LocalDate d = fromDate.plusDays(1);
            while (d.getDayOfWeek() != supplyDay) {
                d = d.plusDays(1);
            }
            return d;
        }

        public void moveToNext(LocalDate currentDate){
            this.reservationDate = currentDate;
            this.nextReservationDate = calcNext(this.nextReservationDate,this.supplyDay);
        }
    
        // --- Getters ---
        public String getTemplateId() { return templateId; }
        public Supplier getSupplier() { return supplier; }
        public List<ProductRes> getTemplateProducts() { return templateProducts; }
        public SupplierAgreement getTemplateAgreement() { return templateAgreement; }
        public LocalDate getReservationDate() { return reservationDate; }
        public LocalDate getNextReservationDate() { return nextReservationDate; }
        public DayOfWeek getSupplyDay() { return supplyDay; }
    
        public void setReservationDate(LocalDate resDate){
            this.reservationDate = resDate;
        }

        public void setNextReservationDate(LocalDate nextResDate){
            this.nextReservationDate = nextResDate;
        }
        // --- Utility ---
        public boolean isDue(LocalDate currentDate) {
            return !currentDate.isBefore(nextReservationDate);
        }
    
        @Override
        public String toString() {
            return "WeeklyTemplate{" +
                    "templateId=" + templateId +
                    ", templateProducts=" + templateProducts +
                    ", supplyDay=" + supplyDay +
                    ", nextReservationDate=" + nextReservationDate +
                    '}';
        }

}
