package web.servlets;

import core.dao.ShelfDAO;
import core.utils.DatabaseConnectionManager;
import core.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/customer/dashboard")
public class CustomerDashboardServlet extends HttpServlet {
    private ShelfDAO shelfDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DatabaseConnectionManager.getInstance().getConnection();
            this.shelfDAO = new ShelfDAO(conn);
        } catch (SQLException e) {
            throw new ServletException("Failed to init CustomerDashboardServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=Please login first");
            return;
        }

        if (!"customer".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=Unauthorized");
            return;
        }

        try {
            List<Map<String, Object>> products = shelfDAO.getShelfWithItems();
            System.out.println("✅ Products fetched: " + products.size()); // DEBUG
            for (Map<String, Object> p : products) {
                System.out.println("   -> " + p); // DEBUG
            }
            request.setAttribute("products", products);
            request.getRequestDispatcher("/customer-dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error fetching products for customer dashboard", e);
        }

    }
}
