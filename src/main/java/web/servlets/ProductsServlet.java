package web.servlets;

import core.dao.ShelfDAO;
import core.models.Shelf;
import core.services.ShelfService;
import core.utils.DatabaseConnectionManager;

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

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {
    private ShelfDAO shelfDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DatabaseConnectionManager.getInstance().getConnection();
            this.shelfDAO = new ShelfDAO(conn);
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize ProductsServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.jsp?error=Please login first");
            return;
        }

        try {
            List<Map<String, Object>> products = shelfDAO.getShelfWithItems();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error fetching products", e);
        }
    }
}

