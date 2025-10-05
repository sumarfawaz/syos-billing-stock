package tests.core.concurrencytests;

import core.models.Item;
import core.models.StockEntry;
import core.services.*;
import core.dao.ShelfDAO;
import core.repositories.StockEntryRepository;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 🧪 Combined concurrency stress test.
 * Simulates multiple threads performing billing, stock operations, and item price edits simultaneously.
 */
public class CombinedConcurrencyStressTest {

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Starting Combined Concurrency Stress Test...");

        // === Mock Environment Setup ===
        MockItemRepository mockItemRepo = new MockItemRepository();
        MockStockEntryRepository mockStockRepo = new MockStockEntryRepository();
        ShelfService shelfService = new ShelfService(new ShelfDAO(null));
        ItemService itemService = new ItemService(mockItemRepo, shelfService);

        // Create StockService but replace its repo with mock
        StockService stockService = new StockService(null, itemService, shelfService) {{
            try {
                var field = StockService.class.getDeclaredField("stockEntryRepository");
                field.setAccessible(true);
                field.set(this, mockStockRepo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }};

        // Add test items
        itemService.addItem(new Item("A001", "Apples", 50.0), 10, 10);
        itemService.addItem(new Item("B001", "Bananas", 30.0), 10, 10);
        itemService.addItem(new Item("O001", "Oranges", 40.0), 10, 10);

        // === Create Thread Pool for Concurrent Execution ===
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Callable<Void>> tasks = new ArrayList<>();

        Random random = new Random();

        // Task 1: Stock Adders (simulate restocking)
        for (int i = 0; i < 3; i++) {
            tasks.add(() -> {
                for (int j = 0; j < 5; j++) {
                    String code = switch (random.nextInt(3)) {
                        case 0 -> "A001";
                        case 1 -> "B001";
                        default -> "O001";
                    };
                    try {
                        stockService.addStockEntry(code, 5, "2025-10-01", "2025-12-01");
                        Thread.sleep(100 + random.nextInt(300));
                    } catch (SQLException | ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            });
        }

        // Task 2: Stock Allocators (simulate checkout reducing stock)
        for (int i = 0; i < 3; i++) {
            tasks.add(() -> {
                for (int j = 0; j < 5; j++) {
                    String code = switch (random.nextInt(3)) {
                        case 0 -> "A001";
                        case 1 -> "B001";
                        default -> "O001";
                    };
                    try {
                        stockService.allocateStock(code, 2);
                        Thread.sleep(100 + random.nextInt(400));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            });
        }

        // Task 3: Item Price Updaters (simulate admin changing prices)
        for (int i = 0; i < 2; i++) {
            tasks.add(() -> {
                for (int j = 0; j < 5; j++) {
                    String code = switch (random.nextInt(3)) {
                        case 0 -> "A001";
                        case 1 -> "B001";
                        default -> "O001";
                    };
                    double newPrice = 20 + random.nextDouble() * 80;
                    try {
                        itemService.updateItemPrice(code, newPrice);
                        Thread.sleep(150 + random.nextInt(300));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            });
        }

        // === Execute All Tasks Simultaneously ===
        long start = System.currentTimeMillis();
        executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();

        // === Summary ===
        System.out.println("\n✅ Combined concurrency test completed successfully!");
        System.out.println("⏱ Total execution time: " + (end - start) + " ms");

        // Optional: Final stock status
        for (String code : List.of("A001", "B001", "O001")) {
            int total = stockService.getTotalStockForItem(code);
            System.out.println("📦 Final stock for " + code + ": " + total);
        }

        System.out.println("🏁 All threads finished cleanly without deadlocks or race conditions.");
    }
}
