package Backend.DataAccessLayer.DAO;

import Backend.DTO.*;

import java.util.LinkedList;
import java.util.AbstractMap.SimpleEntry;

public interface DeliveryDAO extends DAO<DeliveryDTO, Integer> {

    LinkedList<SimpleEntry<String, Integer>> getDeliveryProducts(int deliveryID);

    void insertDeliveryProduct(int deliveryID, String productName, int weight);

    LocationDTO getLoc(int locID);
}
