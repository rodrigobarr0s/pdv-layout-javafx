package com.example.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import com.example.gui.util.Utils;
import com.example.gui.viewmodels.OrderViewModel;
import com.example.model.entities.Order;
import com.example.model.entities.OrderItem;
import com.example.model.entities.Product;
import com.example.model.services.ProductService;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class PDVController implements Initializable {

    private ProductService productService;

    private ObservableList<Product> masterProductList;
    private FilteredList<Product> filteredProducts;
    private OrderViewModel orderVM = new OrderViewModel(new Order(null, Instant.now(), null));

    @FXML
    private Label lblDateTime;
    @FXML
    private TextField txtSearchProduct;
    @FXML
    private TableView<Product> tableProducts;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableView<OrderItem> tableCart;
    @FXML
    private TableColumn<OrderItem, String> colCartProduct;
    @FXML
    private TableColumn<OrderItem, Integer> colCartQuantity;
    @FXML
    private TableColumn<OrderItem, Double> colCartSubtotal;

    @FXML
    private Label lblTotal;
    @FXML
    private Label lblOperator;

    public PDVController() {
        // Construtor padrão
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeNodes();
        setupSearchFilter();
        setupCartShortcuts();
        setupGlobalShortcuts();
        initializeVisuals();
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @FXML
    private void onAddToCart() {
        Product selectedProduct = tableProducts.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            for (OrderItem item : orderVM.getObservableItems()) {
                if (item.getProduct().equals(selectedProduct)) {
                    item.setQuantity(item.getQuantity() + 1);
                    tableCart.refresh();
                    updateTotal();
                    return;
                }
            }

            OrderItem newItem = new OrderItem(orderVM.getOrder(), selectedProduct, 1, selectedProduct.getPrice());
            orderVM.getObservableItems().add(newItem);
            updateTotal();
        }
    }

    @FXML
    private void onCheckout() {
        if (orderVM.getObservableItems().isEmpty()) {
            showAlert("Carrinho vazio", "Adicione produtos antes de finalizar a venda.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PaymentForm.fxml"));
            Parent root = loader.load();

            // Passa o total para o formulário
            PaymentFormController controller = loader.getController();
            orderVM.syncToEntity(); // sincroniza itens observáveis com a entidade
            controller.setTotal(orderVM.getOrder().getTotal());

            controller.setOnPaymentConfirmed(() -> {
                orderVM.getObservableItems().clear();
                updateTotal();
                orderVM = new OrderViewModel(new Order(null, Instant.now(), null));

                txtSearchProduct.clear();
                txtSearchProduct.requestFocus();
            });

            Stage stage = new Stage();
            stage.setTitle("Pagamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível abrir o formulário de pagamento.");
        }
    }

    private void updateTotal() {
        double total = orderVM.getOrder().getTotal();
        lblTotal.setText(Utils.formatCurrencyBR(total, 2));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void onSearchProduct(KeyEvent event) {
        String filter = txtSearchProduct.getText().toLowerCase().trim();
        filteredProducts.setPredicate(product -> {
            if (filter.isEmpty())
                return true;
            return product.getName().toLowerCase().contains(filter);
        });

        // Seleciona automaticamente o primeiro item filtrado
        if (!filteredProducts.isEmpty()) {
            tableProducts.getSelectionModel().select(filteredProducts.get(0));
        }
    }

    private void initializeNodes() {
        // Configuração das colunas da tabela de produtos
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        Utils.formatTableColumnCurrencyBR(colPrice, 2);

        // Configuração das colunas da tabela do carrinho
        colCartProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        Utils.formatTableColumnCurrencyBR(colCartSubtotal, 2);

        setupCartShortcuts();

        // Vincula a lista de itens ao TableView
        tableCart.setItems(orderVM.getObservableItems());
        tableCart.setEditable(true);
        colCartQuantity.setCellFactory(column -> new TextFieldTableCell<OrderItem, Integer>(
                new StringConverter<>() {
                    @Override
                    public String toString(Integer object) {
                        return object != null ? object.toString() : "";
                    }

                    @Override
                    public Integer fromString(String string) {
                        try {
                            return Integer.parseInt(string.trim());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                }) {
            @Override
            public void commitEdit(Integer newValue) {
                if (!isEditing()) {
                    super.commitEdit(newValue);
                } else {
                    TableView<OrderItem> table = getTableView();
                    if (table != null) {
                        int row = getIndex();
                        OrderItem item = table.getItems().get(row);
                        item.setQuantity(newValue);
                        table.refresh();
                        updateTotal();
                    }
                    super.commitEdit(newValue);
                }
            }
        });

        colCartQuantity.setOnEditCommit(event -> {
            OrderItem item = event.getRowValue();
            Integer newQuantity = event.getNewValue();

            if (newQuantity == null)
                return;

            if (newQuantity <= 0) {
                orderVM.getObservableItems().remove(item); // Remove item se for zero ou negativo
            } else {
                item.setQuantity(newQuantity);
            }

            tableCart.refresh();
            updateTotal();
            tableCart.requestFocus();
        });

        // Se pressionar Enter, adiciona o produto selecionado
        tableProducts.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    Product selectedProduct = tableProducts.getSelectionModel().getSelectedItem();
                    if (selectedProduct != null) {
                        onAddToCart();
                    }
                    break;
                default:
                    break;
            }
        });

    }

    public void updateTableView() {
        if (productService == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Product> listSorted = productService.findAll();
        listSorted.sort(Comparator.comparing(Product::getName));

        masterProductList = FXCollections.observableArrayList(listSorted);
        filteredProducts = new FilteredList<>(masterProductList, p -> true);
        tableProducts.setItems(filteredProducts);
    }

    private void setupSearchFilter() {
        txtSearchProduct.setOnKeyReleased(event -> {
            onSearchProduct(event); // delega a filtragem

            // Se pressionar Enter, adiciona o produto selecionado
            if (event.getCode().toString().equals("ENTER")) {
                Product selectedProduct = tableProducts.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    onAddToCart();
                    // txtSearchProduct.clear(); // opcional: limpa o campo após adicionar
                }
            }
        });
    }

    private void initializeVisuals() {
        lblOperator.setText("João");
        lblTotal.setText("R$ 0.00");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            String formattedDateTime = LocalDateTime.now().format(formatter);
            lblDateTime.setText(formattedDateTime);
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void setupCartShortcuts() {
        tableCart.setOnKeyPressed(event -> {
            OrderItem selectedItem = tableCart.getSelectionModel().getSelectedItem();
            if (selectedItem == null)
                return;

            switch (event.getCode()) {
                case PLUS: // Tecla "+"
                case ADD: // Teclado numérico "+"
                    selectedItem.setQuantity(selectedItem.getQuantity() + 1);
                    break;

                case MINUS: // Tecla "-"
                case SUBTRACT: // Teclado numérico "-"
                    int newQty = selectedItem.getQuantity() - 1;
                    if (newQty <= 0) {
                        orderVM.getObservableItems().remove(selectedItem);
                    } else {
                        selectedItem.setQuantity(newQty);
                    }
                    break;

                case DELETE: // Remove item diretamente
                    orderVM.getObservableItems().remove(selectedItem);
                    break;

                case F5: // Finaliza a venda
                    onCheckout();
                    break;

                default:
                    break;
            }

            tableCart.refresh();
            updateTotal();
        });
    }

    private void setupGlobalShortcuts() {
        Platform.runLater(() -> {
            tableCart.getScene().setOnKeyPressed(event -> {
                KeyCode code = event.getCode();
                if (code == KeyCode.F5) {
                    onCheckout();
                } else if (code == KeyCode.ESCAPE) {
                    tableProducts.getSelectionModel().clearSelection();
                    tableCart.getSelectionModel().clearSelection();
                }
            });
        });
    }

}
