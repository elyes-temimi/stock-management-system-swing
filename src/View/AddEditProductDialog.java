package View;

import DAO.ProductDAO;
import Model.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddEditProductDialog extends JDialog {
    private Product product;
    private boolean saved = false;

    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JSpinner quantitySpinner;
    private JFormattedTextField priceField;
    private JTextArea descriptionArea;

    public AddEditProductDialog(JFrame parent, Product product) {
        super(parent, product == null ? "Ajouter un produit" : "Modifier le produit", true);
        this.product = product;

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        if (product != null) {
            loadProductData();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel(
                product == null ? "Ajouter un nouveau produit" : "Modifier le produit",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Nom du produit *:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        nameField = new JTextField(30);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(nameField, gbc);

        // Category field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel categoryLabel = new JLabel("Catégorie *:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        categoryField = new JComboBox<>();
        categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadCategories();
        formPanel.add(categoryField, gbc);

        // Quantity field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel quantityLabel = new JLabel("Quantité *:");
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        SpinnerNumberModel quantityModel = new SpinnerNumberModel(0, 0, 9999, 1);
        quantitySpinner = new JSpinner(quantityModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(quantitySpinner, "#");
        quantitySpinner.setEditor(editor);
        quantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(quantitySpinner, gbc);

        // Price field
        gbc.gridx = 2;
        JLabel priceLabel = new JLabel("Prix (DT) *:");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 3;
        priceField = new JFormattedTextField(java.text.NumberFormat.getNumberInstance());
        priceField.setColumns(10);
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(priceField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton saveButton = new JButton("Enregistrer");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saveProduct();
                saved = true;
                dispose();
            }
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set focus to name field
        nameField.requestFocus();
    }

    private void loadCategories() {
        List<String> categories = ProductDAO.getAllCategories();
        // Remove "Toutes" option
        for (String category : categories) {
            if (!category.equals("Toutes")) {
                categoryField.addItem(category);
            }
        }

        // Add option to create new category
        categoryField.addItem("-- Nouvelle catégorie --");

        if (categoryField.getItemCount() > 0) {
            categoryField.setSelectedIndex(0);
        }
    }

    private void loadProductData() {
        if (product != null) {
            nameField.setText(product.getName());
            categoryField.setSelectedItem(product.getCategory());
            quantitySpinner.setValue(product.getQuantity());
            priceField.setValue(product.getPrice());
            descriptionArea.setText(product.getDescription());
        }
    }

    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            showError("Le nom du produit est obligatoire");
            nameField.requestFocus();
            return false;
        }

        // Validate category
        if (categoryField.getSelectedItem() == null ||
                categoryField.getSelectedItem().equals("-- Nouvelle catégorie --")) {

            String newCategory = JOptionPane.showInputDialog(this,
                    "Veuillez entrer le nom de la nouvelle catégorie:",
                    "Nouvelle catégorie",
                    JOptionPane.QUESTION_MESSAGE);

            if (newCategory == null || newCategory.trim().isEmpty()) {
                showError("La catégorie est obligatoire");
                return false;
            }

            categoryField.addItem(newCategory.trim());
            categoryField.setSelectedItem(newCategory.trim());
        }

        // Validate price
        try {
            double price = Double.parseDouble(priceField.getText().replace(",", "."));
            if (price <= 0) {
                showError("Le prix doit être supérieur à 0");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Le prix doit être un nombre valide");
            priceField.requestFocus();
            return false;
        }

        return true;
    }

    private void saveProduct() {
        String name = nameField.getText().trim();
        String category = (String) categoryField.getSelectedItem();
        int quantity = (int) quantitySpinner.getValue();
        double price;

        try {
            price = Double.parseDouble(priceField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            price = 0;
        }

        String description = descriptionArea.getText().trim();

        if (product == null) {
            // New product
            product = new Product();
            product.setName(name);
            product.setCategory(category);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setDescription(description);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return ProductDAO.addProduct(product);
                }
            }.execute();

        } else {
            // Update existing product
            product.setName(name);
            product.setCategory(category);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setDescription(description);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return ProductDAO.updateProduct(product);
                }
            }.execute();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE);
    }

}