package com.hex.scenes;

import com.hex.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.util.Duration;
import com.hex.SceneManager.SceneType;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoadingScreen extends BaseScene {
    private static final int SERVER_PORT = 5917;

    private String serverIP;
    private Label statusLabel;
    private ProgressIndicator progressIndicator;
    private SceneManager sceneManager;
    private Timeline statusTextAnim;
    private VBox content;

    public LoadingScreen(SceneManager sceneManager, String serverIP) {
        this.sceneManager = sceneManager;
        this.serverIP = serverIP;
        System.out.println(serverIP);
        drawUI();
        startGameSearch();
    }

    private void drawUI() {
        StackPane root = new StackPane();
        content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        statusLabel = new Label("Looking for an opponent...");

        statusTextAnim = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> statusLabel.setText("Looking for an opponent.")),
                new KeyFrame(Duration.seconds(1.0), e -> statusLabel.setText("Looking for an opponent..")),
                new KeyFrame(Duration.seconds(1.5), e -> statusLabel.setText("Looking for an opponent..."))
        );
        statusTextAnim.setCycleCount(Timeline.INDEFINITE);
        statusTextAnim.play();

        progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #1e1f22;");

        content.getChildren().addAll(progressIndicator, statusLabel);
        root.getChildren().add(content);

        scene = new Scene(root, sceneManager.getSceneWidth(), sceneManager.getSceneHeight());
    }

    public void updateStatus(String status) {
        Platform.runLater(() -> {
            statusTextAnim.stop();
            statusLabel.setText(status);
        });
    }

    public void startGameSearch() {
        new Thread(() -> {
            try {
                Socket socket = new Socket(serverIP, SERVER_PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                updateStatus("Connected! Waiting for opponent...");

                sceneManager.storeConnection(socket, in, out);

                Platform.runLater(() -> {
                    sceneManager.switchScene(SceneType.ONLINE_GAME);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(this::failedGameSearch);
            }
        }).start();
    }

    private void failedGameSearch() {
        content.getChildren().remove(progressIndicator);
        updateStatus("Connection failed. Please try again.");
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> sceneManager.switchScene(SceneType.ONLINE_GAME_SETTINGS));
        content.getChildren().add(backButton);
        Button retyButton = new Button("Retry");
        retyButton.setOnAction(event -> retry());
        content.getChildren().add(retyButton);
    }

    private void retry() {
        content.getChildren().clear();
        progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #1e1f22;");
        statusLabel = new Label("Looking for an opponent...");
        content.getChildren().addAll(progressIndicator, statusLabel);
        statusTextAnim.play();
        startGameSearch();
    }
}