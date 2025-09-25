package Backend.DataAccessLayer.Controllers;

import Backend.DTO.TruckDTO;
import Backend.DataAccessLayer.DAO.TruckDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

public class TruckDAOImpl implements TruckDAO {

    private static final TruckDAOImpl instance = new TruckDAOImpl();
//    private Connection connection;
    private final String tableName = "Truck";
    private final String productTableName = "Truck_Product";

    private TruckDAOImpl() {
//        try {
//            this.connection = DBUtil.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public static TruckDAOImpl getInstance() {
        return instance;
    }

    @Override
    public LinkedList<SimpleEntry<String, Integer>> getAllTruckProduct(int truckID) {
        String query = "SELECT productName, weight FROM " + productTableName + " WHERE truckID = ?";
        LinkedList<SimpleEntry<String, Integer>> products = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, truckID);
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
    public void insertTruckProduct(int truckID, String productName, int weight) {
        String query = "INSERT INTO " + productTableName + " (truckID, productName, weight) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, truckID);
            statement.setString(2, productName);
            statement.setInt(3, weight);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTruckProducts(int truckID, LinkedList<SimpleEntry<String, Integer>> products) {
        String query = "DELETE FROM " + productTableName + " WHERE truckID = ? AND productName = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (SimpleEntry<String, Integer> product : products) {
                statement.setInt(1, truckID);
                statement.setString(2, product.getKey());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TruckDTO getBy(Integer id) {
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        TruckDTO truck = null;
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String model = resultSet.getString("model");
                int dryWeight = resultSet.getInt("dryWeight");
                int maxLoad = resultSet.getInt("maxLoad");
                int weight = resultSet.getInt("weight");
                boolean isAvailable = resultSet.getInt("available") == 1;
                String license = resultSet.getString("license");
                LinkedList<SimpleEntry<String, Integer>> products = getAllTruckProduct(id);
                truck = new TruckDTO(id, model, dryWeight, weight, maxLoad, isAvailable, license, products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return truck;
    }

    @Override
    public void insert(TruckDTO truck) {
        String query = "INSERT INTO " + tableName + " (id, model, dryWeight, weight, maxLoad, available, license) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, truck.getId());
            statement.setString(2, truck.getModel());
            statement.setInt(3, truck.getDryWeight());
            statement.setInt(4, truck.getWeight());
            statement.setInt(5, truck.getMaxLoad());
            statement.setInt(6, truck.getAvailable() ? 1 : 0);
            statement.setString(7, truck.getLicense());
            statement.executeUpdate();
            for (SimpleEntry<String, Integer> product : truck.getItems()) {
                insertTruckProduct(truck.getId(), product.getKey(), product.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TruckDTO truck) {
        String query = "UPDATE " + tableName + " SET model = ?, dryWeight = ?, weight = ?, maxLoad = ?, available = ?, license = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, truck.getModel());
            statement.setInt(2, truck.getDryWeight());
            statement.setInt(3, truck.getWeight());
            statement.setInt(4, truck.getMaxLoad());
            statement.setInt(5, truck.getAvailable() ? 1 : 0);
            statement.setString(6, truck.getLicense());
            statement.setInt(7, truck.getId());
            statement.executeUpdate();
            // Clear existing products and reinsert
            String deleteQuery = "DELETE FROM " + productTableName + " WHERE truckID = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, truck.getId());
                deleteStatement.executeUpdate();
            }
            for (SimpleEntry<String, Integer> product : truck.getItems()) {
                insertTruckProduct(truck.getId(), product.getKey(), product.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(TruckDTO truck) {
        String query = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, truck.getId());
            statement.executeUpdate();
            // Also delete associated products
            String deleteProductQuery = "DELETE FROM " + productTableName + " WHERE truckID = ?";
            try (PreparedStatement deleteProductStatement = connection.prepareStatement(deleteProductQuery)) {
                deleteProductStatement.setInt(1, truck.getId());
                deleteProductStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LinkedList<TruckDTO> getAll() {
        String query = "SELECT * FROM " + tableName;
        LinkedList<TruckDTO> trucks = new LinkedList<>();
        try (Connection connection = DBUtil.getConnection();
                Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String model = resultSet.getString("model");
                int dryWeight = resultSet.getInt("dryWeight");
                int weight = resultSet.getInt("weight");
                int maxLoad = resultSet.getInt("maxLoad");
                boolean isAvailable = resultSet.getInt("available") == 1;
                String license = resultSet.getString("license");
                LinkedList<SimpleEntry<String, Integer>> products = getAllTruckProduct(id);
                trucks.add(new TruckDTO(id, model, dryWeight, weight, maxLoad, isAvailable, license, products));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trucks;
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
    public int count() {
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

    public int getMaxID(){
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
