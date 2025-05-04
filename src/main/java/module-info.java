module com.example.vendingmachineproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.vendingmachineproject to javafx.fxml;
    exports com.example.vendingmachineproject;
}