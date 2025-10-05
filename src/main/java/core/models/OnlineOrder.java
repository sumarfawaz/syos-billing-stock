package core.models;

import java.util.*;

public class OnlineOrder {
    private int orderId;
    private int customerId;
    private double totalAmount;
    private Date orderDate;
    private String status;
    private List<OnlineOrderItem> items = new ArrayList<>();

    // 🔹 Constructors
    public OnlineOrder() {}

    public OnlineOrder(int customerId, double totalAmount, Date orderDate, String status) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public OnlineOrder(int orderId, int customerId, double totalAmount, Date orderDate, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    // 🔹 Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OnlineOrderItem> getItems() {
        return items;
    }

    public void setItems(List<OnlineOrderItem> items) {
        this.items = items;
    }

    // 🔹 Helper Methods
    public void addItem(OnlineOrderItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(OnlineOrderItem item) {
        this.items.remove(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(OnlineOrderItem::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        return "OnlineOrder{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }
}
