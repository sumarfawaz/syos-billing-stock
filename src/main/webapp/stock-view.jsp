<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User, core.models.StockEntry, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
%>
<html>
<head>
    <title>View All Stock Entries</title>
    <!-- ✅ Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="text-primary">📦 All Stock Entries</h1>
        <a href="javascript:history.back()" class="btn btn-secondary">⬅ Back to Stock Menu</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("message") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("message") %></div>
    <% } %>

    <div class="card shadow-sm p-4">
        <h4 class="mb-3">Stock List</h4>
        <table class="table table-bordered table-hover align-middle">
            <thead class="table-dark">
                <tr>
                    <th>Stock Entry ID</th>
                    <th>Item Code</th>
                    <th>Quantity</th>
                    <th>Entry Date</th>
                    <th>Expiry Date</th>
                </tr>
            </thead>
            <tbody>
            <%
                List<StockEntry> entries = (List<StockEntry>) request.getAttribute("stockEntries");
                if (entries != null && !entries.isEmpty()) {
                    for (StockEntry entry : entries) {
            %>
                <tr>
                    <td><%= entry.getId() %></td>
                    <td><%= entry.getItemCode() %></td>
                    <td><%= entry.getQuantity() %></td>
                    <td><%= entry.getEntryDate() %></td>
                    <td><%= entry.getExpiryDate() %></td>
                </tr>
            <% 
                    } 
                } else { 
            %>
                <tr>
                    <td colspan="5" class="text-center text-muted">No stock entries available.</td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>

<!-- ✅ Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
