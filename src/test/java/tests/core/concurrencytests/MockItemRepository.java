package tests.core.concurrencytests;

import core.models.Item;
import core.repositories.ItemRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MockItemRepository implements ItemRepository {
    private final Map<String, Item> items = new ConcurrentHashMap<>();

    @Override
    public void addItem(Item item) throws SQLException {
        items.put(item.getCode(), item);
    }

    @Override
    public Item getItemByCode(String code) throws SQLException {
        return items.get(code);
    }

    @Override
    public List<Item> getAllItems() throws SQLException {
        return new ArrayList<>(items.values());
    }

    @Override
    public void updateItem(Item item) throws SQLException {
        items.put(item.getCode(), item);
    }

    @Override
    public void deleteItem(String code) throws SQLException {
        items.remove(code);
    }
}
