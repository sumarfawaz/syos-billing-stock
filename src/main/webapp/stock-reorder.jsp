<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User, java.util.Map" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
%>
<html>
<head>
    <title>SYOS - Reorder Alerts</title>
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
        rel="stylesheet"
    />
</head>

<body class="bg-light">
    <div class="container py-5">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="text-primary fw-bold">Reorder Alerts</h1>
            <a href="javascript:history.back()" class="btn btn-secondary">
                Back to Stock Menu
            </a>
        </div>

        <!-- Alerts -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!-- Card Section -->
        <div class="card shadow-sm p-4">
            <h4 class="mb-3 text-secondary">Items Needing Reorder</h4>

            <%
                Map<String, Integer> reorderItems = (Map<String, Integer>) request.getAttribute("reorderItems");
                if (reorderItems == null || reorderItems.isEmpty()) {
            %>
                <div class="alert alert-success text-center mb-0">
                    All stock levels are sufficient. No items need reordering.
                </div>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle">
                        <thead class="table-dark">
                            <tr>
                                <th>Item Code</th>
                                <th>Quantity Left</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Map.Entry<String, Integer> entry : reorderItems.entrySet()) { 
                                   int qty = entry.getValue();
                                   String statusClass = qty < 10 ? "text-danger fw-bold" :
                                                        qty < 25 ? "text-warning fw-semibold" :
                                                        "text-success";
                            %>
                            <tr>
                                <td><%= entry.getKey() %></td>
                                <td><%= qty %></td>
                                <td class="<%= statusClass %>">
                                    <%= qty < 10 ? "Critical Low" : qty < 25 ? "Low" : "Sufficient" %>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
