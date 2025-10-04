<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="core.models.User" %>
<%@ page import="core.models.Order" %>
<%@ page import="core.dao.OrderDAO" %>
<%@ page import="core.utils.DatabaseConnectionManager" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.getRole().equalsIgnoreCase("customer")) {
        response.sendRedirect("login.jsp?error=Unauthorized");
        return;
    }

    List<Order> orders = null;
    try {
        OrderDAO orderDAO = new OrderDAO(DatabaseConnectionManager.getInstance().getConnection());
        orders = orderDAO.getOrdersByUserId(user.getId()); // you'll implement this in OrderDAO
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<html>
<head>
    <title>My Orders</title>
</head>
<body>
    <h1>My Orders</h1>
    <p>Welcome, <%= user.getUsername() %></p>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Order ID</th>
            <th>Date</th>
            <th>Total</th>
            <th>Status</th>
        </tr>
        <%
            if (orders != null && !orders.isEmpty()) {
                for (Order order : orders) {
        %>
        <tr>
            <td><%= order.getId() %></td>
            <td><%= order.getOrderDate() %></td>
            <td>$<%= order.getTotal() %></td>
            <td><%= order.getStatus() %></td>
        </tr>
        <% 
                }
            } else {
        %>
        <tr>
            <td colspan="4">No orders found.</td>
        </tr>
        <% } %>
    </table>

    <p><a href="customer-dashboard.jsp">⬅ Back to Dashboard</a></p>
</body>
</html>
