package core.services;

import core.billing.BasicBill;
import core.billing.BillBuilder;
import core.dao.*;
import core.models.*;
import core.discount.DiscountContext;
import core.discount.DiscountResult;
import core.utils.SerialNumberGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BillingService {
    private final ItemService itemService;
    private final BillDAO billDAO;
    private final BillItemDAO billItemDAO;
    private final StockService stockService;
    private final DiscountContext discountContext;
    private final Connection conn;

    public BillingService(Connection conn) {
        this.conn = conn;
        ShelfService shelfService = new ShelfService(new ShelfDAO(conn));
        this.itemService = new ItemService(new ItemDAO(conn), shelfService);
        this.billDAO = new BillDAO(conn);
        this.billItemDAO = new BillItemDAO(conn);
        this.stockService = new StockService(conn, itemService, shelfService);
        this.discountContext = new DiscountContext();
    }

    //   test constructor 
    public BillingService(ItemService itemService, BillDAO billDAO, BillItemDAO billItemDAO,
                          StockService stockService, DiscountContext discountContext) {
        this.conn = null;
        this.itemService = itemService;
        this.billDAO = billDAO;
        this.billItemDAO = billItemDAO;
        this.stockService = stockService;
        this.discountContext = discountContext;
    }

    //   method
    public double calculateTotal(Map<String, Integer> purchasedItems) throws SQLException {
        double total = 0;
        for (Map.Entry<String, Integer> entry : purchasedItems.entrySet()) {
            Item item = itemService.getItemByCode(entry.getKey());
            if (item == null) {
                throw new SQLException("Item not found: " + entry.getKey());
            }
            total += item.getPrice() * entry.getValue();
        }
        return total;
    }

    // Updated createBill() — synchronized for thread safety
    public void createBill(Map<String, Integer> purchasedItems, double cashTendered) throws SQLException {
        synchronized (BillingService.class) { // Only one billing at a time
            List<BillItem> billItems = new ArrayList<>();
            double total = 0;

            for (Map.Entry<String, Integer> entry : purchasedItems.entrySet()) {
                String itemCode = entry.getKey();
                int quantityNeeded = entry.getValue();

                Item item = itemService.getItemByCode(itemCode);
                if (item == null)
                    throw new SQLException("Item not found: " + itemCode);

                double itemTotal = item.getPrice() * quantityNeeded;
                total += itemTotal;

                if (!checkShelfStock(itemCode, quantityNeeded)) {
                    throw new SQLException("❌ Insufficient stock in shelf for item: " + itemCode);
                }

                billItems.add(new BillItem(item.getCode(), item.getName(), quantityNeeded, itemTotal));

                // Reduce shelf stock after successful check
                reduceShelfStock(itemCode, quantityNeeded);
            }

            // Apply discounts and calculate totals
            Bill tempBill = new BasicBill(total, 0, cashTendered, 0, billItems, 0);
            DiscountResult discountResult = discountContext.applyDiscounts(tempBill, total);

            double discount = total - discountResult.getTotalAfterDiscount();
            double netTotal = discountResult.getTotalAfterDiscount();
            double change = cashTendered - netTotal;
            int serialNumber = SerialNumberGenerator.getNextSerial();

            Bill bill = BillBuilder.build(total, discount, cashTendered, change, billItems, serialNumber);

            // Save bill & bill items
            int billId = billDAO.saveBill(bill);
            billItemDAO.saveBillItems(billId, billItems);

            // Output
            System.out.println(bill.print());
            System.out.println("✅ Bill saved with ID: " + billId);
            System.out.println("✅ Discount applied: " + discountResult.getDiscountName());
            System.out.println("Total after discount: " + netTotal);
            System.out.println("Change due: " + change);
        } // 🔒 Lock is released automatically here
    }

    // ✅ Existing helper methods (unchanged)
    private boolean checkShelfStock(String itemCode, int quantityNeeded) throws SQLException {
        Shelf shelf = stockService.getShelfService().getShelfByProductCode(itemCode);
        if (shelf == null) {
            System.out.println("⚠️ Shelf not found for item code: " + itemCode);
            return false;
        }
        int shelfCurrent = shelf.getShelfCurrent();
        return shelfCurrent >= quantityNeeded;
    }

    private void reduceShelfStock(String itemCode, int quantityNeeded) throws SQLException {
        Shelf shelf = stockService.getShelfService().getShelfByProductCode(itemCode);
        if (shelf != null) {
            int updatedQuantity = shelf.getShelfCurrent() - quantityNeeded;
            shelf.setShelfCurrent(updatedQuantity);
            stockService.getShelfService().updateShelf(shelf);
            System.out.println("✅ Shelf quantity updated: Current quantity is now " + updatedQuantity + " for " + itemCode);

            if (updatedQuantity < 10) {
                refillShelf(itemCode, updatedQuantity);
            }
        } else {
            System.out.println("⚠️ Shelf not found for item code: " + itemCode);
        }
    }

    private void refillShelf(String itemCode, int shelfQuantity) throws SQLException {
        Shelf shelf = stockService.getShelfService().getShelfByProductCode(itemCode);
        int quantityToRefill = shelf.getShelfDefault() - shelfQuantity;

        if (quantityToRefill > 0) {
            List<StockEntry> allocated = stockService.allocateStock(itemCode, quantityToRefill);
            int allocatedQuantity = allocated.stream().mapToInt(StockEntry::getQuantity).sum();

            shelf.setShelfCurrent(shelfQuantity + allocatedQuantity);
            stockService.getShelfService().updateShelf(shelf);

            System.out.println("✅ Shelf refilled: Current quantity is now " + shelf.getShelfCurrent());
        }
    }
}
