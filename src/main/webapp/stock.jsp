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
    <title>SYOS - Stock Management</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="text-primary">Stock Management</h1>
        <a href="admin-dashboard.jsp" class="btn btn-secondary"> Back to Dashboard</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("message") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("message") %></div>
    <% } %>

    <!-- Stock Actions -->
    <div class="card shadow-sm p-4 mb-4">
        <h4 class="mb-3">Stock Actions</h4>
        <div class="d-grid gap-2 d-md-flex">
            <button class="btn btn-outline-success" data-bs-toggle="modal" data-bs-target="#addStockModal">Add Stock</button>
            <button class="btn btn-outline-warning" data-bs-toggle="modal" data-bs-target="#allocateStockModal">Allocate Stock</button>
            <a href="stock/reorder" class="btn btn-outline-danger">Check Reorder Alerts</a>
            <button class="btn btn-outline-info" data-bs-toggle="modal" data-bs-target="#stockLevelModal">View Stock Level</button>
            <button class="btn btn-outline-dark" data-bs-toggle="modal" data-bs-target="#updateStockModal">Update Stock</button>
            <% if ("admin".equals(user.getRole())) { %>
                <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteStockModal">Delete Stock</button>
            <% } %>
        </div>
    </div>

    <!--  All Stock Entries Table -->
    <div class="card shadow-sm p-4">
        <h4 class="mb-3">All Stock Entries</h4>
        <table class="table table-bordered table-hover mt-3">
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

<!--  Add Stock Modal -->
<div class="modal fade" id="addStockModal" tabindex="-1">
  <div class="modal-dialog">
    <form action="stock" method="post" class="modal-content">
        <input type="hidden" name="action" value="add">
        <div class="modal-header">
            <h5 class="modal-title">Add Stock Entry</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <label class="form-label">Item Code</label>
            <input type="text" name="code" class="form-control" required>
            <label class="form-label mt-2">Quantity</label>
            <input type="number" name="quantity" min="1" class="form-control" required>
            <label class="form-label mt-2">Expiry Date</label>
            <input type="date" name="expiry" class="form-control" required>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-success">Add</button>
        </div>
    </form>
  </div>
</div>

<!-- Allocate Stock Modal -->
<div class="modal fade" id="allocateStockModal" tabindex="-1">
  <div class="modal-dialog">
    <form action="stock" method="post" class="modal-content">
        <input type="hidden" name="action" value="allocate">
        <div class="modal-header">
            <h5 class="modal-title">Allocate Stock</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <label class="form-label">Item Code</label>
            <input type="text" name="code" class="form-control" required>
            <label class="form-label mt-2">Quantity</label>
            <input type="number" name="quantity" min="1" class="form-control" required>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-warning">Allocate</button>
        </div>
    </form>
  </div>
</div>

<!-- Stock Level Modal -->
<div class="modal fade" id="stockLevelModal" tabindex="-1">
  <div class="modal-dialog">
    <form action="stock/level" method="get" class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title">View Stock Level</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <label class="form-label">Item Code</label>
            <input type="text" name="code" class="form-control" required>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-info">View</button>
        </div>
    </form>
  </div>
</div>

<!--  Update Stock Modal -->
<div class="modal fade" id="updateStockModal" tabindex="-1">
  <div class="modal-dialog">
    <form action="stock" method="post" class="modal-content">
        <input type="hidden" name="action" value="update">
        <div class="modal-header">
            <h5 class="modal-title">✏ Update Stock Entry</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <label class="form-label">Item Code</label>
            <input type="text" name="itemCode" class="form-control" required>
            <label class="form-label mt-2">New Quantity</label>
            <input type="number" name="quantity" min="1" class="form-control" required>
            <label class="form-label mt-2">New Expiry Date</label>
            <input type="date" name="expiry" class="form-control" required>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-dark">Update</button>
        </div>
    </form>
  </div>
</div>

<!--  Delete Stock Modal -->
<div class="modal fade" id="deleteStockModal" tabindex="-1">
  <div class="modal-dialog">
    <form action="stock" method="post" class="modal-content">
        <input type="hidden" name="action" value="delete">
        <div class="modal-header">
            <h5 class="modal-title">🗑 Delete Stock Entry</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <label class="form-label">Stock Entry ID</label>
            <input type="number" name="entryId" min="1" class="form-control" required>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-danger">Delete</button>
        </div>
    </form>
  </div>
</div>

<!--  Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
