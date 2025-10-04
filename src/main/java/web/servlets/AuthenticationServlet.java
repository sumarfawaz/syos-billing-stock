package web.servlets;

import core.services.AuthenticationService;
import core.dao.UserDAO;
import core.models.User;
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

@WebServlet(urlPatterns = "/api/auth/*", loadOnStartup = 1, asyncSupported = true)
public class AuthenticationServlet extends HttpServlet {
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DatabaseConnectionManager.getInstance().getConnection();
            UserDAO userDAO = new UserDAO(conn);
            this.authService = new AuthenticationService(userDAO);
        } catch (SQLException e) {
            throw new ServletException("❌ Failed to initialize AuthenticationServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user != null) {
            // ✅ Already logged in → go to role dashboard
            redirectByRole(resp, req, user);
            return;
        }

        String path = req.getPathInfo();
        if (path == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        switch (path) {
            case "/login":
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                break;
            case "/register":
                resp.sendRedirect(req.getContextPath() + "/register.jsp");
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user != null) {
            // ✅ Already logged in → go to role dashboard
            redirectByRole(resp, req, user);
            return;
        }

        AsyncContext asyncContext = req.startAsync();
        RequestHandler handler = (r, s) -> {
            String path = r.getPathInfo();
            if (path == null) {
                s.sendRedirect(req.getContextPath() + "/index.jsp?error=Invalid endpoint");
                return;
            }

            switch (path) {
                case "/register":
                    handleRegister(r, s);
                    break;
                case "/login":
                    handleLogin(r, s);
                    break;
                default:
                    s.sendRedirect(req.getContextPath() + "/index.jsp?error=Unknown endpoint");
            }
        };
        AsyncRequestProcessor.getInstance().submitTask(new RequestTask(asyncContext, handler));
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String role = req.getParameter("role");
        String password = req.getParameter("password");

        try {
            boolean success = authService.registerUser(username, role, password);
            if (success) {
                // Auto-login after registration
                User user = authService.login(username, password);
                if (user != null) {
                    req.getSession().setAttribute("user", user);
                    redirectByRole(resp, req, user);
                } else {
                    resp.sendRedirect(
                            req.getContextPath() + "/login.jsp?message=Registration successful, please log in");
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/register.jsp?error=Username already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=Server error during registration");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = authService.login(username, password);
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                redirectByRole(resp, req, user);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp?error=Invalid username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=Server error during login");
        }
    }

    private void redirectByRole(HttpServletResponse resp, HttpServletRequest req, User user) throws IOException {
        String base = req.getContextPath();
        switch (user.getRole().toLowerCase()) {
            case "admin":
                resp.sendRedirect(base + "/admin-dashboard.jsp");
                break;
            case "employee":
                resp.sendRedirect(base + "/employee-dashboard.jsp");
                break;
            case "customer":
                resp.sendRedirect(base + "/customer/dashboard"); // ✅ servlet (loads data)
                break;
            default:
                resp.sendRedirect(base + "/dashboard.jsp");
        }
    }
}
