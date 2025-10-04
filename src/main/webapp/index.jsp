<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user != null) {
        String role = user.getRole().toLowerCase();
        if ("admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp");
            return;
        } else if ("employee".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/employee-dashboard.jsp");
            return;
        } else if ("customer".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            return;
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            return;
        }
    }
%>
<html>
<head>
    <title>SYOS - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Poppins', sans-serif;
        }
        .home-container {
            background: white;
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            max-width: 500px;
            text-align: center;
        }
        .home-container h1 {
            font-weight: 700;
            color: #0d47a1;
        }
        .home-container p {
            color: #555;
            margin-bottom: 2rem;
        }
        .btn-custom {
            width: 45%;
            font-weight: 600;
            padding: 0.75rem;
            border-radius: 8px;
            transition: all 0.2s ease;
        }
        .btn-custom:hover {
            transform: scale(1.05);
        }
        .btn-login {
            background-color: #1976d2;
            color: white;
        }
        .btn-register {
            background-color: #43a047;
            color: white;
        }
    </style>
</head>

<body>
    <div class="home-container">
        <h1>SYOS Billing & Stock System</h1>
        <p>Welcome to the Smart Your Own Store management system. Please log in or register to continue.</p>
        <div class="d-flex justify-content-between">
            <a href="api/auth/login" class="btn btn-custom btn-login">Login</a>
            <a href="api/auth/register" class="btn btn-custom btn-register">Register</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
