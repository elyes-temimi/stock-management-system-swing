/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Get all products
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        System.out.println("Executing query: SELECT * FROM products ORDER BY name");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY name")) {

            System.out.println("Query executed successfully");

            int count = 0;
            while (rs.next()) {
                count++;
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                products.add(product);
                System.out.println("Loaded product " + count + ": " + product.getName());
            }

            System.out.println("Total products loaded: " + count);

        } catch (SQLException e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Database connection is null!");
        }
        return products;
    }



    // Add new product
    public static boolean addProduct(Product product) {
        String query = "INSERT INTO products (name, description, quantity, price, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setString(5, product.getCategory());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit: " + e.getMessage());
        }
        return false;
    }

    // Update product
    public static boolean updateProduct(Product product) {
        String query = "UPDATE products SET name = ?, description = ?, quantity = ?, price = ?, category = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setString(5, product.getCategory());
            pstmt.setInt(6, product.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du produit: " + e.getMessage());
        }
        return false;
    }

    // Delete product
    public static boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
        }
        return false;
    }



    // Get all categories
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Toutes");

        String query = "SELECT DISTINCT category FROM products ORDER BY category";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories: " + e.getMessage());
        }

        // If no categories in database, add default ones
        if (categories.size() == 1) {
            String[] defaultCategories = {
                    "Electronique", "Vêtements", "Alimentation",
                    "Médicaments", "Bureau", "Meubles", "Jardin"
            };
            for (String cat : defaultCategories) {
                categories.add(cat);
            }
        }

        return categories;
    }

    // Get total stock value
    public static double getTotalStockValue() {
        String query = "SELECT SUM(quantity * price) as total FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul de la valeur totale: " + e.getMessage());
        }
        return 0;
    }

    // Get total products count
    public static int getTotalProductsCount() {
        String query = "SELECT SUM(quantity) as total FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du nombre total: " + e.getMessage());
        }
        return 0;
    }

    // Authenticate user
    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User authenticated: " + username);
                return true;
            } else {
                System.out.println("Authentication failed for: " + username);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            // Fallback to demo credentials if database fails
            return username.equals("admin") && password.equals("admin123");
        }
    }

    public static String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            System.err.println("Error getting user role: " + e.getMessage());
        }

        return "user";
    }



}
