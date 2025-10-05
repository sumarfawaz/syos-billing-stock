package web.servlets;

import core.models.*;
import core.services.OnlineOrderService;
import core.utils.DatabaseConnectionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/placeOrder") // This is the missing part
public class PlaceOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        if (user == null || !"customer".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect("index.jsp?error=Unauthorized access");
            return;
        }

        try {
            Connection conn = DatabaseConnectionManager.getInstance().getConnection();
            OnlineOrderService service = new OnlineOrderService(conn);

            OnlineOrder order = new OnlineOrder();
            order.setCustomerId(user.getId()); // ✅ Fixed method name
            order.setStatus("PLACED");

            String[] itemCodes = req.getParameterValues("itemCode");
            String[] quantities = req.getParameterValues("quantity");
            String[] prices = req.getParameterValues("price");

            List<OnlineOrderItem> items = new ArrayList<>();
            double total = 0;

            if (itemCodes != null) {
                for (int i = 0; i < itemCodes.length; i++) {
                    OnlineOrderItem item = new OnlineOrderItem();
                    item.setItemCode(itemCodes[i]);
                    item.setQuantity(Integer.parseInt(quantities[i]));
                    item.setPrice(Double.parseDouble(prices[i]));
                    total += item.getQuantity() * item.getPrice();
                    items.add(item);
                }
            }

            order.setItems(items);
            order.setTotalAmount(total);

            int orderId = service.placeOrder(order);

            if (orderId > 0) {
                resp.sendRedirect(req.getContextPath()
                        + "/customer-dashboard.jsp?success=Order placed successfully! ID=" + orderId);
            } else {
                resp.sendRedirect(req.getContextPath() + "/customer-dashboard.jsp?error=Order placement failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("customerDashboard.jsp?error=" + e.getMessage());
        }
    }
}
