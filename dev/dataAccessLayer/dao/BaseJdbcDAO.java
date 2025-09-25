package dataAccessLayer.dao;

import java.sql.Connection;
import java.sql.SQLException;

abstract class BaseJdbcDAO {
    protected Connection conn() throws SQLException {
        return util.DatabaseManager.getConnection();
    }
}