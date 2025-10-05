package core.dao;

import core.models.OnlineOrder;
import core.models.OnlineOrderItem;

import java.sql.*;
import java.util.*;

public class OnlineOrderDAO {
    private final Connection conn;

    public OnlineOrderDAO(Connection conn) {
        this.conn = conn;
    }

    public int createOrder(OnlineOrder order) throws SQLException {
        String sql = "INSERT INTO online_orders (customer_id, total_amount, status) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, order.getCustomerId());
        stmt.setDouble(2, order.getTotalAmount());
        stmt.setString(3, order.getStatus());
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    public void addOrderItems(int orderId, List<OnlineOrderItem> items) throws SQLException {
        String sql = "INSERT INTO online_order_items (order_id, item_code, quantity, price) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (OnlineOrderItem item : items) {
            stmt.setInt(1, orderId);
            stmt.setString(2, item.getItemCode());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.addBatch();
        }
        stmt.executeBatch();
    }
}
