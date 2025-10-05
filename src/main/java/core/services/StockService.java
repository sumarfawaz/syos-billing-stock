package core.services;

import core.models.Item;
import core.models.StockEntry;
import core.models.Shelf;
import core.repositories.StockEntryRepository;
import core.dao.StockEntryDAO;
import core.strategy.stock.StockAllocator;
import core.strategy.stock.ExpiryAwareStockSelectionStrategy;
import core.observer.StockObserver;
import core.observer.StockSubject;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class StockService implements StockSubject {

    private final StockEntryRepository stockEntryRepository;
    private final ItemService itemService;
    private final ShelfService shelfService;

    private final List<StockObserver> observers = new ArrayList<>();
    private final Map<String, Integer> stockLevels = new HashMap<>();

    // ✅ Per-item lock map
    private static final ConcurrentHashMap<String, ReentrantLock> STOCK_LOCKS = new ConcurrentHashMap<>();

    public StockService(Connection conn, ItemService itemService, ShelfService shelfService) {
        this.stockEntryRepository = new StockEntryDAO(conn);
        this.itemService = itemService;
        this.shelfService = shelfService;
    }

    // ✅ Expose ShelfService (unchanged)
    public ShelfService getShelfService() {
        return shelfService;
    }

    // ==================== OBSERVER METHODS (unchanged) ====================

    @Override
    public void registerObserver(StockObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String itemCode, int newQuantity) {
        for (StockObserver observer : observers) {
            observer.update(itemCode, newQuantity);
        }
    }

    // ==================== READ METHODS (no locking) ====================

    public List<StockEntry> getAllStockEntries() throws SQLException {
        return stockEntryRepository.findAll();
    }

    public StockEntry getStockEntryByItemCode(String itemCode) throws SQLException {
        return ((StockEntryDAO) stockEntryRepository).getStockEntryByItemCode(itemCode);
    }

    public int getTotalStockForItem(String itemCode) throws SQLException {
        List<StockEntry> entries = stockEntryRepository.findAvailableByItemCode(itemCode);
        return entries.stream().mapToInt(StockEntry::getQuantity).sum();
    }

    public boolean itemExists(String itemCode) {
        try {
            return itemService.getItemByCode(itemCode) != null;
        } catch (SQLException e) {
            return false;
        }
    }

    // ==================== WRITE METHODS (per-item locking) ====================

    public void addStockEntry(String itemCode, int quantity, String entryDateStr, String expiryDateStr)
            throws SQLException, ParseException {

        // Acquire lock per item
        ReentrantLock lock = STOCK_LOCKS.computeIfAbsent(itemCode, k -> new ReentrantLock());
        lock.lock();

        try {
            Item item = itemService.getItemByCode(itemCode);
            if (item == null) {
                throw new IllegalArgumentException("Item with code '" + itemCode + "' does not exist.");
            }

            Date entryDate = parseDate(entryDateStr);
            Date expiryDate = parseDate(expiryDateStr);

            StockEntry entry = new StockEntry(itemCode, quantity, entryDate, expiryDate);
            stockEntryRepository.insert(entry);

            // Update and notify
            int newQuantity = getTotalStockForItem(itemCode);
            notifyObservers(itemCode, newQuantity);

            System.out.println("✅ Stock entry added safely for item: " + itemCode);
        } finally {
            lock.unlock();
        }
    }

    public List<StockEntry> allocateStock(String itemCode, int quantity) throws SQLException {
        ReentrantLock lock = STOCK_LOCKS.computeIfAbsent(itemCode, k -> new ReentrantLock());
        lock.lock();

        try {
            List<StockEntry> available = stockEntryRepository.findAvailableByItemCode(itemCode);
            StockAllocator allocator = new StockAllocator(new ExpiryAwareStockSelectionStrategy());
            List<StockEntry> allocated = allocator.allocate(available, quantity);

            for (StockEntry entry : allocated) {
                stockEntryRepository.reduceQuantity(entry, entry.getQuantity());
            }

            int newQuantity = getTotalStockForItem(itemCode);
            notifyObservers(itemCode, newQuantity);

            System.out.println("✅ Stock allocated safely for item: " + itemCode);
            return allocated;
        } finally {
            lock.unlock();
        }
    }

    public void updateStockEntry(StockEntry entry) throws SQLException {
        ReentrantLock lock = STOCK_LOCKS.computeIfAbsent(entry.getItemCode(), k -> new ReentrantLock());
        lock.lock();

        try {
            stockEntryRepository.update(entry);
            int newQuantity = getTotalStockForItem(entry.getItemCode());
            notifyObservers(entry.getItemCode(), newQuantity);

            System.out.println("✅ Stock updated safely for item: " + entry.getItemCode());
        } finally {
            lock.unlock();
        }
    }

    public void deleteStockEntry(StockEntry entry) throws SQLException {
        ReentrantLock lock = STOCK_LOCKS.computeIfAbsent(entry.getItemCode(), k -> new ReentrantLock());
        lock.lock();

        try {
            stockEntryRepository.delete(entry);
            int newQuantity = getTotalStockForItem(entry.getItemCode());
            notifyObservers(entry.getItemCode(), newQuantity);

            System.out.println("✅ Stock entry deleted safely for item: " + entry.getItemCode());
        } finally {
            lock.unlock();
            // Optional cleanup
            STOCK_LOCKS.remove(entry.getItemCode(), lock);
        }
    }

    // ==================== UTILITY ====================

    private Date parseDate(String input) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(input);
    }
}
