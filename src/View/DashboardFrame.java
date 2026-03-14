package View;

import Model.Product;
import DAO.ProductDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import static DAO.ProductDAO.addProduct;
import static DAO.ProductDAO.updateProduct;

public class DashboardFrame extends JFrame {
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JLabel totalProductsLabel;
    private JLabel totalValueLabel;

    public DashboardFrame() {
        setTitle("Dashboard - Gestionnaire de Stock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("C:\\Users\\elyes\\Downloads\\5164023.PNG").getImage());

        initUI();
        loadProducts();
    }

    private void initUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("GESTIONNAIRE DE STOCK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.setBackground(new Color(0, 209, 255, 190));
        logoutBtn.addActionListener(e -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
        });

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(logoutBtn, BorderLayout.EAST);

        // Statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));

        totalProductsLabel = new JLabel("0", SwingConstants.CENTER);
        totalProductsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalProductsLabel.setForeground(Color.BLUE);

        totalValueLabel = new JLabel("0.00 DT", SwingConstants.CENTER);
        totalValueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalValueLabel.setForeground(Color.GREEN.darker());

        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.add(new JLabel("Produits en stock:", SwingConstants.CENTER), BorderLayout.NORTH);
        productsPanel.add(totalProductsLabel, BorderLayout.CENTER);

        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.add(new JLabel("Valeur totale:", SwingConstants.CENTER), BorderLayout.NORTH);
        valuePanel.add(totalValueLabel, BorderLayout.CENTER);

        statsPanel.add(productsPanel);
        statsPanel.add(valuePanel);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche"));

        searchField = new JTextField(20);
        categoryCombo = new JComboBox<>();
        loadCategories();

        JButton searchBtn = new JButton("Rechercher");
        searchBtn.addActionListener(e -> searchProducts());

        JButton viewAllBtn = new JButton("Voir tous");
        viewAllBtn.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            loadProducts();
        });

        searchPanel.add(new JLabel("Texte:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Catégorie:"));
        searchPanel.add(categoryCombo);
        searchPanel.add(searchBtn);
        searchPanel.add(viewAllBtn);

        // Table
        String[] columns = {"ID", "Nom", "Catégorie", "Quantité", "Prix (DT)","Description", "Valeur (DT)", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productsTable = new JTable(tableModel);
        productsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des produits"));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton addBtn = new JButton("Ajouter");
        addBtn.addActionListener(e -> showAddDialog());
        addBtn.setBackground(new Color(0, 209, 255, 190));

        JButton editBtn = new JButton("Modifier");
        editBtn.addActionListener(e -> showEditDialog());
        editBtn.setBackground(new Color(0, 209, 255, 190));

        JButton deleteBtn = new JButton("Supprimer");
        deleteBtn.addActionListener(e -> deleteProduct());
        deleteBtn.setBackground(new Color(255, 11, 11, 190));

        JButton refreshBtn = new JButton("Actualiser");
        refreshBtn.addActionListener(e -> loadProducts());
        refreshBtn.setBackground(new Color(255, 255, 255, 213));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Layout
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(statsPanel, BorderLayout.CENTER);
        northPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        List<String> categories = ProductDAO.getAllCategories();
        for (String cat : categories) {
            categoryCombo.addItem(cat);
        }
    }

    private void loadProducts() {
        // Load in background to avoid UI freeze
        loadCategories();
        new SwingWorker<Void, Void>() {
            List<Product> products;

            @Override
            protected Void doInBackground() {
                products = ProductDAO.getAllProducts();
                return null;
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);
                if (products != null) {
                    for (Product p : products) {
                        tableModel.addRow(p.toTableRow());
                    }
                    updateStats();
                }
            }
        }.execute();
    }

    private void updateStats() {
        new SwingWorker<Void, Void>() {
            int totalProducts;
            double totalValue;

            @Override
            protected Void doInBackground() {
                totalProducts = ProductDAO.getTotalProductsCount();
                totalValue = ProductDAO.getTotalStockValue();
                return null;
            }

            @Override
            protected void done() {
                totalProductsLabel.setText(String.valueOf(totalProducts));
                totalValueLabel.setText(String.format("%.2f DT", totalValue));
            }
        }.execute();
    }

    private void searchProducts() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryCombo.getSelectedItem();

        new SwingWorker<Void, Void>() {
            List<Product> allProducts;
            List<Product> filtered = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                allProducts = ProductDAO.getAllProducts();
                for (Product p : allProducts) {
                    String id=p.toStringId();
                    boolean matchesSearch = searchText.isEmpty() ||
                            p.getName().toLowerCase().contains(searchText) || id.contains(searchText) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchText));

                    boolean matchesCategory = selectedCategory.equals("Toutes") ||
                            p.getCategory().equals(selectedCategory);

                    if (matchesSearch && matchesCategory) {
                        filtered.add(p);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);
                for (Product p : filtered) {
                    tableModel.addRow(p.toTableRow());
                }
            }
        }.execute();
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField descriptionField = new JTextField();

        Object[] message = {
                "Nom:", nameField,
                "Catégorie:", categoryField,
                "Quantité:", quantityField,
                "Prix (DT):", priceField,
                "Description" , descriptionField
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Ajouter produit", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Product p = new Product();
                p.setName(nameField.getText());
                p.setCategory(categoryField.getText());
                p.setQuantity(Integer.parseInt(quantityField.getText()));
                p.setDescription(descriptionField.getText());
                p.setPrice(Double.parseDouble(priceField.getText()));
                // Add to table (in-memory for now)
                List<Product> products = ProductDAO.getAllProducts();
                p.setId(products.size() + 1);
                tableModel.addRow(p.toTableRow());
                loadCategories();
                updateStats();
                addProduct(p);
                JOptionPane.showMessageDialog(this, "Produit ajouté!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: données invalides!");
            }
        }
    }

    private void showEditDialog() {
        int row = productsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un produit!");
            return;
        }

        // Get current values
        String name = tableModel.getValueAt(row, 1).toString();
        String category = tableModel.getValueAt(row, 2).toString();
        String quantity = tableModel.getValueAt(row, 3).toString();
        String price = tableModel.getValueAt(row, 4).toString().replace(" DT", "");
        String description = tableModel.getValueAt(row, 5).toString();
        int id= (int) tableModel.getValueAt(row,0);

        JTextField nameField = new JTextField(name);
        JTextField categoryField = new JTextField(category);
        JTextField quantityField = new JTextField(quantity);
        JTextField priceField = new JTextField(price);
        JTextField descriptionField = new JTextField(description);

        Object[] message = {
                "Nom:", nameField,
                "Catégorie:", categoryField,
                "Quantité:", quantityField,
                "Prix (DT):", priceField,
                "Description" , descriptionField
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Modifier produit", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                tableModel.setValueAt(nameField.getText(), row, 1);
                tableModel.setValueAt(categoryField.getText(), row, 2);
                tableModel.setValueAt(quantityField.getText(), row, 3);
                tableModel.setValueAt(priceField.getText() + " DT", row, 4);
                tableModel.setValueAt(descriptionField.getText(), row, 5);
                Product p = new Product();
                p.setId(id);
                p.setName(nameField.getText());
                p.setCategory(categoryField.getText());
                p.setDescription(descriptionField.getText());
                p.setQuantity(Integer.parseInt(quantityField.getText()));
                p.setPrice(Double.parseDouble(priceField.getText()));

                // Update status
                int qty = Integer.parseInt(quantityField.getText());
                String status = qty < 5 ? "⚠️ Rupture" : qty < 10 ? "📉 Faible" : "✅ Disponible";
                tableModel.setValueAt(status, row, 7);
                updateStats();
                updateProduct(p);
                JOptionPane.showMessageDialog(this, "Produit modifié!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: données invalides!");
            }
        }
    }

    private void deleteProduct() {
        int row = productsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un produit!");
            return;
        }

        String name = tableModel.getValueAt(row, 1).toString();
        int id= (int) tableModel.getValueAt(row,0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer " + name + "?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(row);
            updateStats();
            JOptionPane.showMessageDialog(this, "Produit supprimé!");
            ProductDAO.deleteProduct(id);
        }
    }
}