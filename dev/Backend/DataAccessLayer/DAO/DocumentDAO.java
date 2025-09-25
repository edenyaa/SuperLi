package Backend.DataAccessLayer.DAO;

import java.util.LinkedList;
import java.util.AbstractMap.SimpleEntry;

import Backend.DTO.*;

public interface DocumentDAO extends DAO<DocumentDTO, Integer> {

    LinkedList<SimpleEntry<String, Integer>> getDocumentProduct(int documentID);

    void insertDocumentProduct(int documentID, String productName, int weight);

    LocationDTO getLoc(int locID);

    LinkedList<DocumentDTO> getTruckDocuments(int truckID);
}
