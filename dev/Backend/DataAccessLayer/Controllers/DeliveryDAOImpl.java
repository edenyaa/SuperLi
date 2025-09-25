package Backend.DataAccessLayer.Controllers;

import Backend.DTO.DeliveryDTO;
import Backend.DTO.LocationDTO;
import Backend.DataAccessLayer.DAO.DeliveryDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

public class DeliveryDAOImpl implements DeliveryDAO {
    
    private static final DeliveryDAOImpl instance = new DeliveryDAOImpl();
//    private Connection connection;
    private final String deliveryTableName = "Delivery";
    private final String productTableName = "Delivery_Product";
    private final String locationTableName = "Location";

    private DeliveryDAOImpl() {
//        try {
//            this.connection = DBUtil.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public static DeliveryDAOImpl getInstance() {
        return instance;
    }

    @Override
    public LocationDTO getLoc(int locID){
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

    @Override
    public LinkedList<SimpleEntry<String, Integer>> getDeliveryProducts(int deliveryID) {
        String query = "SELECT productName, weight FROM " + productTableName + " WHERE deliveryID = ?";
        LinkedList<SimpleEntry<String, Integer>> products = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, deliveryID);
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
    public DeliveryDTO getBy(Integer id) {
        String query = "SELECT * FROM " + deliveryTableName + " WHERE id = ?";
        DeliveryDTO delivery = null;
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int originID = resultSet.getInt("originID");
                int destID = resultSet.getInt("destinationID");
                String createdDate = resultSet.getString("creationDate");
                LocalDate cdate = (createdDate != null) ? LocalDate.parse(createdDate) : null;
                String deliveryDate = resultSet.getString("deliveryDate");
                LocalDate ddate = (deliveryDate != null) ? LocalDate.parse(deliveryDate) : null;
                LocationDTO origin = getLoc(originID);
                LocationDTO destination = getLoc(destID);
                LinkedList<SimpleEntry<String, Integer>> products = getDeliveryProducts(id);
                delivery = new DeliveryDTO(id, cdate, ddate, origin, destination, products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delivery;
    }

    @Override
    public LinkedList<DeliveryDTO> getAll() {
        String query = "SELECT * FROM " + deliveryTableName;
        LinkedList<DeliveryDTO> deliveries = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
                Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int originID = resultSet.getInt("originID");
                int destID = resultSet.getInt("destinationID");
                String createdDate = resultSet.getString("creationDate");
                LocalDate cdate = (createdDate != null) ? LocalDate.parse(createdDate) : null;
                String deliveryDate = resultSet.getString("deliveryDate");
                LocalDate ddate = (deliveryDate != null) ? LocalDate.parse(deliveryDate) : null;
                LocationDTO origin = getLoc(originID);
                LocationDTO destination = getLoc(destID);
                LinkedList<SimpleEntry<String, Integer>> products = getDeliveryProducts(id);
                deliveries.add(new DeliveryDTO(id, cdate, ddate, origin, destination, products));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    @Override
    public void insertDeliveryProduct(int deliveryID, String productName, int weight) {
        String query = "INSERT INTO " + productTableName + " (deliveryID, productName, weight) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, deliveryID);
            statement.setString(2, productName);
            statement.setInt(3, weight);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DeliveryDTO delivery) {
        String query = "INSERT INTO " + deliveryTableName + " (id, originID, destinationID, creationDate, deliveryDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, delivery.getId());
            statement.setInt(2, delivery.getOriginLoc().getId());
            statement.setInt(3, delivery.getDestinationLoc().getId());
            statement.setString(4, delivery.getCreatedDate() != null ? delivery.getCreatedDate().toString() : null);
            statement.setString(5, delivery.getDeliveryDate() != null ? delivery.getDeliveryDate().toString() : null);
            statement.executeUpdate();
            for (SimpleEntry<String, Integer> product : delivery.getListOfItems()) {
                insertDeliveryProduct(delivery.getId(), product.getKey(), product.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(DeliveryDTO delivery) {
        String query = "UPDATE " + deliveryTableName + " SET originID = ?, destinationID = ?, creationDate = ?, deliveryDate = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, delivery.getOriginLoc().getId());
            statement.setInt(2, delivery.getDestinationLoc().getId());
            statement.setString(3, delivery.getCreatedDate() != null ? delivery.getCreatedDate().toString() : null);
            statement.setString(4, delivery.getDeliveryDate() != null ? delivery.getDeliveryDate().toString() : null);
            statement.setInt(5, delivery.getId());
            statement.executeUpdate();
            String deleteQuery = "DELETE FROM " + productTableName + " WHERE deliveryID = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, delivery.getId());
                deleteStatement.executeUpdate();
            }
            for (SimpleEntry<String, Integer> product : delivery.getListOfItems()) {
                insertDeliveryProduct(delivery.getId(), product.getKey(), product.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DeliveryDTO delivery) {
        String query = "DELETE FROM " + deliveryTableName + " WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, delivery.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Check if the delivery has any associated documents
        // If not, delete the products associated with this delivery
        String checkingDoc = "SELECT COUNT(*) FROM Document WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(checkingDoc)) {
            checkStatement.setInt(1, delivery.getId());
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                String deleteProductsQuery = "DELETE FROM " + productTableName + " WHERE deliveryID = ?";
                try (PreparedStatement deleteProductsStatement = connection.prepareStatement(deleteProductsQuery)) {
                    deleteProductsStatement.setInt(1, delivery.getId());
                    deleteProductsStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll(){
        String query = "DELETE FROM " + deliveryTableName;
        try (Connection connection = DBUtil.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM " + deliveryTableName;
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
        String query = "SELECT MAX(id) FROM " + deliveryTableName;
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
