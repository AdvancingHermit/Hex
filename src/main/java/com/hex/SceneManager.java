package com.hex;

import com.hex.scenes.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SceneManager {
    private final Stage primaryStage;
    private Socket currentSocket;
    private ObjectInputStream currentIn;
    private ObjectOutputStream currentOut;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public enum SceneType {
        MAIN_MENU,
        LOCAL_GAME,
        ALGO_V_ALGO,
        ONLINE_GAME,
        LOADING_SCREEN
    }

    public void storeConnection(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.currentSocket = socket;
        this.currentIn = in;
        this.currentOut = out;
    }

    public Scene createScene(SceneType type) {
        return switch (type) {
            case MAIN_MENU -> new MainMenu(this).getScene();

            case LOCAL_GAME -> new LocalGame(1, false, 1, GameMode.NORMAL).getScene();

            case ALGO_V_ALGO -> new AlgovAlgo().getScene();

            case ONLINE_GAME -> {
                if (currentSocket != null && currentIn != null && currentOut != null) {
                    yield new OnlineGame(this, currentSocket, currentIn, currentOut).getScene();
                } else {
                    System.err.println("Tried tp create online game with no connection details");
                    yield new MainMenu(this).getScene();
                }
            }
            case LOADING_SCREEN -> new LoadingScreen(this).getScene();
        };
    }

    public void switchScene(SceneType type) {
        Scene scene = createScene(type);
        primaryStage.setScene(scene);
    }

    // Clean up resources when no longer needed
    public void clearConnection() {
        this.currentSocket = null;
        this.currentIn = null;
        this.currentOut = null;
    }
}