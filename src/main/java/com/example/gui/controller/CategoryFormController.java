package com.example.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryFormController {

    @FXML
    private TextField txtName;

    @FXML
    public void onSaveAction() {
        String name = txtName.getText();
        if (name == null || name.trim().isEmpty()) {
            // Exibir alerta ou mensagem de erro
            System.out.println("Nome da categoria é obrigatório.");
            return;
        }

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
}
