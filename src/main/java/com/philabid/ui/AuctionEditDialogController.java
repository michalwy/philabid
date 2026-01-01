package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.*;
import com.philabid.ui.control.MonetaryField;
import com.philabid.ui.control.TradingItemSelector;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Controller for the auction edit dialog.
 */
public class AuctionEditDialogController extends CrudEditDialogController<Auction> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionEditDialogController.class);
    // Static field to remember the last used end date across dialog instances
    private static LocalDateTime lastUsedEndDate;
    // Static field to remember the last used auction house
    private static AuctionHouse lastUsedAuctionHouse;

    private static Seller lastUsedSeller;

    private final Collection<Seller> allSellers = AppContext.getSellerService().getAll();
    @FXML
    private ComboBox<AuctionHouse> auctionHouseComboBox;
    @FXML
    private TradingItemSelector tradingItemSelector;
    @FXML
    private ComboBox<Condition> conditionComboBox;
    @FXML
    private TextField lotIdField;
    @FXML
    private TextField sellerField;
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
    @FXML
    private Label multipleAuctionsWarningLabel;
    @FXML
    private Label auctionExistsWarningLabel;
    private boolean saveClicked = false;
    private boolean initiallyArchived;

    private Seller selectedSeller;

    @Override
    protected void initContent() {
        logger.debug("AuctionEditDialogController initialized.");

        Platform.runLater(() -> urlField.requestFocus());

        // Add a listener to auto-trigger parsing when a URL is pasted
        urlField.textProperty().addListener((obs, oldVal, newVal) -> {
            // A simple heuristic to detect a paste of a URL
            if (getEntity() != null && getEntity().getId() == null && newVal != null && !newVal.equals(oldVal) &&
                    newVal.trim().startsWith("http")) {
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

        conditionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            verifyActiveAuctions();
        });

        tradingItemSelector.selectedTradingItemProperty().addListener((obs, oldVal, newVal) -> {
            verifyActiveAuctions();
        });

        auctionHouseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            verifyAuctionExists();
        });

        lotIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            verifyAuctionExists();
        });

        setupSellerAutocomplete();

        populateComboBoxes();
    }

    private void setupSellerAutocomplete() {
        sellerField.textProperty().addListener((obs, oldVal, newVal) -> handleSellerTextChange(newVal));

        AutoCompletionBinding<Seller> binding = TextFields.bindAutoCompletion(sellerField,
                suggestionRequest -> {
                    String filter = suggestionRequest.getUserText().toLowerCase();
                    Stream<Seller> sellers = allSellers.stream();

                    String[] tokens = filter.trim().split("\\s+");
                    for (String token : tokens) {
                        sellers = sellers.filter(s -> s.getName().toLowerCase().contains(token) ||
                                (s.getFullName() != null && s.getFullName().toLowerCase().contains(token)));
                    }

                    List<Seller> s = sellers.toList();
                    logger.info("Sellers found: {}", s);
                    return s;
                });

        binding.setOnAutoCompleted(event -> {
            selectedSeller = event.getCompletion();
            sellerField.setText(selectedSeller.getName());
        });
    }

    private void handleSellerTextChange(String newText) {
        if (selectedSeller != null && (newText == null || !newText.equals(selectedSeller.getName()))) {
            selectedSeller = null;
        }
    }

    private Long resolveSellerId() {
        if (selectedSeller != null) {
            return selectedSeller.getId();
        } else if (!sellerField.getText().isBlank()) {
            Seller newSeller = new Seller();
            newSeller.setName(sellerField.getText());
            AppContext.getSellerService().save(newSeller);
            logger.info("Created new seller with ID: {}", newSeller.getId());
            return newSeller.getId();
        } else {
            return null;
        }
    }

    private void populateComboBoxes() {
        auctionHouseComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getAuctionHouseService().getAll()));
        conditionComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getConditionService().getAll()));
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

    @Override
    public void loadEntity(Auction auction) {
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

            if (lastUsedSeller != null) {
                selectedSeller = lastUsedSeller;
                sellerField.setText(selectedSeller.getName());
            }
        } else { // It's an existing auction
            // Set date and time from the existing auction object
            endDatePicker.setValue(auction.getEndDate().toLocalDate());
            endTimeField.setText(auction.getEndDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

            if (auction.getSellerId() != null) {
                AppContext.getSellerService().getById(auction.getSellerId()).ifPresent(s -> {
                    selectedSeller = s;
                    sellerField.setText(selectedSeller.getName());
                });
            }
        }

        // Select items in ComboBoxes
        if (auction.getAuctionHouseId() != null) {
            auctionHouseComboBox.getSelectionModel()
                    .select(AppContext.getAuctionHouseService().getById(auction.getAuctionHouseId())
                            .orElse(null));
        }
        if (auction.getTradingItemId() != null) {
            AppContext.getTradingItemService().getById(auction.getTradingItemId())
                    .ifPresent(tradingItemSelector::setSelectedTradingItem);
        }
        selectComboBoxValue(conditionComboBox, auction.getConditionId());
        if (auction.getCurrentPrice() != null) {
            selectComboBoxValue(currencyComboBox,
                    auction.getCurrentPrice().originalAmount().getCurrency().getCurrencyCode());
        }
        archivedCheckBox.setSelected(auction.isArchived());
        initiallyArchived = auction.isArchived();

        verifyActiveAuctions();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    protected void updateEntity(Auction auction) {
        AuctionHouse selectedAuctionHouse = auctionHouseComboBox.getValue();
        auction.setAuctionHouseId(selectedAuctionHouse.getId());
        auction.setSellerId(resolveSellerId());
        auction.setTradingItemId(tradingItemSelector.resolveTradingItemId());
        auction.setConditionId(conditionComboBox.getValue().getId());
        auction.setLotId(lotIdField.getText());
        auction.setUrl(urlField.getText());
        BigDecimal currentPrice = priceField.getAmount();
        auction.setCurrentPrice(currentPrice != null ? Money.of(currentPrice, currencyComboBox.getValue()) : null);

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
        if (!initiallyArchived && archivedCheckBox.isSelected() && auction.getCatalogValue() != null) {
            auction.setArchivedCatalogValue(auction.getCatalogValue());
        }
        auction.setArchived(archivedCheckBox.isSelected());

        // Remember the date for the next time
        lastUsedEndDate = newEndDate;
        lastUsedAuctionHouse = selectedAuctionHouse;
        lastUsedSeller = selectedSeller;

        saveClicked = true;
    }

    @Override
    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (auctionHouseComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError("Auction House is required.", auctionHouseComboBox));
        }
        if (tradingItemSelector.getText() == null || tradingItemSelector.getText().isBlank()) {
            errors.add(new ValidationError("Catalog Number is required.", tradingItemSelector));
        }
        if (tradingItemSelector.getSelectedTradingItem() == null && tradingItemSelector.getSelectedCategory() == null) {
            errors.add(new ValidationError("Category must be selected for a new catalog number.", tradingItemSelector));
        }
        if (conditionComboBox.getValue() == null) {
            errors.add(new ValidationError("Condition is required.", conditionComboBox));
        }

        if (endTimeField.getText() != null && !endTimeField.getText().isBlank()) {
            try {
                LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                errors.add(new ValidationError("Invalid time format. Please use HH:mm.", endTimeField));
            }
        }

        return errors;
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
                tradingItemSelector.requestFocus();
                logger.info("Fields populated from URL data.");
            });
        });
    }

    private void verifyActiveAuctions() {
        TradingItem tradingItem = tradingItemSelector.selectedTradingItemProperty().getValue();
        Condition condition = conditionComboBox.getSelectionModel().getSelectedItem();

        if (tradingItem == null || condition == null) {
            multipleAuctionsWarningLabel.setVisible(false);
            return;
        }

        Collection<Auction> auctions =
                AppContext.getAuctionService().getActiveAuctionsForItem(tradingItem.getId(), condition.getId());

        multipleAuctionsWarningLabel.setVisible(
                auctions.stream().anyMatch(a -> !a.getId().equals(getEntity().getId())));
    }

    private void verifyAuctionExists() {
        String lotId = lotIdField.getText();
        AuctionHouse auctionHouse = auctionHouseComboBox.getSelectionModel().getSelectedItem();

        if (!Objects.isNull(auctionHouse)) {
            boolean exists =
                    AppContext.getAuctionService().getByAuctionHouseAndLotId(auctionHouse.getId(), lotId).stream()
                            .anyMatch(a -> !Objects.equals(a.getId(), getEntity().getId()));
            auctionExistsWarningLabel.setVisible(exists);
        } else {
            auctionExistsWarningLabel.setVisible(false);
        }
    }
}
