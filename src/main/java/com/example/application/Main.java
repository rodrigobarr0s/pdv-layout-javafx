package com.example.application;

import com.example.config.DataSeed;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene mainScene;

    @Override
    public void start(Stage stage) throws Exception {
        DataSeed.seed();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
        BorderPane borderPane = loader.load();

        mainScene = new Scene(borderPane);
        mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("JavaFX JPA");
        stage.setScene(mainScene);
        stage.show();
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static void main(String[] args) {
        launch(args);
    }

}