<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user != null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
%>
<html>
<head>
    <title>SYOS - Login</title>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">

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
        .login-container {
            background: white;
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            width: 400px;
            text-align: center;
        }
        .login-container h1 {
            font-weight: 700;
            color: #0d47a1;
            margin-bottom: 1.5rem;
        }
        .form-label {
            font-weight: 600;
            color: #333;
        }
        .form-control {
            padding: 0.75rem;
            border-radius: 10px;
        }
        .btn-login {
            background-color: #1976d2;
            color: white;
            font-weight: 600;
            padding: 0.75rem;
            border-radius: 10px;
            transition: all 0.2s ease;
        }
        .btn-login:hover {
            background-color: #0d47a1;
            transform: scale(1.03);
        }
        .register-link {
            margin-top: 1rem;
            display: block;
            color: #1976d2;
            font-weight: 500;
            text-decoration: none;
        }
        .register-link:hover {
            text-decoration: underline;
        }
        .alert {
            margin-top: 1rem;
        }
    </style>
</head>

<body>
    <div class="login-container">
        <h1>Login</h1>
        <form action="api/auth/login" method="post">
            <div class="mb-3 text-start">
                <label for="username" class="form-label">Username:</label>
                <input type="text" id="username" name="username" class="form-control" required>
            </div>

            <div class="mb-3 text-start">
                <label for="password" class="form-label">Password:</label>
                <input type="password" id="password" name="password" class="form-control" required>
            </div>

            <button type="submit" class="btn btn-login w-100">Login</button>
        </form>

        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getParameter("error") %>
            </div>
        <% } %>

        <a href="register.jsp" class="register-link">Don’t have an account? Register here</a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
