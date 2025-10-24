package com.example.gui.controller;

import com.example.gui.callback.OnPaymentConfirmed;
import com.example.gui.util.Utils;
import com.example.model.entities.PaymentItem;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

public class PaymentFormController {

    @FXML
    private Label lblTotalAmount;
    @FXML
    private TextField txtDiscount;
    @FXML
    private TextField txtAddition;
    @FXML
    private Button btnAddPayment;
    @FXML
    private Button btnConfirmPayment;
    @FXML
    private Label lblAdjustedTotal;
    @FXML
    private TableView<PaymentItem> tablePayments;
    @FXML
    private TableColumn<PaymentItem, String> colMethod;
    @FXML
    private TableColumn<PaymentItem, Double> colAmount;
    @FXML
    private Label lblTotalReceived;
    @FXML
    private Label lblChange;

    private double totalOriginal;
    private double adjustedTotalValue = 0.0;
    private OnPaymentConfirmed onPaymentConfirmed;

    private final ObservableList<PaymentItem> paymentItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        txtDiscount.setOnAction(e -> recalculateAdjustedTotal());
        txtAddition.setOnAction(e -> recalculateAdjustedTotal());

        txtDiscount.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal)
                recalculateAdjustedTotal(); // perdeu o foco
        });

        txtAddition.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal)
                recalculateAdjustedTotal(); // perdeu o foco
        });
        configureEnterNavigation();
    }

    public void setTotal(double total) {
        this.totalOriginal = total;
        lblTotalAmount.setText(Utils.formatCurrencyBR(total, 2));
        recalculateAdjustedTotal();
        setupTable();
    }

    public void setOnPaymentConfirmed(OnPaymentConfirmed callback) {
        this.onPaymentConfirmed = callback;
    }

    private void setupTable() {
        colMethod.setCellValueFactory(data -> data.getValue().methodProperty());
        colMethod.setCellFactory(
                ComboBoxTableCell.forTableColumn("Dinheiro", "Cartão de Crédito", "Cartão de Débito", "PIX"));

        colAmount.setCellValueFactory(data -> data.getValue().amountProperty().asObject());
        colAmount.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return Utils.formatCurrencyBR(value, 2);
            }

            @Override
            public Double fromString(String s) {
                try {
                    return Double.parseDouble(s.replace(",", "."));
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }));

        colAmount.setOnEditCommit(event -> {
            event.getRowValue().setAmount(event.getNewValue());
            updateTotals();
        });

        tablePayments.setItems(paymentItems);
        tablePayments.setEditable(true);

        tablePayments.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                PaymentItem selectedItem = tablePayments.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    paymentItems.remove(selectedItem);
                    updateTotals();
                }
            }
        });

    }

    @FXML
    private void onAddPaymentMethod() {
        double totalRecebido = paymentItems.stream().mapToDouble(PaymentItem::getAmount).sum();
        double saldoRestante = adjustedTotalValue - totalRecebido;

        if (saldoRestante <= 0) {
            showAlert("Pagamento completo", "Não há saldo restante para adicionar outra forma de pagamento.");
            return;
        }

        List<String> opcoes = List.of("Dinheiro", "Cartão de Crédito", "Cartão de Débito", "PIX");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Dinheiro", opcoes);
        dialog.setTitle("Selecionar Forma de Pagamento");
        dialog.setHeaderText("Escolha uma forma de pagamento");
        dialog.setContentText("Forma:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(metodo -> {
            boolean jaAdicionado = paymentItems.stream()
                    .anyMatch(item -> item.getMethod().equalsIgnoreCase(metodo));

            if (jaAdicionado) {
                showAlert("Forma já adicionada", "Você já adicionou essa forma de pagamento.");
                return;
            }

            double valor;
            if ("Dinheiro".equals(metodo)) {
                TextInputDialog valorDialog = new TextInputDialog(String.valueOf(saldoRestante));
                valorDialog.setTitle("Valor em Dinheiro");
                valorDialog.setHeaderText("Informe o valor recebido em dinheiro");
                valorDialog.setContentText("Valor:");

                Optional<String> valorInput = valorDialog.showAndWait();
                if (valorInput.isEmpty())
                    return; // usuário cancelou ou fechou
                valor = parseValue(valorInput.get(), false);

            } else {
                valor = saldoRestante;
            }

            paymentItems.add(new PaymentItem(metodo, valor));
            updateTotals();
        });
    }

    @FXML
    private void recalculateAdjustedTotal() {
        String descontoInput = txtDiscount.getText();
        String acrescimoInput = txtAddition.getText();

        double discount = parseValue(descontoInput, true);
        double addition = parseValue(acrescimoInput, false);

        // Limpa campo se valor for inválido
        if (!descontoInput.isBlank() && discount == 0)
            txtDiscount.clear();

        if (!acrescimoInput.isBlank() && addition == 0)
            txtAddition.clear();

        adjustedTotalValue = totalOriginal - discount + addition;
        lblAdjustedTotal.setText(Utils.formatCurrencyBR(adjustedTotalValue, 2));
        updateTotals();
    }

    private double parseValue(String input, boolean isDiscount) {
        if (input == null || input.isBlank())
            return 0;

        boolean isPercent = input.contains("%");
        input = input.replace("%", "").replace(",", ".").trim();

        try {
            double value = Double.parseDouble(input);

            if (value < 0)
                return 0;

            if (isPercent) {
                if (isDiscount && value > 100)
                    return 0;
                return totalOriginal * value / 100.0;
            } else {
                return isDiscount && value > totalOriginal ? 0 : value;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateTotals() {
        double totalReceived = paymentItems.stream().mapToDouble(PaymentItem::getAmount).sum();
        lblTotalReceived.setText(Utils.formatCurrencyBR(totalReceived, 2));

        double change = totalReceived - adjustedTotalValue;
        if (change < 0)
            change = 0;
        lblChange.setText(Utils.formatCurrencyBR(change, 2));

        // Foca no botão se não houver nenhuma forma de pagamento
        if (paymentItems.isEmpty()) {
            Platform.runLater(() -> btnAddPayment.requestFocus());
        }
    }

    @FXML
    private void onConfirmPayment() {
        double totalReceived = paymentItems.stream().mapToDouble(PaymentItem::getAmount).sum();

        if (totalReceived < adjustedTotalValue) {
            showAlert("Valor insuficiente", "O total recebido é menor que o total ajustado.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar Pagamento");
        confirmDialog.setHeaderText("Deseja realmente confirmar o pagamento?");
        confirmDialog.setContentText("Essa ação finalizará a venda.");

        ButtonType btnSim = new ButtonType("Sim", ButtonBar.ButtonData.YES);
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.NO);
        confirmDialog.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == btnSim) {
            showAlert("Pagamento confirmado", "Venda finalizada com sucesso!");
            if (onPaymentConfirmed != null) {
                onPaymentConfirmed.focusOnProductSearch();
            }
            ((Stage) lblTotalAmount.getScene().getWindow()).close();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void configureEnterNavigation() {
        txtDiscount.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                txtAddition.requestFocus();
            }
        });

        txtAddition.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAddPayment.requestFocus();
            }
        });

        btnAddPayment.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAddPayment.fire();

                Platform.runLater(() -> {
                    double totalRecebido = paymentItems.stream().mapToDouble(PaymentItem::getAmount).sum();
                    if (totalRecebido >= adjustedTotalValue) {
                        btnConfirmPayment.requestFocus();
                    } else {
                        btnAddPayment.requestFocus(); // mantém o foco para adicionar mais
                    }
                });
            }
        });

        btnConfirmPayment.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnConfirmPayment.fire(); // dispara a ação de confirmar
            }
        });
    }

}
