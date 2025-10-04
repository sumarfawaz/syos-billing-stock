<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="core.models.User, java.util.List, core.models.Item" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp?error=Please login first");
        return;
    }
    List<Item> itemList = (List<Item>) request.getAttribute("itemList");
%>
<html>
  <head>
    <title>SYOS - Billing</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
  </head>

  <body class="bg-light">
    <div class="container py-5">
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="text-primary fw-bold">Billing</h1>
        <a href="admin-dashboard.jsp" class="btn btn-secondary">Back to Dashboard</a>
      </div>

      <!-- Alerts -->
      <% if (request.getAttribute("error") != null) { %>
      <div class="alert alert-danger">
        <%= request.getAttribute("error") %>
      </div>
      <% } %>
      <% if (request.getAttribute("success") != null) { %>
      <div class="alert alert-success">
        <%= request.getAttribute("success") %>
      </div>
      <% } %>

      <!-- Billing Form -->
      <div class="card shadow-sm p-4 mb-5">
        <form id="billingForm" action="billing" method="post">
          <h4 class="mb-3 text-secondary">Add Items to Bill</h4>

          <div id="items" class="mb-3">
            <div class="row g-2 align-items-center mb-2">
              <div class="col-md-4">
                <input
                  type="text"
                  name="itemCode"
                  class="form-control item-code"
                  placeholder="Item Code"
                  required
                />
              </div>
              <div class="col-md-3">
                <input
                  type="number"
                  name="quantity"
                  class="form-control item-qty"
                  placeholder="Quantity"
                  min="1"
                  required
                  oninput="updateTotal()"
                />
              </div>
              <div class="col-md-3">
                <input
                  type="number"
                  name="price"
                  class="form-control item-price"
                  placeholder="Price (Rs.)"
                  step="0.01"
                  min="0"
                  required
                  oninput="updateTotal()"
                />
              </div>
              <div class="col-md-2 text-center fw-bold pt-2 item-total">
                0.00
              </div>
            </div>
          </div>

          <button
            type="button"
            class="btn btn-outline-primary mb-4"
            onclick="addItem()"
          >
            Add Another Item
          </button>

          <div
            class="d-flex justify-content-between align-items-center mb-3 border-top pt-3"
          >
            <h5 class="text-dark mb-0">Subtotal:</h5>
            <h4 id="subtotalDisplay" class="text-success mb-0">Rs. 0.00</h4>
          </div>

          <div class="mb-3">
            <label for="cash" class="form-label fw-semibold">Cash Tendered:</label>
            <input
              type="number"
              id="cash"
              name="cash"
              step="0.01"
              min="0"
              class="form-control w-50"
              required
              oninput="calculateChange()"
            />
          </div>

          <div
            class="d-flex justify-content-between align-items-center mb-3 border-top pt-3"
          >
            <h5 class="text-dark mb-0">Change:</h5>
            <h4 id="changeDisplay" class="text-primary mb-0">Rs. 0.00</h4>
          </div>

          <button
            type="submit"
            class="btn btn-success w-100 mt-3 fw-semibold"
          >
            Generate Bill
          </button>
        </form>
      </div>

      <!-- Item Reference Table -->
      <div class="card shadow-sm p-4">
        <h4 class="mb-3 text-secondary">Item Price Reference</h4>

        <input
          type="text"
          id="searchBox"
          class="form-control mb-3"
          placeholder="Search by item code or name..."
          onkeyup="filterItems()"
        />

        <div class="table-responsive" style="max-height: 400px; overflow-y: auto">
          <table
            class="table table-bordered table-hover align-middle"
            id="itemsTable"
          >
            <thead class="table-dark sticky-top">
              <tr>
                <th>Item Code</th>
                <th>Item Name</th>
                <th>Price (Rs.)</th>
              </tr>
            </thead>
            <tbody>
              <% if (itemList != null && !itemList.isEmpty()) {
                   for (Item i : itemList) { %>
              <tr>
                <td><%= i.getCode() %></td>
                <td><%= i.getName() %></td>
                <td><%= String.format("%.2f", i.getPrice()) %></td>
              </tr>
              <% } } else { %>
              <tr>
                <td colspan="3" class="text-center text-muted py-3">
                  No items available
                </td>
              </tr>
              <% } %>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- JavaScript -->
    <script>
      function addItem() {
        const itemsDiv = document.getElementById("items");
        const newRow = document.createElement("div");
        newRow.className = "row g-2 align-items-center mb-2";
        newRow.innerHTML = `
          <div class="col-md-4">
              <input type="text" name="itemCode" class="form-control item-code" placeholder="Item Code" required>
          </div>
          <div class="col-md-3">
              <input type="number" name="quantity" class="form-control item-qty" placeholder="Quantity" min="1" required oninput="updateTotal()">
          </div>
          <div class="col-md-3">
              <input type="number" name="price" class="form-control item-price" placeholder="Price (Rs.)" step="0.01" min="0" required oninput="updateTotal()">
          </div>
          <div class="col-md-2 text-center fw-bold pt-2 item-total">0.00</div>
        `;
        itemsDiv.appendChild(newRow);
        newRow.scrollIntoView({ behavior: "smooth", block: "center" });
      }

      function updateTotal() {
        let subtotal = 0;
        document.querySelectorAll("#items .row").forEach((row) => {
          const qty = parseFloat(row.querySelector(".item-qty")?.value || 0);
          const price = parseFloat(row.querySelector(".item-price")?.value || 0);
          const total = qty * price;
          row.querySelector(".item-total").textContent = total.toFixed(2);
          subtotal += total;
        });
        document.getElementById("subtotalDisplay").textContent =
          "Rs. " + subtotal.toFixed(2);
        calculateChange();
      }

      function calculateChange() {
        const subtotal =
          parseFloat(
            document
              .getElementById("subtotalDisplay")
              .textContent.replace("Rs. ", "")
          ) || 0;
        const cash = parseFloat(document.getElementById("cash").value || 0);
        const change = cash - subtotal;
        document.getElementById("changeDisplay").textContent =
          "Rs. " + (change >= 0 ? change.toFixed(2) : "0.00");
      }

      function filterItems() {
        const query = document.getElementById("searchBox").value.toLowerCase();
        const rows = document.querySelectorAll("#itemsTable tbody tr");
        rows.forEach((row) => {
          const text = row.textContent.toLowerCase();
          row.style.display = text.includes(query) ? "" : "none";
        });
      }
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
