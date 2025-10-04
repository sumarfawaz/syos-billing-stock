<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
    if (!user.getRole().equalsIgnoreCase("admin")) {
        response.sendRedirect("index.jsp?error=Unauthorized");
        return;
    }
%>
<html>
<head>
    <title>SYOS - Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background: linear-gradient(135deg, #e8f5e9, #f1f8e9);
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
        }
        .navbar {
            background: #2e7d32 !important;
        }
        .navbar-brand {
            font-weight: 700;
            letter-spacing: 1px;
        }
        .dashboard-container {
            max-width: 900px;
            margin: 60px auto;
            background: white;
            border-radius: 15px;
            padding: 3rem;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }
        h1 {
            color: #2e7d32;
            font-weight: 700;
        }
        h3 {
            color: #388e3c;
            margin-top: 1.5rem;
            font-weight: 600;
        }
        .btn-custom {
            border-radius: 10px;
            font-weight: 600;
            padding: 14px;
            transition: all 0.3s ease;
        }
        .btn-custom:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(46, 125, 50, 0.3);
        }
        .logout-btn {
            background-color: #c62828;
            color: white;
            border: none;
        }
        .logout-btn:hover {
            background-color: #b71c1c;
        }
    </style>
</head>

<body>
    <!--  Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand text-white" href="#">SYOS Admin</a>
            <div class="d-flex align-items-center">
                <span class="text-white me-3">
                    Logged in as: <strong><%= user.getUsername() %></strong>
                </span>
                <a href="logout.jsp" class="btn logout-btn">Logout</a>
            </div>
        </div>
    </nav>

    <!--  Main Dashboard -->
    <div class="dashboard-container text-center">
        <h1>Welcome, <%= user.getUsername() %></h1>
        <p class="text-muted">Role: <span class="badge bg-success"><%= user.getRole() %></span></p>
        <hr class="my-4">

        <h3>Management Options</h3>
        <div class="d-grid gap-3 mt-4">
            <a href="billing" class="btn btn-success btn-custom">Billing</a>
            <a href="stock" class="btn btn-outline-success btn-custom">Stock Management</a>
            <a href="item" class="btn btn-outline-success btn-custom">Item Management</a>
            <a href="report" class="btn btn-outline-success btn-custom">Generate Reports</a>
            <!-- <a href="users" class="btn btn-outline-success btn-custom">👥 User Management</a> -->
        </div>
    </div>

    <!--  Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
