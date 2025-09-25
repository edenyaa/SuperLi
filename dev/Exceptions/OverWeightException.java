package Exceptions;

public class OverWeightException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int truckId;
    private int deliveryId;
    private int weight;

    public OverWeightException(int truckId, int deliveryId, int weight) {
        super("Truck " + truckId + " cannot carry delivery " + deliveryId + " due to excess weight: " + weight);
        this.truckId = truckId;
        this.deliveryId = deliveryId;
        this.weight = weight;
    }

    public int getTruckId() {
        return truckId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public double getWeight() {
        return weight;
    }
    
}
