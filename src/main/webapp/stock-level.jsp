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
    <title>SYOS - View Stock Level</title>
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
        rel="stylesheet"
    />
</head>

<body class="bg-light">
<div class="container py-5">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="text-primary fw-bold">View Stock Level</h1>
        <a href="javascript:history.back()" class="btn btn-secondary">Back to Stock Menu</a>
    </div>

    <!-- Error message -->
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger mb-4">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <!-- Main Card -->
    <div class="card shadow-sm p-4">
        <% if (request.getAttribute("itemCode") != null) { %>
            <!-- Display Stock Info -->
            <h4 class="text-secondary mb-3">
                Stock Level for <span class="text-dark fw-bold"><%= request.getAttribute("itemCode") %></span>
            </h4>
            <div class="alert alert-info fs-5">
                Available Stock: <span class="fw-bold"><%= request.getAttribute("stockLevel") %></span> units
            </div>
        <% } else { %>
            <!-- Input Form -->
            <h4 class="text-secondary mb-3">Check Stock Availability</h4>
            <form action="stock/level" method="get" class="w-50">
                <div class="mb-3">
                    <label for="code" class="form-label fw-semibold">Item Code:</label>
                    <input
                        type="text"
                        id="code"
                        name="code"
                        class="form-control"
                        placeholder="Enter Item Code"
                        required
                    />
                </div>
                <button type="submit" class="btn btn-primary w-100 fw-semibold">View Stock Level</button>
            </form>
        <% } %>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
