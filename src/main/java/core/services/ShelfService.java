package core.services;

import core.dao.ShelfDAO;
import core.models.Shelf;

import java.sql.SQLException;
import java.util.List;

public class ShelfService {
    private final ShelfDAO shelfDAO;

    // Constructor accepting ShelfDAO instance (Dependency Injection)
    public ShelfService(ShelfDAO shelfDAO) {
        this.shelfDAO = shelfDAO;
    }

    //  Write operation — synchronized for thread safety
    public void addShelf(Shelf shelf) throws SQLException {
        synchronized (ShelfService.class) {
            shelfDAO.addShelf(shelf);
            System.out.println("✅ Shelf added safely for item: " + shelf.getProductCode());
        }
    }

    //  Read operation — no lock (safe and fast)
    public Shelf getShelfByProductCode(String productCode) throws SQLException {
        return shelfDAO.getShelfByProductCode(productCode);
    }

    //  Write operation — synchronized
    public void updateShelf(Shelf shelf) throws SQLException {
        synchronized (ShelfService.class) {
            shelfDAO.updateShelf(shelf);
            System.out.println("✅ Shelf updated safely for item: " + shelf.getProductCode());
        }
    }

    //  Write operation — synchronized
    public void deleteShelf(String productCode) throws SQLException {
        synchronized (ShelfService.class) {
            shelfDAO.deleteShelf(productCode);
            System.out.println("✅ Shelf deleted safely for product: " + productCode);
        }
    }

    //  Read operation 
    public List<Shelf> getAllShelves() throws SQLException {
        return shelfDAO.getAllShelves();
    }
}
