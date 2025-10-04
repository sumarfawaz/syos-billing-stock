<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User, java.util.List, java.util.Map" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
    if (!user.getRole().equalsIgnoreCase("customer")) {
        response.sendRedirect("index.jsp?error=Unauthorized");
        return;
    }
%>
<html>
<head>
    <title>SYOS - Customer Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background: linear-gradient(135deg, #e8f5e9, #f1f8e9);
            min-height: 100vh;
            font-family: 'Poppins', sans-serif;
        }
        .dashboard-container {
            max-width: 900px;
            margin: 60px auto;
            background: white;
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        h1 {
            color: #2e7d32;
            font-weight: 700;
            text-align: center;
            margin-bottom: 0.5rem;
        }
        .role-text {
            text-align: center;
            color: #666;
            margin-bottom: 2rem;
        }
        h3 {
            color: #388e3c;
            margin-top: 2rem;
            font-weight: 600;
        }
        ul {
            list-style: none;
            padding: 0;
        }
        ul li a {
            display: inline-block;
            background-color: #66bb6a;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            margin-bottom: 10px;
            transition: all 0.3s ease;
        }
        ul li a:hover {
            background-color: #2e7d32;
            transform: scale(1.05);
        }
        table {
            width: 100%;
            margin-top: 1rem;
            border-collapse: collapse;
            text-align: center;
            border-radius: 10px;
            overflow: hidden;
        }
        th {
            background-color: #388e3c;
            color: white;
            padding: 12px;
        }
        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        tr:hover {
            background-color: #f9fbe7;
        }
        .logout {
            text-align: center;
            margin-top: 2rem;
        }
        .logout a {
            color: #388e3c;
            text-decoration: none;
            font-weight: 600;
        }
        .logout a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>
<div class="dashboard-container">
    <h1>Welcome, <%= user.getUsername() %></h1>
    <p class="role-text">Role: <strong><%= user.getRole() %></strong></p>

    <h3>Available Products</h3>
    <div class="table-responsive">
        <table class="table table-bordered align-middle">
            <thead class="table-success">
                <tr>
                    <th>Product</th>
                    <th>Price (Rs.)</th>
                    <th>Available Qty</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Map<String, Object>> products = (List<Map<String, Object>>) request.getAttribute("products");
                    if (products != null && !products.isEmpty()) {
                        for (Map<String, Object> p : products) {
                %>
                <tr>
                    <td><%= p.get("itemName") %> (<%= p.get("productCode") %>)</td>
                    <td>Rs. <%= p.get("itemPrice") %></td>
                    <td><%= p.get("currentQty") %></td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="3" class="text-muted py-3">No products available</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <div class="logout">
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
