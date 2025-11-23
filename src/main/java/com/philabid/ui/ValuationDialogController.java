package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Valuation;
import com.philabid.ui.control.ValuationDetails;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ValuationDialogController {

    private static final Logger logger = LoggerFactory.getLogger(ValuationDialogController.class);

    @FXML
    private ValuationDetails valuationDetails;

    private Stage dialogStage;

    public static void showValuationDialog(Window owner, Long tradingItemId, Long conditionId) {
        Valuation valuation = AppContext.getValuationService().getForItem(tradingItemId, conditionId)
                .orElse(new Valuation());
        showValuationDialog(owner, valuation);
    }

    public static void showValuationDialog(Window owner, Valuation valuation) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(ValuationViewController.class.getResource("/fxml/ValuationDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Trading Item Valuation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.setScene(new Scene(page));

            ValuationDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTradingItem(valuation.getTradingItemId(), valuation.getConditionId());

            dialogStage.showAndWait();
        } catch (IOException e) {
            logger.error("Failed to load Archive Auction dialog.", e);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTradingItem(Long tradingItemId, Long conditionId) {
        valuationDetails.setTradingItem(tradingItemId, conditionId);
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}