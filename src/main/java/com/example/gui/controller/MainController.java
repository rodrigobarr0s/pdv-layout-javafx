package com.example.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.example.model.services.CategoryService;
import com.example.model.services.ProductService;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {

    @FXML
    private Label lblDateTime;

    @FXML
    private Label lblOperator;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private MenuItem menuItemCategory;

    @FXML
    private MenuItem menuItemVendas;

    @FXML
    public void onMenuItemCategoryAction() {
        loadView("/gui/CategoryView.fxml", (CategoryController controller) -> {
            controller.setService(new CategoryService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemVendasAction() {
        loadBorderPaneView("/gui/PDVView.fxml", (PDVController controller) -> {
            controller.setProductService(new ProductService());
            controller.updateTableView();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeVisuals();
    }

    private synchronized <T> void loadView(String path, Consumer<T> consumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            VBox newVBox = loader.load();

            VBox mainVBox = (VBox) scrollPane.getContent();

            Node mainMenu = mainVBox.getChildren().get(0);
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainMenu);
            mainVBox.getChildren().addAll(newVBox.getChildren());

            consumer.accept(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized <T> void loadBorderPaneView(String path, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            controllerConsumer.accept(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeVisuals() {
        lblOperator.setText("Administrador");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            String formattedDateTime = LocalDateTime.now().format(formatter);
            lblDateTime.setText(formattedDateTime);
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        
    }

}
