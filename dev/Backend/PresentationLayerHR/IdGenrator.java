package Backend.PresentationLayerHR;

public class IdGenrator {
    
    private static int truckID = 0;
    private static int deliveryID = 0;
    private static int locationID = 0;

    public static int generateTruckID() {
        return truckID++;
    }


    public static int generateDeliveryID() {
        return deliveryID++;
    }

    public static int generateLocationID() {
        return locationID++;
    }

    public static void decrementTruckID() {
        if (truckID > 0) {
            truckID--;
        }
    }

    public static void decrementDeliveryID() {
        if (deliveryID > 0) {
            deliveryID--;
        }
    }

    public static void decrementLocationID() {
        if (locationID > 0) {
            locationID--;
        }
    }

    public static void resetIDs() {
        truckID = 0;
        deliveryID = 0;
        locationID = 0;
    }

    public static void setTruckID(int maxID) {
        truckID = maxID + 1;
    }

    public static void setDeliveryID(int maxID) {
        deliveryID = maxID + 1;
    }

    public static void setLocationID(int maxID) {
        locationID = maxID + 1;
    }
}
