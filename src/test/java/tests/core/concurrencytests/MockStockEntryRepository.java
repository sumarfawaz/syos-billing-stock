package tests.core.concurrencytests;

import core.models.StockEntry;
import core.repositories.StockEntryRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MockStockEntryRepository implements StockEntryRepository {

    private final Map<String, List<StockEntry>> stockMap = new ConcurrentHashMap<>();

    @Override
    public void insert(StockEntry entry) throws SQLException {
        stockMap.computeIfAbsent(entry.getItemCode(), k -> new ArrayList<>()).add(entry);
        System.out.println("✅ Stock entry added safely for item: " + entry.getItemCode());
    }

    @Override
    public List<StockEntry> findAvailableByItemCode(String itemCode) throws SQLException {
        return new ArrayList<>(stockMap.getOrDefault(itemCode, new ArrayList<>()));
    }

    @Override
    public List<StockEntry> findAll() throws SQLException {
        List<StockEntry> all = new ArrayList<>();
        for (List<StockEntry> entries : stockMap.values()) {
            all.addAll(entries);
        }
        return all;
    }

    @Override
    public void reduceQuantity(StockEntry entry, int amount) throws SQLException {
        entry.setQuantity(entry.getQuantity() - amount);
        System.out.println("✅ Stock reduced safely for " + entry.getItemCode() + " by " + amount);
    }

    @Override
    public void update(StockEntry entry) throws SQLException {
        // Just replace old with new (mock)
        List<StockEntry> list = stockMap.get(entry.getItemCode());
        if (list != null) {
            list.removeIf(e -> e.equals(entry));
            list.add(entry);
        }
        System.out.println("✅ Stock entry updated safely for: " + entry.getItemCode());
    }

    @Override
    public void delete(StockEntry entry) throws SQLException {
        List<StockEntry> list = stockMap.get(entry.getItemCode());
        if (list != null) {
            list.remove(entry);
        }
        System.out.println("✅ Stock entry deleted safely for: " + entry.getItemCode());
    }
}
