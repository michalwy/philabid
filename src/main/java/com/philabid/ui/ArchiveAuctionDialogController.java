package com.philabid.ui;

import com.philabid.model.Auction;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

public class ArchiveAuctionDialogController {

    @FXML
    private TextField finalPriceField;

    private Stage dialogStage;
    private Auction auction;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        // Add a listener to enforce numeric input
        finalPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\d*([.,]?\\d*)?$")) {
                finalPriceField.setText(oldValue);
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
        // Pre-fill with the current price
        if (auction.getCurrentPrice() != null) {
            finalPriceField.setText(auction.getCurrentPrice().getNumber().toString());
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            String priceText = finalPriceField.getText().replace(',', '.');
            BigDecimal finalAmount = new BigDecimal(priceText);
            // Update the auction's price and archive it
            auction.setCurrentPrice(Money.of(finalAmount, auction.getCurrentPrice().getCurrency()));
            auction.setArchived(true);

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        if (finalPriceField.getText() == null || finalPriceField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Field");
            alert.setHeaderText("Final price cannot be empty.");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}