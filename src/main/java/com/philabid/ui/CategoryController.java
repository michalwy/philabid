package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Category;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Category management view (CategoryView.fxml).
 */
public class CategoryController extends SimpleCrudTableViewController<Category> {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> codeColumn;
    @FXML
    private TableColumn<Category, String> catalogColumn;

    public CategoryController() {
        super(AppContext.getCategoryService());
    }

    @Override
    protected void initializeView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        // Custom cell value factory to display formatted catalog name and year
        catalogColumn.setCellValueFactory(cellData -> {
            Category category = cellData.getValue();
            return new SimpleStringProperty(String.format("%s (%d)", category.getCatalogName(),
                    category.getCatalogIssueYear()));
        });
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/CategoryEditDialog.fxml";
    }
}
