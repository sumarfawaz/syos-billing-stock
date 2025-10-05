package core.services;

import core.models.Item;
import core.models.Shelf;
import core.repositories.ItemRepository;
import core.dao.ShelfDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ItemService {
    private final ItemRepository itemRepo;
    private final ShelfService shelfService;

    // Global map of locks — one per item code
    private static final ConcurrentHashMap<String, ReentrantLock> ITEM_LOCKS = new ConcurrentHashMap<>();

    // Constructor now accepts ShelfService
    public ItemService(ItemRepository itemRepo, ShelfService shelfService) {
        this.itemRepo = itemRepo;
        this.shelfService = shelfService;
    }

    //  Read operations (no locking needed)
    public Item getItemByCode(String code) throws SQLException {
        return itemRepo.getItemByCode(code);
    }

    public List<Item> getAllItems() throws SQLException {
        return itemRepo.getAllItems();
    }

    //  Write operations — per-item locking

    public void addItem(Item item, int shelfDefault, int shelfCurrent) throws SQLException {
        ReentrantLock lock = ITEM_LOCKS.computeIfAbsent(item.getCode(), k -> new ReentrantLock());
        lock.lock();
        try {
            itemRepo.addItem(item);
            // Optionally initialize shelf here
            // shelfService.addShelf(new Shelf(item.getCode(), shelfDefault, shelfCurrent));
            System.out.println("✅ Item added safely: " + item.getName());
        } finally {
            lock.unlock();
        }
    }

    public void updateItem(Item item) throws SQLException {
        ReentrantLock lock = ITEM_LOCKS.computeIfAbsent(item.getCode(), k -> new ReentrantLock());
        lock.lock();
        try {
            itemRepo.updateItem(item);
            System.out.println("✅ Item updated safely: " + item.getCode());
        } finally {
            lock.unlock();
        }
    }

    public void updateItemName(String code, String newName) throws SQLException {
        ReentrantLock lock = ITEM_LOCKS.computeIfAbsent(code, k -> new ReentrantLock());
        lock.lock();
        try {
            Item item = itemRepo.getItemByCode(code);
            if (item != null) {
                Item updated = new Item(item.getCode(), newName, item.getPrice());
                itemRepo.updateItem(updated);
                System.out.println("✅ Item name updated safely: " + code);
            } else {
                System.out.println("⚠️ Item not found: " + code);
            }
        } finally {
            lock.unlock();
        }
    }

    public void updateItemPrice(String code, double newPrice) throws SQLException {
        ReentrantLock lock = ITEM_LOCKS.computeIfAbsent(code, k -> new ReentrantLock());
        lock.lock();
        try {
            Item item = itemRepo.getItemByCode(code);
            if (item != null) {
                Item updated = new Item(item.getCode(), item.getName(), newPrice);
                itemRepo.updateItem(updated);
                System.out.println("✅ Item price updated safely: " + code);
            } else {
                System.out.println("⚠️ Item not found: " + code);
            }
        } finally {
            lock.unlock();
        }
    }

    public void deleteItem(String code) throws SQLException {
        ReentrantLock lock = ITEM_LOCKS.computeIfAbsent(code, k -> new ReentrantLock());
        lock.lock();
        try {
            itemRepo.deleteItem(code);
            shelfService.deleteShelf(code);
            System.out.println("✅ Item deleted safely: " + code);
        } finally {
            lock.unlock();
            // Optional cleanup — remove unused lock
            ITEM_LOCKS.remove(code, lock);
        }
    }
}
