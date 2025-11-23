package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;

import java.util.Collection;
import java.util.List;

import static com.philabid.ui.ValuationDialogController.showValuationDialog;

public class ArchivedAuctionController extends BaseAuctionController {

    @Override
    protected void initializeView() {
        super.initializeView();
        Platform.runLater(() -> {
            endDateColumn.setSortType(TableColumn.SortType.DESCENDING);
            getTableView().getSortOrder().setAll(List.of(endDateColumn));
        });
    }

    @Override
    public Collection<Auction> loadAuctions(Collection<FilterCondition> filterConditions) {
        return AppContext.getAuctionService().getArchivedAuctions(filterConditions);
    }

    @Override
    protected void handleDoubleClick() {
        Auction auction = getTableView().getSelectionModel().getSelectedItem();
        if (auction == null) {
            return;
        }
        showValuationDialog(getTableView().getScene().getWindow(), auction.getTradingItemId(),
                auction.getConditionId());
    }
}