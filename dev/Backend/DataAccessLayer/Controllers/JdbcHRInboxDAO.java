package Backend.DataAccessLayer.Controllers;

import Backend.DTO.InboxMessageDTO;
import Backend.DataAccessLayer.DAO.HRInboxDAO;
import Backend.DataAccessLayer.DBUtil;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JdbcHRInboxDAO implements HRInboxDAO {

    @Override
    public InboxMessageDTO getBy(Integer id) {
        String sql = "SELECT * FROM HR_Messages WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String sender = rs.getString("sender");
                    String content = rs.getString("message");
                    boolean isRead = rs.getInt("isRead") == 1;
                    return new InboxMessageDTO(id, sender, content, isRead);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve message with ID: " + id, e);
        }
        return null;
    }

    @Override
    public void deleteAll() {
        String deleteHrMessages = "DELETE FROM HR_Messages;";
        String deleteInboxMessages = "DELETE FROM Inbox_Message;";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(deleteHrMessages);
            stmt.executeUpdate(deleteInboxMessages);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete all messages", e);
        }
    }
    public int getIdAndInsert(InboxMessageDTO message) {
        String sql = "INSERT INTO HR_Messages (sender, message, isRead) VALUES (?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getMessage());
            stmt.setInt(3, message.isRead() ? 1 : 0);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("No generated ID returned");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert message", e);
        }
    }


    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM HR_Messages";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to count messages", e);
        }
        return 0;
    }

    @Override
    public void insert(InboxMessageDTO message) {
        String sql = "INSERT INTO HR_Messages (sender, message, isRead) VALUES (?, ?, ?, ?)";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getMessage());
            stmt.setInt(3, message.isRead() ? 1 : 0);
            stmt.setInt(4, message.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert message", e);
        }
    }

    @Override
    public LinkedList<InboxMessageDTO> getAll() {
        return getMessages("SELECT * FROM HR_Messages");
    }


    public List<InboxMessageDTO> getUnread() {
        return getMessages("SELECT * FROM HR_Messages WHERE isRead = 0");
    }

    @Override
    public void update(InboxMessageDTO message) {
        String sql = "UPDATE HR_Messages SET sender = ?, message = ?, isRead = ? WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getMessage());
            stmt.setInt(3, message.isRead() ? 1 : 0);
            stmt.setInt(4, message.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update message with ID: " + message.getId(), e);
        }
    }
    @Override
    public void markAsRead(int messageId) {
        String sql = "UPDATE HR_Messages SET isRead = 1 WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(InboxMessageDTO messageDTO) {
        int messageId = messageDTO.getId();
        String sql = "DELETE FROM HR_Messages WHERE id = ?";
        try (   Connection connection = DBUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete message with ID: " + messageId, e);
        }
    }

    @Override
    public void clearAll() {
        String sql = "DELETE FROM HR_Messages";
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private LinkedList<InboxMessageDTO> getMessages(String query) {
        LinkedList<InboxMessageDTO> messages = new LinkedList<>();
        try (   Connection connection = DBUtil.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String sender = rs.getString("sender");
                String content = rs.getString("message");
                boolean isRead = rs.getInt("isRead") == 1;
                messages.add(new InboxMessageDTO(id,sender, content, isRead));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve messages", e);
        }
        return messages;
    }
}
