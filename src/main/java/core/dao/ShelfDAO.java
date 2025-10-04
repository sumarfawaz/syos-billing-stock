package core.dao;

import core.models.Shelf;
import core.repositories.ShelfRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShelfDAO implements ShelfRepository {
    private final Connection conn;

    public ShelfDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addShelf(Shelf shelf) throws SQLException {
        String sql = "INSERT INTO shelves (product_code, shelf_default, shelf_current) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shelf.getProductCode());
            stmt.setInt(2, shelf.getShelfDefault());
            stmt.setInt(3, shelf.getShelfCurrent());
            stmt.executeUpdate();
        }
    }

    public List<Shelf> getAllShelves() throws SQLException {
        List<Shelf> shelves = new ArrayList<>();
        String sql = "SELECT * FROM shelves"; // ✅ use the correct table
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Shelf shelf = new Shelf(
                        rs.getInt("shelf_id"),
                        rs.getString("product_code"),
                        rs.getInt("shelf_default"),
                        rs.getInt("shelf_current"));
                shelves.add(shelf);
            }
        }
        return shelves;
    }

    @Override
    public Shelf getShelfByProductCode(String productCode) throws SQLException {
        String sql = "SELECT * FROM shelves WHERE product_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Shelf(
                        rs.getInt("shelf_id"),
                        rs.getString("product_code"),
                        rs.getInt("shelf_default"),
                        rs.getInt("shelf_current"));
            }
        }
        return null;
    }

    @Override
    public void updateShelf(Shelf shelf) throws SQLException {
        String sql = "UPDATE shelves SET shelf_default = ?, shelf_current = ? WHERE product_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shelf.getShelfDefault());
            stmt.setInt(2, shelf.getShelfCurrent());
            stmt.setString(3, shelf.getProductCode());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteShelf(String productCode) throws SQLException {
        String sql = "DELETE FROM shelves WHERE product_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getShelfWithItems() throws SQLException {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT s.shelf_id, s.product_code, s.shelf_default, s.shelf_current, " +
                "i.name AS itemName, i.price AS itemPrice " +
                "FROM shelves s " +
                "JOIN items i ON s.product_code = i.code";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("shelfId", rs.getInt("shelf_id"));
                row.put("productCode", rs.getString("product_code"));
                row.put("defaultQty", rs.getInt("shelf_default"));
                row.put("currentQty", rs.getInt("shelf_current"));
                row.put("itemName", rs.getString("itemName")); // ✅ use alias correctly
                row.put("itemPrice", rs.getDouble("itemPrice")); // ✅ use alias correctly
                products.add(row);
            }
        }
        return products;
    }

}
