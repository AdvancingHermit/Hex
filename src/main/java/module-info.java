module com.example.hex {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hex to javafx.fxml;
    exports com.hex;
    exports com.hex.scenes;
    opens com.hex.scenes to javafx.fxml;
    exports com.hex.components;
    opens com.hex.components to javafx.fxml;
}