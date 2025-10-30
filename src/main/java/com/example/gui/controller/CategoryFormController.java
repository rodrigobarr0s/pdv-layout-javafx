package com.example.gui.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CategoryFormController {

    private static final String ERROR_STYLE = "-fx-border-color: #D9534F; " +
            "-fx-border-width: 1.5px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;";

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField txtName;

    @FXML
    public void onSaveAction() {
        String name = txtName.getText();
        if (name == null || name.trim().isEmpty()) {
            // Aplica borda vermelha ao campo
            shakeField(txtName);
            System.out.println("Nome da categoria é obrigatório.");
            return;
        }

        // Remove o estilo de erro se o campo estiver preenchido
        txtName.setStyle("");

        // Aqui você pode salvar no banco ou chamar um serviço
        System.out.println("Categoria salva: " + name);

        closeForm();
    }

    @FXML
    public void onCancelAction() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    private void shakeField(TextField field) {
        if (!field.getStyle().contains("#D9534F")) {
            field.setStyle(ERROR_STYLE);
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis(100), field);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }
}
