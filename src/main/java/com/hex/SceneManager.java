package com.hex;

import com.hex.scenes.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
// Made by Oscar
 /**
  * Manages the different Scenes, and makes sure to properly switch between them
  */
public class SceneManager {

    private final Stage primaryStage;
    // For Online play
    private Socket currentSocket;
    private ObjectInputStream currentIn;
    private ObjectOutputStream currentOut;
    public  String serverIP;
    private double sceneHeight;
    private double sceneWidth;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.serverIP = "localhost";
        this.sceneHeight = 600;
        this.sceneWidth = 800;
    }

    public enum SceneType {
        MAIN_MENU,
        LOCAL_GAME,
        ALGO_V_ALGO,
        ALGO_V_ALGO_SETTINGS,
        ONLINE_GAME,
        LOADING_SCREEN,
        LOCAL_GAME_SETTINGS,
        ONLINE_GAME_SETTINGS
    }
    public double getSceneHeight() {
        return this.sceneHeight;
    }
    public double getSceneWidth() {
         return this.sceneWidth;
     }
    // Stores the server connection for online play
    public void storeConnection(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.currentSocket = socket;
        this.currentIn = in;
        this.currentOut = out;
    }

    public Scene createScene(SceneType type) {
        return switch (type) {
            case MAIN_MENU -> new MainMenu(this).getScene();

            case LOCAL_GAME_SETTINGS -> new LocalGameSettings(this).getScene();

            case ALGO_V_ALGO_SETTINGS -> new AlgovAlgoSettings(this).getScene();

            case ONLINE_GAME -> {
                if (currentSocket != null && currentIn != null && currentOut != null) {
                    yield new OnlineGame(this, currentSocket, currentIn, currentOut).getScene();
                } else {
                    System.err.println("Tried tp create online game with no connection details");
                    yield new MainMenu(this).getScene();
                }
            }
            case ONLINE_GAME_SETTINGS -> new OnlineGameSettings(this, serverIP).getScene();
            case LOADING_SCREEN -> new LoadingScreen(this, serverIP).getScene();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }


    public void switchScene(SceneType type) {
        Scene scene = createScene(type);
        primaryStage.setScene(scene);
    }
    public void switchScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    // Clean up resources when no longer needed
    public void clearConnection() {
        this.currentSocket = null;
        this.currentIn = null;
        this.currentOut = null;
    }
}