module com.example.hex {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hex to javafx.fxml;
    exports com.hex;
}