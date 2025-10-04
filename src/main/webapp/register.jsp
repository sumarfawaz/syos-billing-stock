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
    <title>SYOS - Register</title>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background: linear-gradient(135deg, #f1f8e9, #dcedc8);
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Poppins', sans-serif;
        }
        .register-container {
            background: white;
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            width: 450px;
            text-align: center;
        }
        .register-container h1 {
            font-weight: 700;
            color: #33691e;
            margin-bottom: 1.5rem;
        }
        .form-label {
            font-weight: 600;
            color: #333;
        }
        .form-control, .form-select {
            padding: 0.75rem;
            border-radius: 10px;
        }
        .btn-register {
            background-color: #689f38;
            color: white;
            font-weight: 600;
            padding: 0.75rem;
            border-radius: 10px;
            transition: all 0.2s ease;
        }
        .btn-register:hover {
            background-color: #33691e;
            transform: scale(1.03);
        }
        .login-link {
            margin-top: 1rem;
            display: block;
            color: #558b2f;
            font-weight: 500;
            text-decoration: none;
        }
        .login-link:hover {
            text-decoration: underline;
        }
        .alert {
            margin-top: 1rem;
        }
    </style>
</head>

<body>
    <div class="register-container">
        <h1>Create Account</h1>
        <form action="api/auth/register" method="post">
            <div class="mb-3 text-start">
                <label for="username" class="form-label">Username:</label>
                <input type="text" id="username" name="username" class="form-control" required>
            </div>

            <div class="mb-3 text-start">
                <label for="role" class="form-label">Role:</label>
                <select id="role" name="role" class="form-select" required>
                    <option value="" disabled selected>Select role</option>
                    <option value="employee">Employee</option>
                    <option value="admin">Admin</option>
                    <option value="customer">Customer</option>
                </select>
            </div>

            <div class="mb-3 text-start">
                <label for="password" class="form-label">Password:</label>
                <input type="password" id="password" name="password" class="form-control" required>
            </div>

            <button type="submit" class="btn btn-register w-100">Register</button>
        </form>

        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger mt-3">
                <%= request.getParameter("error") %>
            </div>
        <% } %>

        <% if (request.getParameter("message") != null) { %>
            <div class="alert alert-success mt-3">
                <%= request.getParameter("message") %>
            </div>
        <% } %>

        <a href="login.jsp" class="login-link">Already have an account? Login here</a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
