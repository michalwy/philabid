package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Catalog management view (CatalogView.fxml).
 */
public class CatalogController extends SimpleCrudTableViewController<Catalog> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @FXML
    private TableColumn<Catalog, String> nameColumn;
    @FXML
    private TableColumn<Catalog, Integer> issueYearColumn;
    @FXML
    private TableColumn<Catalog, String> currencyColumn;
    @FXML
    private TableColumn<Catalog, Boolean> activeColumn;

    public CatalogController() {
        super(AppContext.getCatalogService());
    }

    @Override
    protected void initializeView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        issueYearColumn.setCellValueFactory(new PropertyValueFactory<>("issueYear"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Custom cell factory for the 'active' column to display a checkmark
        activeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("✓"); // Checkmark character
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        setAlignment(Pos.CENTER);
                    } else {
                        setText(null);
                        setStyle("");
                    }
                }
            }
        });

        logger.debug("CatalogController initialized.");
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/CatalogEditDialog.fxml";
    }
}
