package Backend.DomainLayer.DomainLayerHR.Repos;

import java.sql.SQLException;

public interface DataRepository  {
    void loadData() throws SQLException;
    void deleteData() throws SQLException;
    void loadLoginData() throws SQLException;
}
