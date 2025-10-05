package core.models;

public class OnlineOrderItem {
    private int id;
    private String itemCode;
    private int quantity;
    private double price;

    // 🔹 Constructors
    public OnlineOrderItem() {}

    public OnlineOrderItem(String itemCode, int quantity, double price) {
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.price = price;
    }

    public OnlineOrderItem(int id, String itemCode, int quantity, double price) {
        this.id = id;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.price = price;
    }

    // 🔹 Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // 🔹 Utility Method (optional)
    public double getSubtotal() {
        return quantity * price;
    }

    @Override
    public String toString() {
        return "OnlineOrderItem{" +
                "id=" + id +
                ", itemCode='" + itemCode + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
