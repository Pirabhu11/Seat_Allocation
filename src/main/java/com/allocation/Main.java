package com.allocation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/views/login_view.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Cannot find login_view.fxml.");
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        
        primaryStage.setTitle("Management Portal - Login");
        primaryStage.setScene(new Scene(root, 350, 450));
        primaryStage.setResizable(false); // Lock size for login screen
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
