package domainLayerSuppliers;

public class PaymentCondition {
    private PaymentType paymentType;
    private String bankAccount;

    public enum PaymentType {
        TRANSFER, CASH, CHECK
    }

    public PaymentCondition(PaymentType paymentType, String bankAccount) {
        this.paymentType = paymentType;
        this.bankAccount = bankAccount;
    }

    public PaymentType getPaymentType() { return paymentType; }
    public String getBankAccount() { return bankAccount; }

}
