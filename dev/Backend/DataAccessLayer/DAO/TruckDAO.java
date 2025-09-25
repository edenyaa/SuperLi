package Backend.DataAccessLayer.DAO;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

import Backend.DTO.*;

public interface TruckDAO extends DAO<TruckDTO, Integer> {

    LinkedList<SimpleEntry<String, Integer>> getAllTruckProduct(int truckID);
    
    void insertTruckProduct(int truckID, String productName, int weight);

    void deleteTruckProducts(int truckID, LinkedList<SimpleEntry<String, Integer>> products);
}
