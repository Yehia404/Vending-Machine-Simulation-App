package com.example.vendingmachineproject;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.HashMap;

public class VendingMachineController {

    @FXML
    private ListView<String> productList; // To display available products
    @FXML
    private TextArea messageArea; // To display messages (e.g., errors, status updates)
    @FXML
    private TextField moneyField; // For inserting money
    @FXML
    private Button insertButton; // Button to insert money
    @FXML
    private Label changeLabel; // To show change after a purchase
    @FXML
    private Button resetButton; // Reset button to reset the machine

    // HashMaps for products, prices, and images
    private HashMap<String, Double> products; // Product names and prices
    private HashMap<String, String> productImages; // Product names and image file paths

    // Enum representing the states of the vending machine
    private enum VendingMachineState {
        IDLE,
        PRODUCT_SELECTED,
        MONEY_INSERTED,
        DISPENSING,
        CHANGE_RETURNED
    }

    private VendingMachineState currentState;
    private double insertedMoney;
    private double selectedProductPrice;

    // Initialize the vending machine in IDLE state
    @FXML
    public void initialize() {
        currentState = VendingMachineState.IDLE;
        messageArea.setText("Welcome! Please select a product.");

        // Populate product list with sample products and prices
        products = new HashMap<>();
        products.put("Cola", 1.50);
        products.put("Pepsi", 1.00);
        products.put("Galaxy", 0.75);
        products.put("Chips", 2.00);
        products.put("M&Ms", 0.50);


        // Product images associated with each product
        productImages = new HashMap<>();
        productImages.put("Cola", "/images/cola.png");
        productImages.put("Pepsi", "/images/pepsi.jpg");
        productImages.put("Galaxy", "/images/galaxy.png");
        productImages.put("Chips", "/images/chips.png");
        productImages.put("M&Ms", "/images/m&ms.png");


        // Set up the ListView with a custom cell factory
        productList.setCellFactory(param -> new ProductCell());

        // Populate the ListView with product names
        productList.getItems().addAll(products.keySet());

        // Set initial values
        insertedMoney = 0;
        selectedProductPrice = 0;

        insertButton.setDisable(true); // Disable the insert button initially
        insertButton.setOnAction(e -> onInsertMoney());
        resetButton.setOnAction(e -> resetMachine());
        productList.setOnMouseClicked(e -> onProductSelected());
    }

    // Handle product selection
    private void onProductSelected() {
        if (currentState == VendingMachineState.IDLE) {
            // Get selected product
            String selectedProduct = productList.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                // Extract the price from the selected product
                selectedProductPrice = products.get(selectedProduct);

                // Transition to PRODUCT_SELECTED state
                currentState = VendingMachineState.PRODUCT_SELECTED;
                messageArea.setText("You selected: " + selectedProduct + " - $"+ selectedProductPrice + ". Please insert money.");
                insertButton.setDisable(false); // Enable the insert button
            }
        }
    }

    // Handle money insertion
    private void onInsertMoney() {
        if (currentState == VendingMachineState.PRODUCT_SELECTED) {
            try {
                resetButton.setDisable(true);
                // Get money input and add it to the insertedMoney variable
                double money = Double.parseDouble(moneyField.getText());
                insertedMoney += money;
                moneyField.clear();

                // Transition to MONEY_INSERTED state
                currentState = VendingMachineState.MONEY_INSERTED;
                messageArea.setText("Money inserted: $" + insertedMoney);

                // Delay for 3 seconds for inserting money
                PauseTransition delay1 = new PauseTransition(Duration.seconds(3));
                delay1.setOnFinished(event -> {
                    // Check if money is enough for the selected product
                    if (insertedMoney >= selectedProductPrice) {
                        // Proceed to dispense the product
                        dispenseProduct();
                    } else {
                        messageArea.setText("Insufficient funds. Please insert more money.");
                        changeLabel.setText("$" + insertedMoney);

                        PauseTransition delay2 = new PauseTransition(Duration.seconds(3)); // 3-second delay
                        delay2.setOnFinished(e -> resetMachine()); // Reset the machine after the delay
                        delay2.play(); // Start the delay
                    }
                });
                delay1.play();
            } catch (NumberFormatException ex) {
                messageArea.setText("Please enter a valid amount.");
                resetButton.setDisable(false); // Enable the reset button
            }
        }
    }

    // Dispense the product
    private void dispenseProduct() {
        if (currentState == VendingMachineState.MONEY_INSERTED) {
            // Simulate dispensing product
            currentState = VendingMachineState.DISPENSING;
            messageArea.setText("Dispensing your product...");

            // Call the method to handle change calculation
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> handleChange());
            delay.play();
        }
    }

    // Handle the change calculation and display
    private void handleChange() {
        // Calculate change
        double change = insertedMoney - selectedProductPrice;

        // Simulate change returned
        currentState = VendingMachineState.CHANGE_RETURNED;
        messageArea.setText("Please take your product. Change: $" + change);

        // Display change
        changeLabel.setText("$" + change);

        PauseTransition delay = new PauseTransition(Duration.seconds(3)); // 3-second delay
        delay.setOnFinished(event -> resetMachine()); // Reset the machine after the delay
        delay.play(); // Start the delay
    }

    // Reset the machine to IDLE state
    private void resetMachine() {
        insertedMoney = 0;
        selectedProductPrice = 0;
        productList.getSelectionModel().clearSelection(); // Clear product selection
        insertButton.setDisable(true); // Disable the insert button again
        resetButton.setDisable(false); // Enable the reset button
        moneyField.clear(); // Clear the money field
        changeLabel.setText("$0.00"); // Reset the change label
        messageArea.setText("Welcome! Please select a product."); // Reset message
        currentState = VendingMachineState.IDLE; // Set the state to IDLE
    }

    // Custom ListCell for the ListView to display product names, prices, and images
    private class ProductCell extends ListCell<String> {

        @Override
        protected void updateItem(String productName, boolean empty) {
            super.updateItem(productName, empty);

            if (empty || productName == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Create an HBox for displaying the product
                HBox hbox = new HBox(10);
                hbox.setStyle("-fx-padding: 5;");

                // Set the image for the product
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(productImages.get(productName))));
                imageView.setFitHeight(50);  // Set a fixed height for images
                imageView.setFitWidth(50);   // Set a fixed width for images

                // Create a VBox for the product name and price
                VBox vbox = new VBox(5);
                Label productLabel = new Label(productName);
                productLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label priceLabel = new Label("$" + products.get(productName));

                vbox.getChildren().addAll(productLabel, priceLabel);

                // Add the image and the labels to the HBox
                hbox.getChildren().addAll(imageView, vbox);

                setGraphic(hbox);
            }
        }
    }
}
