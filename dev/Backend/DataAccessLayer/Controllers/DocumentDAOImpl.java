package Backend.DataAccessLayer.Controllers;

import Backend.DTO.DocumentDTO;
import Backend.DTO.LocationDTO;
import Backend.DataAccessLayer.DAO.DocumentDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

public class DocumentDAOImpl implements DocumentDAO {

    private static final DocumentDAOImpl instance = new DocumentDAOImpl();
//    private Connection connection;
    private final String tableName = "Document";
    private final String productTableName = "Delivery_Product";
    private final String locationTableName = "Location";

    private DocumentDAOImpl() {
//        try {
//            this.connection = DBUtil.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public static DocumentDAOImpl getInstance() {
        return instance;
    }

    @Override
    public LinkedList<SimpleEntry<String, Integer>> getDocumentProduct(int documentID) {
        String query = "SELECT productName, weight FROM " + productTableName + " WHERE deliveryID = ?";
        LinkedList<SimpleEntry<String, Integer>> products = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, documentID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String productName = resultSet.getString("productName");
                int weight = resultSet.getInt("weight");
                products.add(new SimpleEntry<>(productName, weight));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void insertDocumentProduct(int documentID, String productName, int weight) {
        String query = "INSERT INTO " + productTableName + " (deliveryID, productName, weight) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, documentID);
            statement.setString(2, productName);
            statement.setInt(3, weight);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LocationDTO getLoc(int locID) {
        String query = "SELECT * FROM " + locationTableName + " WHERE id = ?";
        LocationDTO location = null;
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, locID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String areaName = resultSet.getString("areaName");
                String address = resultSet.getString("address");
                String pnum = resultSet.getString("pnum");
                String cname = resultSet.getString("cname");
                location = new LocationDTO(locID, areaName, address, pnum, cname);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

    public LinkedList<DocumentDTO> getTruckDocuments(int truckID) {
        String query = "SELECT * FROM " + tableName + " WHERE truckID = ?";
        LinkedList<DocumentDTO> documents = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, truckID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int originID = resultSet.getInt("originID");
                LocationDTO origin = getLoc(originID);
                int destinationID = resultSet.getInt("destinationID");
                LocationDTO destination = getLoc(destinationID);
                String createdDate = resultSet.getString("creationDate");
                LocalDate cdate = (createdDate != null) ? LocalDate.parse(createdDate) : null;
                String exitedDate = resultSet.getString("exitedDate");
                Date edate = (exitedDate != null) ? Date.valueOf(exitedDate) : null;
                String driverName = resultSet.getString("driverName");
                int weight = resultSet.getInt("weight");
                LinkedList<SimpleEntry<String, Integer>> products = getDocumentProduct(id);
                DocumentDTO document = new DocumentDTO(id, products, origin, destination, cdate, truckID, driverName, weight, edate);
                documents.add(document);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    @Override
    public DocumentDTO getBy(Integer id) {
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        DocumentDTO document = null;
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int truckID = resultSet.getInt("truckID");
                int originID = resultSet.getInt("originID");
                LocationDTO origin = getLoc(originID);
                int destinationID = resultSet.getInt("destinationID");
                LocationDTO destination = getLoc(destinationID);
                String createdDate = resultSet.getString("creationDate");
                LocalDate cdate = (createdDate != null) ? LocalDate.parse(createdDate) : null;
                String exitedDate = resultSet.getString("exitedDate");
                Date edate = (exitedDate != null) ? Date.valueOf(exitedDate) : null;
                String driverName = resultSet.getString("driverName");
                int weight = resultSet.getInt("weight");
                LinkedList<SimpleEntry<String, Integer>> products = getDocumentProduct(id);
                document = new DocumentDTO(id, products, origin, destination, cdate, truckID, driverName, weight, edate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return document;
    }


    @Override
    public void insert(DocumentDTO document) {
        String query = "INSERT INTO " + tableName + " (id, truckID, originID, destinationID, creationDate, exitedDate, driverName, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, document.getId());
            statement.setInt(2, document.getTruckID());
            statement.setInt(3, document.getOrigin().getId());
            statement.setInt(4, document.getDestination().getId());
            statement.setString(5, document.getCreatedDate() != null ? document.getCreatedDate().toString() : null);
            statement.setString(6, document.getExitedTime() != null ? document.getExitedTime().toString() : null);
            statement.setString(7, document.getDriverName());
            statement.setInt(8, document.getWeight());
            statement.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(DocumentDTO document) {
        String query = "UPDATE " + tableName + " SET truckID = ?, originID = ?, destinationID = ?, creationDate = ?, exitedDate = ?, driverName = ?, weight = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, document.getTruckID());
            statement.setInt(2, document.getOrigin().getId());
            statement.setInt(3, document.getDestination().getId());
            statement.setString(4, document.getCreatedDate() != null ? document.getCreatedDate().toString() : null);
            statement.setString(5, document.getExitedTime() != null ? document.getExitedTime().toString() : null);
            statement.setString(6, document.getDriverName());
            statement.setInt(7, document.getWeight());
            statement.setInt(8, document.getId());
            statement.executeUpdate();
            // Clear existing products and reinsert
            String deleteQuery = "DELETE FROM " + productTableName + " WHERE deliveryID = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, document.getId());
                deleteStatement.executeUpdate();
            }
            for (SimpleEntry<String, Integer> product : document.getListOfItems()) {
                insertDocumentProduct(document.getId(), product.getKey(), product.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DocumentDTO document) {
        String query = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, document.getId());
            statement.executeUpdate();
            // Also delete associated products
            String deleteProductQuery = "DELETE FROM " + productTableName + " WHERE deliveryID = ?";
            try (PreparedStatement deleteProductStatement = connection.prepareStatement(deleteProductQuery)) {
                deleteProductStatement.setInt(1, document.getId());
                deleteProductStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public LinkedList<DocumentDTO> getAll() {
        String query = "SELECT * FROM " + tableName;
        LinkedList<DocumentDTO> documents = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int truckID = resultSet.getInt("truckID");
                int originID = resultSet.getInt("originID");
                LocationDTO origin = getLoc(originID);
                int destinationID = resultSet.getInt("destinationID");
                LocationDTO destination = getLoc(destinationID);
                String createdDate = resultSet.getString("creationDate");
                LocalDate cdate = (createdDate != null) ? LocalDate.parse(createdDate) : null;
                String exitedDate = resultSet.getString("exitedDate");
                Date edate = (exitedDate != null) ? Date.valueOf(exitedDate) : null;
                String driverName = resultSet.getString("driverName");
                int weight = resultSet.getInt("weight");
                LinkedList<SimpleEntry<String, Integer>> products = getDocumentProduct(id);
                DocumentDTO document = new DocumentDTO(id, products, origin, destination, cdate, truckID, driverName, weight, edate);
                documents.add(document);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return documents;
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM " + tableName;
        try (Connection connection = DBUtil.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            // Also delete all associated products
            String deleteProductQuery = "DELETE FROM " + productTableName;
            try (Statement deleteProductStatement = connection.createStatement()) {
                deleteProductStatement.executeUpdate(deleteProductQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int count(){
        String query = "SELECT COUNT(*) FROM " + tableName;
        int count = 0;
        try (Connection connection = DBUtil.getConnection();
                Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getMaxID() {
        String query = "SELECT MAX(id) FROM " + tableName;
        int maxId = -1;
        try (Connection connection = DBUtil.getConnection();
                Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                maxId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId;
    }
}
