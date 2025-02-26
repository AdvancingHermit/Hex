package com.hex;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HexController {
    @FXML
    private Label menuText;

    @FXML
    protected void onHelloButtonClick() {
        menuText.setText("Welcome to our Hex Game!!!");
    }
}