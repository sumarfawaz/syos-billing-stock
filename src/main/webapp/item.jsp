<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="core.models.User, core.models.Item, java.util.List" %> <% User user =
(User) session.getAttribute("user"); if (user == null) {
response.sendRedirect("index.jsp?error=Please login first"); return; } List<Item>
  items = (List<Item
    >) request.getAttribute("items"); %>
    <html>
      <head>
        <title>Item Management</title>
        <!-- ✅ Bootstrap CSS -->
        <link
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet"
        />
      </head>
      <body class="container py-4">
        <h1 class="mb-4">Item Management</h1>
        <a
          href="<%= request.getContextPath() %>/admin-dashboard.jsp"
          class="btn btn-secondary mb-3"
        >
          Back to Dashboard
        </a>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
          <%= request.getAttribute("error") %>
        </div>
        <% } %> <% if (request.getAttribute("message") != null) { %>
        <div class="alert alert-success">
          <%= request.getAttribute("message") %>
        </div>
        <% } %>

        <!-- ✅ Items Table -->
        <table class="table table-striped table-bordered">
          <thead class="table-dark">
            <tr>
              <th>Item Code</th>
              <th>Item Name</th>
              <th>Price (Rs)</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <% if (items != null && !items.isEmpty()) { for (Item item : items)
            { %>
            <tr>
              <td><%= item.getCode() %></td>
              <td><%= item.getName() %></td>
              <td><%= String.format("%.2f", item.getPrice()) %></td>
              <td>
                <!-- Action Buttons -->
                <button
                  class="btn btn-sm btn-info"
                  data-bs-toggle="modal"
                  data-bs-target="#viewModal"
                  data-code="<%= item.getCode() %>"
                  data-name="<%= item.getName() %>"
                  data-price="<%= item.getPrice() %>"
                >
                  View
                </button>
                <button
                  class="btn btn-sm btn-warning"
                  data-bs-toggle="modal"
                  data-bs-target="#updateModal"
                  data-code="<%= item.getCode() %>"
                  data-name="<%= item.getName() %>"
                  data-price="<%= item.getPrice() %>"
                >
                  Update
                </button>
                <button
                  class="btn btn-sm btn-danger"
                  data-bs-toggle="modal"
                  data-bs-target="#deleteModal"
                  data-code="<%= item.getCode() %>"
                >
                  Delete
                </button>
              </td>
            </tr>
            <% } } else { %>
            <tr>
              <td colspan="4" class="text-center">No items available.</td>
            </tr>
            <% } %>
          </tbody>
        </table>

        <!-- ✅ Add New Item -->
        <button
          class="btn btn-success mb-4"
          data-bs-toggle="modal"
          data-bs-target="#addModal"
        >
          + Add New Item
        </button>

        <!-- ================= Modals ================= -->

        <!-- View Modal -->
        <div class="modal fade" id="viewModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Item Details</h5>
              </div>
              <div class="modal-body">
                <p><strong>Code:</strong> <span id="viewCode"></span></p>
                <p><strong>Name:</strong> <span id="viewName"></span></p>
                <p><strong>Price:</strong> Rs. <span id="viewPrice"></span></p>
              </div>
            </div>
          </div>
        </div>

        <!-- Add Modal -->
        <div class="modal fade" id="addModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Add Item</h5>
              </div>
              <div class="modal-body">
                <form
                  action="<%= request.getContextPath() %>/item"
                  method="post"
                >
                  <input type="hidden" name="action" value="add" />
                  <label class="form-label">Name</label>
                  <input
                    type="text"
                    name="name"
                    class="form-control"
                    required
                  />
                  <label class="form-label mt-2">Price</label>
                  <input
                    type="number"
                    step="0.01"
                    name="price"
                    class="form-control"
                    required
                  />
                  <label class="form-label mt-2">Shelf Default</label>
                  <input
                    type="number"
                    name="shelfDefault"
                    class="form-control"
                    required
                  />
                  <label class="form-label mt-2">Quantity</label>
                  <input
                    type="number"
                    name="quantity"
                    class="form-control"
                    required
                  />
                  <label class="form-label mt-2">Expiry Date</label>
                  <input
                    type="date"
                    name="expiry"
                    class="form-control"
                    required
                  />
                  <button type="submit" class="btn btn-success mt-3">
                    Add
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>

        <!-- Update Modal -->
        <div class="modal fade" id="updateModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Update Item</h5>
              </div>
              <div class="modal-body">
                <form
                  action="<%= request.getContextPath() %>/item"
                  method="post"
                >
                  <input type="hidden" name="action" value="update" />
                  <label class="form-label">Code</label>
                  <input
                    type="text"
                    id="updateCode"
                    name="code"
                    class="form-control"
                    readonly
                  />
                  <label class="form-label mt-2">Name</label>
                  <input
                    type="text"
                    id="updateName"
                    name="name"
                    class="form-control"
                    required
                  />
                  <label class="form-label mt-2">Price</label>
                  <input
                    type="number"
                    step="0.01"
                    id="updatePrice"
                    name="price"
                    class="form-control"
                    required
                  />
                  <button type="submit" class="btn btn-warning mt-3">
                    Update
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>

        <!-- Delete Modal -->
        <div class="modal fade" id="deleteModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Delete Item</h5>
              </div>
              <div class="modal-body">
                <form
                  action="<%= request.getContextPath() %>/item"
                  method="post"
                >
                  <input type="hidden" name="action" value="delete" />
                  <p>
                    Are you sure you want to delete
                    <strong id="deleteItemCode"></strong>?
                  </p>
                  <input type="hidden" id="deleteCode" name="code" />
                  <button type="submit" class="btn btn-danger">
                    Yes, Delete
                  </button>
                  <button
                    type="button"
                    class="btn btn-secondary"
                    data-bs-dismiss="modal"
                  >
                    Cancel
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>

        <!-- ✅ Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

        <!-- ✅ JS to Fill Modals -->
        <script>
          // View Modal
          var viewModal = document.getElementById("viewModal");
          viewModal.addEventListener("show.bs.modal", function (event) {
            var button = event.relatedTarget;
            document.getElementById("viewCode").innerText =
              button.getAttribute("data-code");
            document.getElementById("viewName").innerText =
              button.getAttribute("data-name");
            document.getElementById("viewPrice").innerText =
              button.getAttribute("data-price");
          });

          // Update Modal
          var updateModal = document.getElementById("updateModal");
          updateModal.addEventListener("show.bs.modal", function (event) {
            var button = event.relatedTarget;
            document.getElementById("updateCode").value =
              button.getAttribute("data-code");
            document.getElementById("updateName").value =
              button.getAttribute("data-name");
            document.getElementById("updatePrice").value =
              button.getAttribute("data-price");
          });

          // Delete Modal
          var deleteModal = document.getElementById("deleteModal");
          deleteModal.addEventListener("show.bs.modal", function (event) {
            var button = event.relatedTarget;
            document.getElementById("deleteItemCode").innerText =
              button.getAttribute("data-code");
            document.getElementById("deleteCode").value =
              button.getAttribute("data-code");
          });
        </script>
      </body>
    </html>
  </Item></Item
>
