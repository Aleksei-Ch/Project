package com.maleshen.ssm.capp;

import com.maleshen.ssm.entity.User;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    public static Stage primaryStage;
    public static User currentUser;
    public static ObservableList<User> contactList;


    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientApp.primaryStage = primaryStage;
        authInit();
    }

    private void authInit() {
        //Set some window properties
        primaryStage.setTitle("Simple & Safety");
        primaryStage.setResizable(false);

        //Show Auth Window
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/AuthRegScene.fxml"));
            Parent parent = loader.load();

            primaryStage.setScene(new Scene(parent));
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
