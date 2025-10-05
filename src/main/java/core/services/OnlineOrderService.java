package core.services;

import core.dao.OnlineOrderDAO;
import core.models.OnlineOrder;
import core.models.OnlineOrderItem;
import java.sql.Connection;
import java.sql.SQLException;

public class OnlineOrderService {
    private final OnlineOrderDAO onlineOrderDAO;

    public OnlineOrderService(Connection conn) {
        this.onlineOrderDAO = new OnlineOrderDAO(conn);
    }

    public int placeOrder(OnlineOrder order) throws SQLException {
        int orderId = onlineOrderDAO.createOrder(order);
        if (orderId > 0) {
            onlineOrderDAO.addOrderItems(orderId, order.getItems());
        }
        return orderId;
    }
}
