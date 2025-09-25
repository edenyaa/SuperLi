package Backend.DataAccessLayer.DAO;

import java.util.LinkedList;

public interface DAO<T, ID> {

    LinkedList<T> getAll();

    void insert(T item);

    void update(T item);

    void delete(T item);

    void deleteAll();

    int count();

    T getBy(ID id);
}