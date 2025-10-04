<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
    if (!user.getRole().equalsIgnoreCase("employee")) {
        response.sendRedirect("index.jsp?error=Unauthorized");
        return;
    }
%>
<html>
<head>
    <title>SYOS - Employee Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background: linear-gradient(135deg, #f1f8e9, #dcedc8);
            height: 100vh;
            font-family: 'Poppins', sans-serif;
        }
        .dashboard-container {
            max-width: 700px;
            margin: 60px auto;
            background: white;
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            text-align: center;
        }
        h1 {
            color: #33691e;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        h3 {
            color: #558b2f;
            margin-top: 2rem;
            font-weight: 600;
        }
        .role-text {
            color: #666;
            margin-bottom: 1rem;
        }
        ul {
            list-style: none;
            padding: 0;
            margin-top: 1rem;
        }
        li {
            margin-bottom: 15px;
        }
        .dashboard-link {
            display: inline-block;
            background-color: #689f38;
            color: white;
            padding: 0.75rem 2rem;
            border-radius: 10px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.2s ease;
        }
        .dashboard-link:hover {
            background-color: #33691e;
            transform: scale(1.05);
            text-decoration: none;
        }
        .logout {
            margin-top: 2rem;
        }
        .logout a {
            color: #558b2f;
            text-decoration: none;
            font-weight: 500;
        }
        .logout a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>
    <div class="dashboard-container">
        <h1>Welcome, Cashier <%= user.getUsername() %></h1>
        <p class="role-text">Role: <strong><%= user.getRole() %></strong></p>

        <h3>Cashier Options</h3>
        <ul>
            <li><a href="billing" class="dashboard-link">Go to Billing</a></li>
        </ul>

        <div class="logout">
            <a href="logout.jsp">Logout</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
