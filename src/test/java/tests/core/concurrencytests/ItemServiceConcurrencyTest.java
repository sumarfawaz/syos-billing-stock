package tests.core.concurrencytests;

import core.models.Item;
import core.services.ItemService;
import core.services.ShelfService;
import core.dao.ShelfDAO;

import java.sql.SQLException;

public class ItemServiceConcurrencyTest {

    public static void main(String[] args) throws SQLException {
        System.out.println("🧪 Starting ItemService concurrency test...");

        // Mock dependencies
        MockItemRepository mockRepo = new MockItemRepository();
        ShelfService shelfService = new ShelfService(new ShelfDAO(null));
        ItemService itemService = new ItemService(mockRepo, shelfService);

        // Add a test item
        Item item = new Item("ITM001", "Test Item", 100.0);
        itemService.addItem(item, 5, 5);

        // Define the concurrent task
        Runnable priceUpdater = () -> {
            try {
                for (int i = 0; i < 5; i++) {
                    double newPrice = 100 + (Math.random() * 50);
                    itemService.updateItemPrice("ITM001", newPrice);
                    System.out.println(Thread.currentThread().getName() +
                            " updated price to " + newPrice);
                    Thread.sleep(300);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Run two threads updating the same item
        Thread t1 = new Thread(priceUpdater, "Thread-1");
        Thread t2 = new Thread(priceUpdater, "Thread-2");

        long start = System.currentTimeMillis();
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("✅ ItemService concurrency test completed in " + (end - start) + " ms");
    }
}
