package com.example.gui.util;

import java.text.NumberFormat;
import java.util.Locale;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class Utils {

    public static String formatCurrencyBR(double value, int decimalPlaces) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        currencyFormat.setMinimumFractionDigits(decimalPlaces);
        currencyFormat.setMaximumFractionDigits(decimalPlaces);
        return currencyFormat.format(value);
    }

    public static <T> void formatTableColumnCurrencyBR(TableColumn<T, Double> tableColumn, int decimalPlaces) {
        tableColumn.setCellFactory(column -> {
            TableCell<T, Double> cell = new TableCell<T, Double>() {
                private final NumberFormat currencyFormat;

                {
                    currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                    currencyFormat.setMinimumFractionDigits(decimalPlaces);
                    currencyFormat.setMaximumFractionDigits(decimalPlaces);
                }

                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : currencyFormat.format(item));
                }
            };
            return cell;
        });
    }

}
