<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
%>
<html>
<head>
    <title>SYOS - Reports</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">
<div class="container py-5">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="text-primary fw-bold">Generate Reports</h1>
        <a href="admin-dashboard.jsp" class="btn btn-secondary">Back to Dashboard</a>
    </div>

    <!-- Alerts -->
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger mb-4"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("message") != null) { %>
        <div class="alert alert-success mb-4"><%= request.getAttribute("message") %></div>
    <% } %>

    <!-- Reports Section -->
    <div class="card shadow-sm p-4 mb-4">
        <h4 class="text-secondary mb-3">Select a Report to Generate</h4>

        <div class="list-group">
            <!-- Reorder Report -->
            <form action="report" method="post" class="mb-3">
                <input type="hidden" name="action" value="reorder">
                <button type="submit" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                    <span>Reorder Level Report</span>
                    <span class="badge bg-primary">Generate</span>
                </button>
            </form>

            <!-- Daily Sales Report -->
            <form action="report" method="post" class="mb-3">
                <input type="hidden" name="action" value="daily">
                <div class="input-group">
                    <span class="input-group-text fw-semibold">Date</span>
                    <input type="date" name="date" class="form-control" required>
                    <button type="submit" class="btn btn-primary">Daily Sales Report</button>
                </div>
            </form>

            <!-- Stock Report -->
            <form action="report" method="post" class="mb-3">
                <input type="hidden" name="action" value="stock">
                <button type="submit" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                    <span>Stock Report</span>
                    <span class="badge bg-primary">Generate</span>
                </button>
            </form>

            <!-- Bill Report -->
            <form action="report" method="post" class="mb-3">
                <input type="hidden" name="action" value="bill">
                <div class="input-group">
                    <span class="input-group-text fw-semibold">Date</span>
                    <input type="date" name="date" class="form-control" required>
                    <button type="submit" class="btn btn-primary">Bill Report</button>
                </div>
            </form>

            <!-- All Reports -->
            <form action="report" method="post" class="mb-3">
                <input type="hidden" name="action" value="all">
                <div class="input-group">
                    <span class="input-group-text fw-semibold">Date</span>
                    <input type="date" name="date" class="form-control" required>
                    <button type="submit" class="btn btn-success">Generate All Reports</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Report Output -->
    <% if ("report".equals(request.getAttribute("view"))) { %>
        <div class="card shadow-sm p-4 mt-4">
            <h4 class="text-secondary mb-3">Report Output</h4>
            <pre class="bg-dark text-light p-3 rounded" style="max-height: 400px; overflow-y: auto;">
<%= request.getAttribute("report") %>
            </pre>
        </div>
    <% } %>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
