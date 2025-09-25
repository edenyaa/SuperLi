package Backend.DataAccessLayer.Controllers;

import Backend.DTO.AreaDTO;
import Backend.DTO.LocationDTO;
import Backend.DataAccessLayer.DAO.AreaLocationDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.util.LinkedList;

public class AreaLocationDAOImpl implements AreaLocationDAO {
    
    private static final AreaLocationDAOImpl instance = new AreaLocationDAOImpl();
    private Connection connection;
    private final String areaTableName = "Area";
    private final String locationTableName = "Location";

    private AreaLocationDAOImpl() {
        try {
            this.connection = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static AreaLocationDAOImpl getInstance() {
        return instance;
    }
    
    @Override
    public LinkedList<AreaDTO> getAll() {
        String query = "SELECT * FROM " + areaTableName;
        LinkedList<AreaDTO> areas = new LinkedList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String areaName = resultSet.getString("areaName");
                LinkedList<LocationDTO> locations = getAllLocationsByArea(areaName);
                AreaDTO area = new AreaDTO(areaName, locations);
                areas.add(area);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areas;
    }

    @Override
    public LinkedList<LocationDTO> getAllLocationsByArea(String areaName) {
        String query = "SELECT * FROM " + locationTableName + " WHERE areaName = ?";
        LinkedList<LocationDTO> locations = new LinkedList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, areaName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int locID = resultSet.getInt("id");
                String address = resultSet.getString("address");
                String pnum = resultSet.getString("pnum");
                String cname = resultSet.getString("cname");
                LocationDTO location = new LocationDTO(locID, areaName, address, pnum, cname);
                locations.add(location);
            }
                
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    @Override
    public void insertLocationToArea(String areaName, LocationDTO location) {
        String query = "INSERT INTO " + locationTableName + " (id, areaName, address, pnum, cname) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, location.getId());
            statement.setString(2, areaName);
            statement.setString(3, location.getAddress());
            statement.setString(4, location.getPhoneNumber());
            statement.setString(5, location.getContactName());
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    location.setId(generatedId);
                }
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteLocationFromArea(String areaName, LocationDTO location) {
        String query = "DELETE FROM " + locationTableName + " WHERE id = ? AND areaName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, location.getId());
            statement.setString(2, areaName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateLocationInArea(String areaName, LocationDTO location) {
        String query = "UPDATE " + locationTableName + " SET address = ?, pnum = ?, cname = ? WHERE id = ? AND areaName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, location.getAddress());
            statement.setString(2, location.getPhoneNumber());
            statement.setString(3, location.getContactName());
            statement.setInt(4, location.getId());
            statement.setString(5, areaName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(AreaDTO area) {
        String query = "INSERT INTO " + areaTableName + " (areaName) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, area.getName());
            statement.executeUpdate();
            for (LocationDTO location : area.getLocations()) {
                insertLocationToArea(area.getName(), location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AreaDTO getBy(String areaName) {
        String query = "SELECT * FROM " + areaTableName + " WHERE areaName = ?";
        AreaDTO area = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, areaName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LinkedList<LocationDTO> locations = getAllLocationsByArea(areaName);
                area = new AreaDTO(areaName, locations);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return area;
    }

    @Override
    public void update(AreaDTO area) {
        String query = "UPDATE " + areaTableName + " SET areaName = ? WHERE areaName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, area.getName());
            statement.setString(2, area.getName());
            statement.executeUpdate();
            for (LocationDTO location : area.getLocations()) {
                updateLocationInArea(area.getName(), location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(AreaDTO area) {
        String query = "DELETE FROM " + areaTableName + " WHERE areaName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, area.getName());
            statement.executeUpdate();
            for (LocationDTO location : area.getLocations()) {
                deleteLocationFromArea(area.getName(), location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM " + areaTableName + "; DELETE FROM " + locationTableName;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM " + locationTableName;
        int count = 0;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    @Override
    public LocationDTO getLocationById(int id) {
        String query = "SELECT * FROM " + locationTableName + " WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String areaName    = rs.getString("areaName");
                    String address     = rs.getString("address");
                    String phoneNumber = rs.getString("pnum");
                    String contactName = rs.getString("cname");
                    return new LocationDTO(id, areaName, address, phoneNumber, contactName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getMaxID() {
        String query = "SELECT MAX(id) FROM " + locationTableName;
        int maxId = -1;
        try (Statement statement = connection.createStatement();
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
