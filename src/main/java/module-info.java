module com.example.hex {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hex to javafx.fxml;
    exports com.example.hex;
}