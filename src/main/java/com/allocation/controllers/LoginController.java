package com.allocation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple hardcoded security for now
        if ("admin".equals(username) && "admin".equals(password)) {
            try {
                // Load the Main Dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_view.fxml"));
                Parent root = loader.load();
                
                // Get the current window (stage) and change the scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 700));
                stage.setTitle("Classroom Seat Allocation System - Dashboard");
                stage.centerOnScreen();
                
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error loading dashboard!");
            }
        } else {
            // Show error message
            errorLabel.setText("Invalid Username or Password!");
        }
    }
}
