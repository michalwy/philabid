package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;

import java.util.Collection;
import java.util.List;

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
}