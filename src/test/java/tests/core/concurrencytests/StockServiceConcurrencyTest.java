package tests.core.concurrencytests;

import core.models.Item;
import core.services.ItemService;
import core.services.ShelfService;
import core.services.StockService;
import core.dao.ShelfDAO;
import core.repositories.StockEntryRepository;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Random;

public class StockServiceConcurrencyTest {

    public static void main(String[] args) throws SQLException {
        System.out.println("🧪 Starting StockService concurrency test...");

        // Mock dependencies (no DB)
        MockItemRepository mockRepo = new MockItemRepository();
        ShelfService shelfService = new ShelfService(new ShelfDAO(null));
        ItemService itemService = new ItemService(mockRepo, shelfService);

        // Use mock repository instead of StockEntryDAO
        StockEntryRepository mockStockRepo = new MockStockEntryRepository();
        StockService stockService = new StockService(null, itemService, shelfService) {
            { // Inline override to use our mock repo
                try {
                    var field = StockService.class.getDeclaredField("stockEntryRepository");
                    field.setAccessible(true);
                    field.set(this, mockStockRepo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Create items
        itemService.addItem(new Item("A001", "Apples", 50.0), 10, 10);
        itemService.addItem(new Item("B001", "Bananas", 30.0), 10, 10);

        // Thread for item A (stock add)
        Runnable stockAdderA = () -> {
            try {
                for (int i = 0; i < 3; i++) {
                    stockService.addStockEntry("A001", 5, "2025-10-01", "2025-12-01");
                    Thread.sleep(new Random().nextInt(300));
                }
            } catch (SQLException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        };

        // Thread for item B (stock add)
        Runnable stockAdderB = () -> {
            try {
                for (int i = 0; i < 3; i++) {
                    stockService.addStockEntry("B001", 5, "2025-10-01", "2025-12-01");
                    Thread.sleep(new Random().nextInt(300));
                }
            } catch (SQLException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        };

        // Mixed thread — allocates stock from both
        Runnable stockAllocator = () -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String code = i % 2 == 0 ? "A001" : "B001";
                    stockService.allocateStock(code, 2);
                    Thread.sleep(new Random().nextInt(400));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(stockAdderA, "Thread-A");
        Thread t2 = new Thread(stockAdderB, "Thread-B");
        Thread t3 = new Thread(stockAllocator, "Thread-Alloc");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("✅ StockService concurrency test completed.");
    }
}
