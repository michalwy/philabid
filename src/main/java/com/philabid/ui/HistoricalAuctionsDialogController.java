package com.philabid.ui;

import com.philabid.ui.control.ValuationDetails;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class HistoricalAuctionsDialogController {

    @FXML
    private ValuationDetails valuationDetails;

    private Stage dialogStage;

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