package com.philabid.ui.control;

import com.philabid.model.AuctionItem;

/**
 * A private wrapper class to control the text displayed in the autocompletion popup.
 * The text field will use the StringConverter, but the popup list will use this class's toString() method.
 */
public record AuctionItemSuggestion(AuctionItem item) {

    @Override
    public String toString() {
        return item().getCategoryCode() + " " + item().getCatalogNumber();
    }
}
