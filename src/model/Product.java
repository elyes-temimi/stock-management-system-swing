/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class Product {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String description;
    
    public Product() {}
    
    public Product(int id, String name, String category, int quantity, double price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // Calculated properties
    public double getTotalValue() {
        return quantity * price;
    }
    
    public String getStatus() {
        if (quantity <= 0) {
            return "❌ Épuisé";
        } else if (quantity < 5) {
            return "⚠️ Rupture";
        } else if (quantity < 10) {
            return "📉 Faible";
        } else {
            return "✅ Disponible";
        }
    }
    
    public Object[] toTableRow() {
        return new Object[]{
            id,
            name,
            category,
            quantity,
            String.format("%.2f DT", price),
                description,
            String.format("%.2f DT", getTotalValue()),
            getStatus()
        };
    }
    public String toStringId(){return id+"";}
    
    @Override
    public String toString() {
        return name + " (" + category + ") - " + quantity + " unités";
    }
}
