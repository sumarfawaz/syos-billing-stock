package web.servlets;

import core.dao.ItemDAO;
import core.models.Item;
import core.models.User;
import core.services.BillingService;
import core.utils.DatabaseConnectionManager;
import web.async.AsyncRequestProcessor;
import web.async.RequestHandler;
import web.async.RequestTask;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/billing/*", asyncSupported = true)
public class BillingServlet extends HttpServlet {
    private BillingService billingService;
    private ItemDAO itemDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DatabaseConnectionManager.getInstance().getConnection();
            this.billingService = new BillingService(conn);
            this.itemDAO = new ItemDAO(conn);
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize BillingServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=Please+login+first");
            return;
        }

        try {
            // ✅ Load all items to display in billing.jsp
            List<Item> itemList = itemDAO.getAllItems();
            request.setAttribute("itemList", itemList);
        } catch (SQLException e) {
            request.setAttribute("error", "Failed to load items: " + e.getMessage());
        }

        // ✅ Handle success/error messages passed via redirect
        String success = request.getParameter("success");
        String error = request.getParameter("error");
        if (success != null) request.setAttribute("success", success);
        if (error != null) request.setAttribute("error", error);

        request.getRequestDispatcher("/billing.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AsyncContext asyncContext = request.startAsync();
        RequestHandler handler = (req, res) -> {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                res.sendRedirect(req.getContextPath() + "/index.jsp?error=Please+login+first");
                return;
            }

            Map<String, Integer> purchasedItems = new HashMap<>();
            String[] itemCodes = req.getParameterValues("itemCode");
            String[] quantities = req.getParameterValues("quantity");

            if (itemCodes != null && quantities != null) {
                for (int i = 0; i < itemCodes.length; i++) {
                    if (!itemCodes[i].isEmpty() && !quantities[i].isEmpty()) {
                        try {
                            int qty = Integer.parseInt(quantities[i]);
                            purchasedItems.put(itemCodes[i], purchasedItems.getOrDefault(itemCodes[i], 0) + qty);
                        } catch (NumberFormatException e) {
                            res.sendRedirect(req.getContextPath() + "/billing?error=Invalid+quantity+for+" + itemCodes[i]);
                            return;
                        }
                    }
                }
            }

            if (purchasedItems.isEmpty()) {
                res.sendRedirect(req.getContextPath() + "/billing?error=No+items+added+to+bill");
                return;
            }

            String cashStr = req.getParameter("cash");
            if (cashStr == null || cashStr.isEmpty()) {
                res.sendRedirect(req.getContextPath() + "/billing?error=Cash+tendered+is+required");
                return;
            }

            try {
                double cash = Double.parseDouble(cashStr);
                double total = billingService.calculateTotal(purchasedItems);

                if (cash < total) {
                    res.sendRedirect(req.getContextPath() + "/billing?error=Cash+tendered+is+less+than+total");
                    return;
                }

                billingService.createBill(purchasedItems, cash);

                // ✅ Use redirect to avoid form resubmission on refresh
                String message = String.format("Bill+generated+successfully!+Total:+%.2f,+Change:+%.2f", total, (cash - total));
                res.sendRedirect(req.getContextPath() + "/billing?success=" + message);

            } catch (NumberFormatException e) {
                res.sendRedirect(req.getContextPath() + "/billing?error=Invalid+cash+amount");
            } catch (SQLException e) {
                res.sendRedirect(req.getContextPath() + "/billing?error=Database+error:+"
                        + e.getMessage().replace(" ", "+"));
            }
        };
        AsyncRequestProcessor.getInstance().submitTask(new RequestTask(asyncContext, handler));
    }
}
