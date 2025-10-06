package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.model.AuctionHouse;
import com.philabid.model.AuctionStatus;
import com.philabid.model.Condition;
import com.philabid.ui.control.AuctionItemSelector;
import com.philabid.ui.control.MonetaryField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.UnaryOperator;

/**
 * Controller for the auction edit dialog.
 */
public class AuctionEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionEditDialogController.class);
    // Static field to remember the last used end date across dialog instances
    private static LocalDateTime lastUsedEndDate;
    // Static field to remember the last used auction house
    private static AuctionHouse lastUsedAuctionHouse;

    @FXML
    private ComboBox<AuctionHouse> auctionHouseComboBox;
    @FXML
    private AuctionItemSelector auctionItemSelector;
    @FXML
    private ComboBox<Condition> conditionComboBox;
    @FXML
    private TextField lotIdField;
    @FXML
    private TextField urlField;
    @FXML
    private MonetaryField priceField;
    @FXML
    private Button fetchUrlButton;
    @FXML
    private ComboBox<CurrencyUnit> currencyComboBox;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeField;
    @FXML
    private CheckBox archivedCheckBox;

    private Stage dialogStage;
    private Auction auction;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        logger.debug("AuctionEditDialogController initialized.");

        Platform.runLater(() -> urlField.requestFocus());

        // Add a listener to auto-trigger parsing when a URL is pasted
        urlField.textProperty().addListener((obs, oldVal, newVal) -> {
            // A simple heuristic to detect a paste of a URL
            if (newVal != null && !newVal.equals(oldVal) && newVal.trim().startsWith("http")) {
                handleFetchUrlData();
            }
        });

        // Auto-select currency when an auction house is chosen
        auctionHouseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getCurrency() != null) {
                currencyComboBox.getSelectionModel().select(newVal.getCurrency());
            }
        });

        // Enforce HH:mm format on the time field
        UnaryOperator<TextFormatter.Change> timeFilter = change -> {
            String newText = change.getControlNewText();
            // This regex allows partial input like "1", "12", "12:", "12:3", "12:34"
            if (newText.matches("^([01]?[0-9]|2[0-3])?(:([0-5]?[0-9])?)?$")) {
                return change;
            }
            // For invalid input, reject the change
            return null;
        };

        endTimeField.setTextFormatter(new TextFormatter<>(timeFilter));

        // Add a listener to auto-add colon ':' after two digits for hours
        endTimeField.textProperty().addListener((obs, oldVal, newVal) -> {
            // If user types 2 digits for the hour and there is no colon yet
            if (newVal.length() == 2 && oldVal.length() == 1 && !newVal.contains(":")) {
                // Check if the hour part is valid (00-23)
                try {
                    int hour = Integer.parseInt(newVal);
                    if (hour >= 0 && hour <= 23) {
                        endTimeField.setText(newVal + ":");
                    }
                } catch (NumberFormatException e) {
                    // Should not happen due to the filter, but good to have
                }
            }
        });

        // Set a custom format for the DatePicker to YYYY-MM-DD
        final String pattern = "yyyy-MM-dd";
        endDatePicker.setConverter(new StringConverter<>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        auctionItemSelector.load(AppContext.getI18nManager().getResourceBundle());

        populateComboBoxes();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;

        lotIdField.setText(auction.getLotId());
        urlField.setText(auction.getUrl());
        if (auction.getCurrentPrice() != null) {
            priceField.setAmount(auction.getCurrentPrice().originalAmount());
        }

        if (auction.getId() == null) { // It's a new auction
            // Use the last saved date/time, or the current date/time if none exists
            LocalDateTime defaultDateTime = lastUsedEndDate != null ? lastUsedEndDate : LocalDateTime.now();
            endDatePicker.setValue(defaultDateTime.toLocalDate());
            endTimeField.setText(defaultDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

            // Pre-select the last used auction house
            if (lastUsedAuctionHouse != null) {
                auctionHouseComboBox.getSelectionModel().select(lastUsedAuctionHouse);
            }

            priceField.setText("1.00");
        } else { // It's an existing auction
            // Set date and time from the existing auction object
            endDatePicker.setValue(auction.getEndDate().toLocalDate());
            endTimeField.setText(auction.getEndDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        // Select items in ComboBoxes
        if (auction.getAuctionHouseId() != null) {
            auctionHouseComboBox.getSelectionModel()
                    .select(AppContext.getAuctionHouseService().getAuctionHouseById(auction.getAuctionHouseId())
                            .orElse(null));
        }
        if (auction.getAuctionItemId() != null) {
            AppContext.getAuctionItemService().getAuctionItemById(auction.getAuctionItemId())
                    .ifPresent(auctionItemSelector::setSelectedAuctionItem);
        }
        selectComboBoxValue(conditionComboBox, auction.getConditionId());
        if (auction.getCurrentPrice() != null) {
            selectComboBoxValue(currencyComboBox,
                    auction.getCurrentPrice().originalAmount().getCurrency().getCurrencyCode());
        }
        archivedCheckBox.setSelected(auction.isArchived());
    }

    private void populateComboBoxes() {
        auctionHouseComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getAuctionHouseService().getAllAuctionHouses()));
        conditionComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getConditionService().getAllConditions()));
        currencyComboBox.setItems(FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));
    }

    private <T> void selectComboBoxValue(ComboBox<T> comboBox, Object id) {
        if (id == null || comboBox.getItems() == null) return;
        comboBox.getItems().stream()
                .filter(item -> {
                    if (item instanceof Condition) return ((Condition) item).getId().equals(id);
                    if (item instanceof CurrencyUnit) return ((CurrencyUnit) item).getCurrencyCode().equals(id);
                    if (item instanceof AuctionStatus) return item.equals(id);
                    if (item instanceof AuctionHouse) return ((AuctionHouse) item).getId().equals(id);
                    return false;
                })
                .findFirst()
                .ifPresent(comboBox.getSelectionModel()::select);
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            AuctionHouse selectedAuctionHouse = auctionHouseComboBox.getValue();
            auction.setAuctionHouseId(selectedAuctionHouse.getId());
            auction.setAuctionItemId(auctionItemSelector.resolveAuctionItemId());
            auction.setConditionId(conditionComboBox.getValue().getId());
            auction.setLotId(lotIdField.getText());
            auction.setUrl(urlField.getText());
            auction.setCurrentPrice(Money.of(priceField.getAmount(), currencyComboBox.getValue()));

            LocalDate localDate = endDatePicker.getValue();
            LocalDateTime newEndDate = null;
            if (localDate != null) {
                LocalTime localTime = LocalTime.MIDNIGHT; // Default time
                if (endTimeField.getText() != null && !endTimeField.getText().isBlank()) {
                    try {
                        // Use a formatter to be more robust
                        localTime = LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                    } catch (DateTimeParseException e) {
                        // This case is handled by isInputValid(), but it's good practice
                        logger.warn("Could not parse time, defaulting to midnight: {}", endTimeField.getText());
                    }
                }
                newEndDate = LocalDateTime.of(localDate, localTime);
                auction.setEndDate(newEndDate);
            }
            auction.setArchived(archivedCheckBox.isSelected());

            // Remember the date for the next time
            lastUsedEndDate = newEndDate;
            lastUsedAuctionHouse = selectedAuctionHouse;

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (auctionHouseComboBox.getSelectionModel().getSelectedItem() == null)
            errorMessage += "Auction House is required.\n";
        if (auctionItemSelector.getText() == null || auctionItemSelector.getText().isBlank()) {
            errorMessage += "Catalog Number cannot be empty!\n";
        }
        if (auctionItemSelector.getSelectedAuctionItem() == null && auctionItemSelector.getSelectedCategory() == null) {
            errorMessage += "Category must be selected for a new catalog number!\n";
        }
        if (conditionComboBox.getValue() == null) errorMessage += "Condition is required.\n";

        // Validate time format if provided
        if (endTimeField.getText() != null && !endTimeField.getText().isBlank()) {
            try {
                LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                errorMessage += "Invalid time format. Please use HH:mm.\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    private void handleFetchUrlData() {
        String url = urlField.getText();
        if (url == null || url.isBlank()) {
            logger.warn("Fetch URL data button clicked, but URL is empty.");
            return;
        }
        logger.info("Fetching data from URL: {}", url);

        AppContext.getUrlParsingService().parseUrl(url).ifPresent(data -> {
            Platform.runLater(() -> {
                if (data.auctionHouse() != null) {
                    auctionHouseComboBox.getSelectionModel().select(data.auctionHouse());
                }
                lotIdField.setText(data.lotId());
                if (data.currentPrice() != null) {
                    priceField.setAmount(data.currentPrice());
                    currencyComboBox.getSelectionModel().select(data.currentPrice().getCurrency());
                }
                if (data.closingDate() != null) {
                    endDatePicker.setValue(data.closingDate().toLocalDate());
                    endTimeField.setText(data.closingDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                }
                // After successfully populating, move focus to the next logical field
                auctionItemSelector.requestFocus();
                logger.info("Fields populated from URL data.");
            });
        });
    }
}
