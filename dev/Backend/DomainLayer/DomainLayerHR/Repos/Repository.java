package Backend.DomainLayer.DomainLayerHR.Repos;

import java.sql.SQLException;
import java.util.LinkedList;

public interface Repository <T, ID> {

    LinkedList<T> selectAll() throws SQLException;

    void deleteAll() throws SQLException;

    void insert(T item) throws SQLException;

    void update(T item) throws SQLException;

    void delete(T item) throws SQLException;

    T select(ID id) throws SQLException;
}
