package com.example.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.application.Main;
import com.example.model.entities.Category;
import com.example.model.services.CategoryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoryController implements Initializable {

    private CategoryService service;

    private ObservableList<Category> observableList;

    @FXML
    private Button buttonNovaCategoria;

    @FXML
    private TableView<Category> tableViewCategory;

    @FXML
    private TableColumn<Category, Long> tableColumnId;

    @FXML
    private TableColumn<Category, String> tableColumnName;

    @FXML
    public void onbuttonNovaCategoriaAction() {
        createDialogForm("/gui/CategoryForm.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeNodes();
    }

    public void setService(CategoryService service) {
        this.service = service;
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewCategory.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        observableList = FXCollections.observableArrayList(service.findAll());
        tableViewCategory.setItems(observableList);
    }

    private void createDialogForm( String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent parent = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nova Categoria");
            dialogStage.setScene(new Scene(parent));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
